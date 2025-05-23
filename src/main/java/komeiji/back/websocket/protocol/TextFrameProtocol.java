package komeiji.back.websocket.protocol;

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import komeiji.back.websocket.message.Message;
import komeiji.back.websocket.session.Session;

public abstract class TextFrameProtocol<T extends Message> implements WebSocketFrameProtocol<T> {

    protected abstract T frameTransform(TextWebSocketFrame frame, Session session);

    @Override
    public T transform(Object data, Session session) throws ClassCastException {
        if(!(data instanceof TextWebSocketFrame frame)) {
            throw new ClassCastException();
        }
        return frameTransform(frame,session);
    }
}
