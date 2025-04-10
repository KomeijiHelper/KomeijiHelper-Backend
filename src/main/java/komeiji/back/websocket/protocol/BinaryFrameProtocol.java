package komeiji.back.websocket.protocol;


import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import komeiji.back.websocket.message.Message;
import komeiji.back.websocket.session.Session;

public abstract class BinaryFrameProtocol<T extends Message> implements WebSocketFrameProtocol<T> {
    protected abstract T frameTransform(BinaryWebSocketFrame frame, Session session);

    @Override
    public T transform(Object data, Session session) {
        if(!(data instanceof BinaryWebSocketFrame frame)) {
            throw new ClassCastException();
        }
        return frameTransform(frame,session);
    }
}
