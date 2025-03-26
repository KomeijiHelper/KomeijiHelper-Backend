package komeiji.back.websocket.persistence;

import komeiji.back.websocket.session.SessionToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConversationManager {
    private static final Logger logger = LoggerFactory.getLogger(ConversationManager.class);

    private final ConcurrentMap<UUID, Conversation> conversations = new ConcurrentHashMap<>();

    public Conversation newConversation(SessionToken session1, SessionToken session2, RecordStorage storage) {
        UUID CID = ConversationUtils.sessionTokens2CID(session1, session2);
        Conversation oldConversation = conversations.get(CID);
        if (oldConversation != null) {
            return oldConversation;
        }
        Conversation conversation = Conversation.newConversationInstance(CID,storage);
        conversations.put(CID, conversation);
        return conversation;
    }

    public void closeConversation(UUID CID) {
        Conversation conversation = conversations.get(CID);
        if (conversation == null) {
            return;
        }
        assert conversation.getCID().equals(CID);
        conversation.close();
        conversations.remove(CID);
    }



}
