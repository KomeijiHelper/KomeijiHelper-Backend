package komeiji.back.websocket.persistence;


import lombok.Getter;

import java.util.List;
import java.util.UUID;

public class Conversation {
    @Getter
    private UUID CID;
    @Getter
    private long timeStamp;
    private RecordStorage storage;

    private Conversation(){}
    private Conversation(UUID cid, long timeStamp,RecordStorage storage){
        this.CID = cid;
        this.timeStamp = timeStamp;
        this.storage = storage;
    }

    public static Conversation newConversationInstance(UUID CID,RecordStorage storage){
        return new Conversation(CID,System.currentTimeMillis(),storage);
    }

    public void storeRecord(MessageRecord record) {
        int ret = storage.singleStorage(record);
        // TODO: handle ret
    }

    public void storeRecord(List<MessageRecord> records) {
        int ret = storage.batchStorage(records);
        // TODO: handle ret
    }

    public void close() {
        storage.close();
    }
}
