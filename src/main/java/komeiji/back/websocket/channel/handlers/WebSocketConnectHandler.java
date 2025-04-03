package komeiji.back.websocket.channel.handlers;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
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

public class WebSocketConnectHandler extends ChannelInboundHandlerAdapter {

    private Session session = null;

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
        closePeerChannel(session.getId(),session.getTarget());
        super.channelInactive(ctx);
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // connect
        EventHandler eventHandler = new HandShakeCompleteEventHandler(evt);
        if(eventHandler.valid() && session == null) {
            eventHandler.handlerEvent(ctx);
            session = ctx.channel().attr(Attributes.SESSION).get();
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
}
