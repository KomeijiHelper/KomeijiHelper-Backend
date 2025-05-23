package komeiji.back.websocket.message;

import komeiji.back.websocket.message.impl.*;
import komeiji.back.websocket.session.SessionToken;

public class MessageFactory {
    public static Message newTextMessage(MessageType type, SessionToken from, SessionToken to, String data, long timestamp) {
        return switch (type) {
            case TEXT_MESSAGE -> new TextMessage(from, to, data, timestamp);
            case IMG_SRC_MESSAGE -> new TextMessage("img_src",from, to, data, timestamp);
            case CHAT_RECORD_MESSAGE -> new TextMessage("chat_record",from, to, data, timestamp);
            case CHAT_REQUEST -> new ChatRequestMessage(from, to, data, timestamp);
            case NOTIFY_MESSAGE -> new NotifyMessage(from, to, data, timestamp);
            case CHAT_REJECT -> new ChatRejectMessage(from,to,data,timestamp);
            case CHAT_CONNECT -> new ChatConnectMessage(from, to, data, timestamp);
            default -> throw new IllegalArgumentException("Unknown message type");
        };
    }
}
