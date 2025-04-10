package komeiji.back.websocket.channel.handlers;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import komeiji.back.entity.User;
import komeiji.back.repository.UserDao;
import komeiji.back.websocket.WebSocketServer;
import komeiji.back.websocket.channel.Attributes;
import komeiji.back.websocket.channel.events.handler.EventHandler;
import komeiji.back.websocket.channel.events.handler.HandShakeCompleteEventHandler;
import komeiji.back.websocket.persistence.Conversation;
import komeiji.back.websocket.persistence.ConversationManager;
import komeiji.back.websocket.persistence.impl.MockRecordstorage;
import komeiji.back.websocket.persistence.impl.RedisRecordstorage;
import komeiji.back.websocket.protocol.ProtocolUtils;
import komeiji.back.websocket.protocol.WebSocketFrameProtocol;
import komeiji.back.websocket.session.OneWayChatSession;
import komeiji.back.websocket.session.Session;
import komeiji.back.websocket.session.SessionToken;

import komeiji.back.utils.RedisUtils;
import komeiji.back.utils.RedisTable;
import komeiji.back.utils.BeanUtils;

import komeiji.back.websocket.persistence.ConversationUtils;

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
        closePeerChannel(session.getId(),session.getTarget());


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

    private void closePeerChannel(SessionToken selfToken,SessionToken peerToken) {
        Session peer = WebSocketServer.getWebSocketSingleServer().getSessionManager().findChatSession(peerToken);
        if(peer == null) {
            return;
        }
        assert peer.getTarget().equals(selfToken);
        Channel peerChannel = peer.getConnect();
        peerChannel.close();
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
            //NOTICE 处理conversation的逻辑 将CID对应的记录删除
            String CID = ConversationUtils.sessionTokens2CID(new SessionToken(id),new SessionToken(target)).toString();
            if(!redisUtils.hasHashKey(RedisTable.SessionToUser,CID)){
                return;
            }
            else{
                Map<String,String> map = (Map<String,String>)redisUtils.getHash(RedisTable.SessionToUser,CID);
                String patient_name = map.get("patient");
                if(redisUtils.hasHashKey(RedisTable.UserToSession,patient_name)){
                    redisUtils.deleteHash(RedisTable.UserToSession,patient_name);
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
