package komeiji.back.websocket.message.impl;

import com.google.gson.Gson;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import komeiji.back.websocket.message.Message;
import komeiji.back.websocket.session.SessionToken;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ChatRejectMessage extends Message {
    private static final Gson gson = new Gson();

    private String data;

    public ChatRejectMessage(SessionToken from,SessionToken to,String data,long timestamp) {
        super(from,to,timestamp);
        this.data = data;
    }


    @Override
    public TextWebSocketFrame messageDecode() {
        return new TextWebSocketFrame(gson.toJson(Map.of(
                "type","chat_reject",
                "content",data
        )));
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
