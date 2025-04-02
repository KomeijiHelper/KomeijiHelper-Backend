package komeiji.back.websocket.message.impl;

import com.google.gson.Gson;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import komeiji.back.websocket.session.SessionToken;

import java.util.Map;

public class ChatConnectMessage extends TextMessage{
    // TODO:
    public ChatConnectMessage(SessionToken from, SessionToken to, long timestamp) {
        super(from, to, timestamp);
    }

    public ChatConnectMessage(SessionToken from, SessionToken to, String data, long timestamp) {
        super(from, to, data, timestamp);
    }

    @Override
    public TextWebSocketFrame messageDecode() {
        return new TextWebSocketFrame(gson.toJson(Map.of("type","chat_connect","content",data)));
    }
}
