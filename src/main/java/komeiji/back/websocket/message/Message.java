package komeiji.back.websocket.message;

import komeiji.back.websocket.session.SessionToken;
import lombok.Data;


@Data
public abstract class Message {
    private SessionToken from;
    private SessionToken to;
    private long timestamp;


    public Message(SessionToken from, SessionToken to, long timestamp) {
        this.from = from;
        this.to = to;
        this.timestamp = timestamp;
    }

    // We need to decode by ourselves the content send to Websocket channel
    public abstract Object messageDecode();
}
