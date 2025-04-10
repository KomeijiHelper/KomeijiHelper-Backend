package komeiji.back.websocket.channel.handlers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import komeiji.back.websocket.WebSocketServer;
import komeiji.back.websocket.channel.Attributes;
import komeiji.back.websocket.channel.events.MessageForwardEvent;
import komeiji.back.websocket.message.MessageType;
import komeiji.back.websocket.message.impl.TextMessage;
import komeiji.back.websocket.persistence.MessageRecord;
import komeiji.back.websocket.session.Session;
import komeiji.back.websocket.session.SessionToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextMessageHandler extends SimpleChannelInboundHandler<TextMessage> {
    private static final Logger logger = LoggerFactory.getLogger(TextMessageHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,TextMessage msg) throws Exception {
        // NOTE: message has been parsed by Protocol here
        logger.debug("handler text message: {}",msg);
        ctx.fireUserEventTriggered(new MessageForwardEvent(msg));
        MessageRecord msgRecord = new MessageRecord(msg.getFrom(),msg.getTimestamp(),"text", msg.getData());
        ctx.fireChannelRead(msgRecord);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerAdded 被调用" + ctx.channel().id().asLongText());
        System.out.println("handlerAdded 被调用" + ctx.channel().id().asShortText());
    }

//    @Override
//    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("handlerRemoved 被调用" + ctx.channel().id().asLongText());
//    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("异常发生 {} in {} ", cause.getMessage(), TextMessageHandler.class.getName());
        super.exceptionCaught(ctx, cause);
    }
}