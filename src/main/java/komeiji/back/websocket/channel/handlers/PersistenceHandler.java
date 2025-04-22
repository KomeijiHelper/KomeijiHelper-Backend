package komeiji.back.websocket.channel.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import komeiji.back.websocket.WebSocketServer;
import komeiji.back.websocket.channel.Attributes;
import komeiji.back.websocket.message.Message;
import komeiji.back.websocket.persistence.Conversation;
import komeiji.back.websocket.persistence.MessageRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistenceHandler extends SimpleChannelInboundHandler<MessageRecord> {
    private static final Logger logger = LoggerFactory.getLogger(PersistenceHandler.class);

    private Conversation conversation = null;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx,Object evt) throws Exception {
        if (!(evt instanceof WebSocketServerProtocolHandler.HandshakeComplete)) {
            super.userEventTriggered(ctx, evt);
            return;
        }
        conversation = ctx.channel().attr(Attributes.CONVERSATION).get();
        // this channel is not a conversation channel, remove this handler
        if (conversation == null) {
            ctx.pipeline().remove(this);
            logger.info("channel is not a conversation channel");
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (conversation != null) {
            WebSocketServer.getWebSocketSingleServer().getConversationManager().closeConversation(conversation);
        }
        super.channelInactive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageRecord record) throws Exception {
        conversation.storeRecord(record);
        ctx.fireChannelRead(record);
    }
}
