package komeiji.back.websocket.persistence;

import komeiji.back.websocket.message.Message;
import komeiji.back.websocket.message.impl.TextMessage;
import komeiji.back.websocket.session.SessionToken;

public class MessageRecord {

    private SessionToken token;
    private long timestamp;
    private String type;
    private String data;
    
    private MessageRecord(){}
    public String toString(){
        return "MessageRecord{" +
                "token=" + token +
                ", timestamp=" + timestamp +
                ", type='" + type + '\'' +
                ", data='" + data + '\'' +
                '}';
    }

    public MessageRecord(Message msg) {
        this.token = msg.getFrom();
        this.timestamp = msg.getTimestamp();
        this.type = "message";
        this.data = ((TextMessage) msg).getData();
    }
}
