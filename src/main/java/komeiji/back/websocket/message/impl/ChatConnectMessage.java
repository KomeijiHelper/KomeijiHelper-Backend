package komeiji.back.websocket.message.impl;

import com.google.gson.Gson;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import komeiji.back.websocket.session.SessionToken;

import java.util.Map;

public class ChatConnectMessage extends TextMessage{
    // TODO:
    public ChatConnectMessage(SessionToken from, SessionToken to) {
        super(from, to);
    }

    public ChatConnectMessage(SessionToken from, SessionToken to, String data) {
        super(from, to, data);
    }

    @Override
    public TextWebSocketFrame messageDecode() {
        return new TextWebSocketFrame(gson.toJson(Map.of("type","chat_connect","content",data)));
    }
}
