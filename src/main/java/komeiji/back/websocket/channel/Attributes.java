package komeiji.back.websocket.channel;

import io.netty.util.AttributeKey;
import komeiji.back.websocket.persistence.Conversation;
import komeiji.back.websocket.session.Session;

public class Attributes {
    public static final AttributeKey<Session> SESSION = AttributeKey.valueOf("session");
    public static final AttributeKey<Conversation> CONVERSATION = AttributeKey.valueOf("conversation");
    public static final AttributeKey<Long> LAST_IDLE_TIME = AttributeKey.valueOf("lastIdleTime");
    public static final AttributeKey<Boolean> TIMEOUT = AttributeKey.valueOf("timeout");
}
