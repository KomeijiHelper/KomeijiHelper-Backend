package komeiji.back.websocket.persistence;

import komeiji.back.websocket.message.Message;
import komeiji.back.websocket.session.SessionToken;

public class MessageRecord {

    private SessionToken token;
    private long timestamp;
    private String type;
    private String data;
    
    private MessageRecord(){}

    public MessageRecord(Message msg) {

    }
}
