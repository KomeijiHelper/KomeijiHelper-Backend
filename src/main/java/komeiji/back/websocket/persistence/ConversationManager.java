package komeiji.back.websocket.persistence;

import komeiji.back.websocket.message.Message;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// TODO:
public class ConversationManager {

    private final ConcurrentMap<String, Conversation> conversations = new ConcurrentHashMap<>();

    public void addMessageRecord(Message msg) {
//        Conversation conversation;
//        MessageRecord  record;
//        conversation.addRecord(record);
    }

    public Conversation newConversation(String CID) {
        if (conversations.containsKey(CID)) {
            return null;
        }
        return null;
    }

    public void persistenceConversation(String CID) {

    }


}
