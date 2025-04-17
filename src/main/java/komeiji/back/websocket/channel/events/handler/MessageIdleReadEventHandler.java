package komeiji.back.websocket.channel.events.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import komeiji.back.websocket.WebSocketServer;
import komeiji.back.websocket.channel.Attributes;
import komeiji.back.websocket.utils.Utils;

public class MessageIdleReadEventHandler implements EventHandler{
    private IdleStateEvent lastEvent = null;

    public MessageIdleReadEventHandler(Object evt) {
        if(evt instanceof IdleStateEvent event && event.state() == IdleState.READER_IDLE) {
            lastEvent = event;
        }
    }


    @Override
    public boolean valid() {
        return lastEvent != null;
    }

    @Override
    public void handlerEvent(ChannelHandlerContext ctx) {
        Long lastActive = ctx.channel().attr(Attributes.LAST_IDLE_TIME).get();
        long now = System.currentTimeMillis();
        if(lastActive == null || now -lastActive >= WebSocketServer.IDLE_TIME) {
            System.out.println("invoke idle event");
            ctx.channel().attr(Attributes.TIMEOUT).set(true);
            Utils.closeChannelWithoutResponse(ctx.channel(),4001,"time out close");
        }
    }
}
