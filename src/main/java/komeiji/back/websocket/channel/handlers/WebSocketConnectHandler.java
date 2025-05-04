package komeiji.back.websocket.channel.handlers;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import komeiji.back.entity.User;
import komeiji.back.entity.UserClass;
import komeiji.back.repository.UserDao;
import komeiji.back.utils.BeanUtils;
import komeiji.back.utils.RedisTable;
import komeiji.back.utils.RedisUtils;
import komeiji.back.websocket.WebSocketServer;
import komeiji.back.websocket.channel.Attributes;
import komeiji.back.websocket.channel.events.handler.EventHandler;
import komeiji.back.websocket.channel.events.handler.HandShakeCompleteEventHandler;
import komeiji.back.websocket.persistence.Conversation;
import komeiji.back.websocket.persistence.ConversationUtils;
import komeiji.back.websocket.persistence.impl.RedisRecordstorage;
import komeiji.back.websocket.session.OneWayChatSession;
import komeiji.back.websocket.session.Session;
import komeiji.back.websocket.session.SessionToken;
import komeiji.back.websocket.utils.Utils;

import java.util.Map;

public class WebSocketConnectHandler extends ChannelInboundHandlerAdapter {

    private Session session = null;
    private RedisUtils redisUtils = BeanUtils.getBean(RedisUtils.class);
    private UserDao userdao = BeanUtils.getBean(UserDao.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // disconnect
        assert session != null;
        Session session = ctx.channel().attr(Attributes.SESSION).get();
        System.out.println("channelDisconnect from: " + session.getId());
        WebSocketServer.getWebSocketSingleServer().getSessionManager().removeSession(session);
        //TODO 在咨询会话关闭时 将SessionToUser表中 CID对应的patient字段 调用offLind函数
        offLine(session.getId().toString(),session.getTarget().toString());
        if(ctx.channel().attr(Attributes.TIMEOUT).get() != null) {
            closePeerChannel(session.getId(),session.getTarget(),4001,"time out close");
        }
        else{
            closePeerChannel(session.getId(),session.getTarget(),4000,"close by peer");
        }

        super.channelInactive(ctx);
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // connect
        //TODO 当咨询师在工作台界面时，在Redis中添加online*******代表在线用户 通过用户的不同身份

        EventHandler eventHandler = new HandShakeCompleteEventHandler(evt);
        if(eventHandler.valid() && session == null) {
            eventHandler.handlerEvent(ctx);
            session = ctx.channel().attr(Attributes.SESSION).get();
            System.out.println("_____________cjw print in userEventTriggered:________________"+session.getId().toString());
            //TODO 调用onLine方法 将在线用户加入Redis
            onLine(session.getId().toString());
            buildConversation(ctx);
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exceptionCaught: " + cause.getMessage() + WebSocketConnectHandler.class.getName());
        super.exceptionCaught(ctx, cause);
    }

    private void closePeerChannel(SessionToken selfToken,SessionToken peerToken,int code, String reason) {
        Session peer = WebSocketServer.getWebSocketSingleServer().getSessionManager().findChatSession(peerToken);
        if(peer == null) {
            return;
        }
        assert peer.getTarget().equals(selfToken);
        Channel peerChannel = peer.getConnect();
        Utils.closeChannelWithoutResponse(peerChannel,code,reason);
    }

    private void buildConversation(ChannelHandlerContext ctx){
        if(session == null | !(session instanceof OneWayChatSession)) {
            return;
        }
        Conversation conversation = WebSocketServer.getWebSocketSingleServer().
                getConversationManager().newConversation(session.getId(),session.getTarget(),new RedisRecordstorage());
        conversation.tryStart();
        ctx.channel().attr(Attributes.CONVERSATION).set(conversation);
    }

    private void offLine(String id,String target){
        if(id == target){
            //NOTICE 处理离线逻辑
            User onlineUesr = userdao.findByUserName(id);
            switch(onlineUesr.getUserClass()){
                case Normal:
                    redisUtils.deleteHash(RedisTable.onlineNormal,id);
                    break;
                case Assistant:
                    redisUtils.deleteHash(RedisTable.onlineConsultant,id);
                    break;
                case Supervisor:
                    redisUtils.deleteHash(RedisTable.onlineSupervisor,id);
                    break;
            }
            return;
        }
        else{
            //TODO 要对普通患者和咨询师的求助进行区分
            //NOTICE 处理conversation的逻辑 将CID对应的记录删除

            String CID = ConversationUtils.sessionTokens2CID(new SessionToken(id),new SessionToken(target)).toString();
            if(!redisUtils.hasHashKey(RedisTable.SessionToUser,CID)){
                return;
            }
            else{
                //NOTICE 聊天结束后 将CID对应的consultant_name去除 patient_name需要在后续的评分中
                Map<String,String> map = (Map<String,String>)redisUtils.getHash(RedisTable.SessionToUser,CID);
                String patient_name = map.get("patient");
                String consultant_name = map.get("consultant");
                User patient = userdao.findByUserName(patient_name);
                if(patient.getUserClass() == UserClass.Normal){
                    //NOTICE 普通用户需要添加一个PatientTempScore (有过期时间)
                    if(redisUtils.hasHashKey(RedisTable.UserToSession,patient_name)){
                        redisUtils.deleteHash(RedisTable.UserToSession,consultant_name);
                    }
                    if(redisUtils.hasHashKey(RedisTable.UserToHelpSession,consultant_name)){
                        redisUtils.deleteHash(RedisTable.UserToHelpSession,consultant_name);
                    }
                    redisUtils.set(RedisTable.PatientTempScore+patient_name,CID,300);//NOTICE 在五分钟内可以进行评分 超时后只能在历史记录中进行评分
                }
                else{
                    redisUtils.deleteHash(RedisTable.UserToHelpSession,patient_name);
                    redisUtils.deleteHash(RedisTable.UserToHelpSession,consultant_name);
                }
                redisUtils.deleteHash(RedisTable.SessionToUser,CID);
            }

        }

        return;
    }
    private void onLine(String id){
        User onlineUser = userdao.findByUserName(id);
        if(onlineUser == null){return;}
        onlineUser.setPassword("");
        switch(onlineUser.getUserClass()){
            case Normal:
                redisUtils.addHash(RedisTable.onlineNormal,onlineUser.getUserName(),onlineUser);
                break;
            case Assistant:
                redisUtils.addHash(RedisTable.onlineConsultant,onlineUser.getUserName(),onlineUser);
                break;
            case Supervisor:
                redisUtils.addHash(RedisTable.onlineSupervisor,onlineUser.getUserName(),onlineUser);
                break;
        }
        return;
    }
}
