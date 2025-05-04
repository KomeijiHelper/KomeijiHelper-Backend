package komeiji.back.websocket.persistence;

import komeiji.back.websocket.session.SessionToken;

public class MessageRecord {

    private SessionToken token;
    private long timestamp;
    private String type;
    private String data;
    
    private MessageRecord(){}

    public MessageRecord(SessionToken token, long timestamp, String type, String data) {
        this.token = token;
        this.timestamp = timestamp;
        this.type = type;
        this.data = data;
    }
}
