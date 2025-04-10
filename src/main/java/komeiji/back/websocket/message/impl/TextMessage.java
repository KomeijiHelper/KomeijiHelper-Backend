package komeiji.back.websocket.message.impl;

import com.google.gson.Gson;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import komeiji.back.websocket.message.Message;
import komeiji.back.websocket.session.SessionToken;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class TextMessage extends Message {
    protected static Gson gson = new Gson();
    protected String data;
    protected String type = "text";

    public TextMessage(SessionToken from, SessionToken to, long timestamp) {
        super(from,to, timestamp);
        this.type = type;
    }


    @Override
    public TextWebSocketFrame
    messageDecode() {
        return new TextWebSocketFrame(gson.toJson(Map.of("type",type,"content", data, "timestamp", getTimestamp())));
    }

    public TextMessage(SessionToken from, SessionToken to, String data, long timestamp) {
        this(from, to, timestamp);
        this.data = data;
    }

    public TextMessage(String type,SessionToken from, SessionToken to, String data, long timestamp) {
        this(from, to, data,timestamp);
        this.type = type;
    }


    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
