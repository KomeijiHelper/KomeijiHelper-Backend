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

    public MessageRecord(Message msg) {
        this.timestamp = msg.getTimestamp();
        this.token = msg.getFrom();
        this.type = msg.getClass().getSimpleName();
        this.data = ((TextMessage) msg).getData();

    }
}
