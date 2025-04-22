package komeiji.back.websocket.persistence;


import komeiji.back.websocket.persistence.meta.Character;
import komeiji.back.websocket.persistence.meta.Meta;
import komeiji.back.websocket.session.SessionToken;
import lombok.Getter;
import lombok.Setter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



public class Conversation {
    @Getter
    private UUID CID;
    private RecordStorage storage;

    private static final String sessionDirPath = "chats";
    static {
        try {
            Files.createDirectories(Paths.get(sessionDirPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // meta data
    @Getter
    private long timeStamp;
    private List<Character> characters;
    @Getter @Setter
    private boolean hasStarted = false;

    public void addCharacter(SessionToken token) {
        characters.add(new Character(token));
    }

    private void start(){
        this.timeStamp = System.currentTimeMillis();
        String storePath = String.format("%s/%s.json", sessionDirPath,CID);
        storage.setMeta(new Meta(CID,timeStamp,characters, storePath));
    }

    private Conversation(){}
    private Conversation(UUID cid,RecordStorage storage){
        this.characters = new ArrayList<>();
        this.CID = cid;
        this.storage = storage;
    }

    public static Conversation newConversationInstance(UUID CID,RecordStorage storage){
        return new Conversation(CID,storage);
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

    public synchronized void tryStart() {
        if(hasStarted) return;
        if(characters.size() == 2) {
            hasStarted = true;
            start();
        }
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Conversation that = (Conversation) obj;
        return CID.equals(that.CID) && timeStamp == that.timeStamp;
    }
}
