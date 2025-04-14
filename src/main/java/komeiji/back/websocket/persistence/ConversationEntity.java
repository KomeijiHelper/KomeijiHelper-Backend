package komeiji.back.websocket.persistence;

import komeiji.back.websocket.persistence.meta.Meta;
import lombok.Data;

import java.util.List;

@Data
public class ConversationEntity {

    private Meta meta;
    private List<MessageRecord> messages;
}
