package komeiji.back.websocket.protocol.impl;


import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import komeiji.back.websocket.message.Message;
import komeiji.back.websocket.protocol.BinaryFrameProtocol;
import komeiji.back.websocket.session.Session;

public class DefaultBinaryFrameProtocol extends BinaryFrameProtocol<Message> {
    @Override
    protected Message frameTransform(BinaryWebSocketFrame frame, Session session) {
        return null;
    }
}
