package komeiji.back.websocket.channel.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import komeiji.back.websocket.channel.Attributes;
import komeiji.back.websocket.channel.events.handler.EventHandler;
import komeiji.back.websocket.channel.events.handler.MessageIdleReadEventHandler;
import komeiji.back.websocket.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageIdleReadHandler extends SimpleChannelInboundHandler<Message> {

    private static final Logger logger = LoggerFactory.getLogger(MessageIdleReadHandler.class);
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            if (ctx.channel().attr(Attributes.CONVERSATION).get() == null) {
                ctx.pipeline().remove(this);
                logger.info("channel is not a conversation channel,remove {}",MessageIdleReadHandler.class.getSimpleName());
                return;
            }
        }
        EventHandler idleHandle = new MessageIdleReadEventHandler(evt);
        if(idleHandle.valid()) {
            idleHandle.handlerEvent(ctx);
            return;
        }
        super.userEventTriggered(ctx, evt);
    }

    private void updateLastIdleTime(ChannelHandlerContext ctx){
        ctx.channel().attr(Attributes.LAST_IDLE_TIME).set(System.currentTimeMillis());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
        System.out.println("called messageIdleReadHandler");
        updateLastIdleTime(ctx);
        ctx.fireChannelRead(message);
    }
}
