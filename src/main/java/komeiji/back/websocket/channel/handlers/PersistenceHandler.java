package komeiji.back.websocket.channel.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import komeiji.back.websocket.channel.Attributes;
import komeiji.back.websocket.message.Message;
import komeiji.back.websocket.persistence.Conversation;
import komeiji.back.websocket.persistence.MessageRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistenceHandler extends SimpleChannelInboundHandler<Message> {
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
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
//        System.out.println("Conversation of msg " + msg + " is " + conversation.getCID());
        MessageRecord record = new MessageRecord(msg);
        conversation.storeRecord(record);
        ctx.fireChannelRead(msg);
    }
}
