package komeiji.back.websocket.persistence.meta;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class Meta {
    UUID uuid;
    long timestamp;
    List<Character> characters;
    transient String storePath;

    public Meta(UUID uuid, long timestamp, List<Character> characters, String storePath){
        this.uuid = uuid;
        this.timestamp = timestamp;
        this.characters = characters;
        this.storePath = storePath;
    }
}