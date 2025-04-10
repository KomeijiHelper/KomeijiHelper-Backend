package komeiji.back.websocket.protocol.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import komeiji.back.websocket.message.Message;
import komeiji.back.websocket.message.MessageFactory;
import komeiji.back.websocket.message.MessageType;
import komeiji.back.websocket.message.impl.TextMessage;
import komeiji.back.websocket.protocol.TextFrameProtocol;
import komeiji.back.websocket.session.Session;

class MessageBody {

    public MessageType type;
    public String content;
    public long timestamp;

    public MessageBody(MessageType type, String content, long timestamp) {
        this.type = type;
        this.content = content;
        this.timestamp = timestamp;
    }

    public static MessageBody buildMessageBody(String json) {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        MessageType type = MessageType.fromString(jsonObject.get("type").getAsString());
        String content = jsonObject.get("content").getAsString();
        long timestamp = jsonObject.get("timestamp").getAsLong();
        return new MessageBody(type, content, timestamp);
    }
}

public class DefaultTextFrameProtocol extends TextFrameProtocol<Message> {

    @Override
    public Message frameTransform(TextWebSocketFrame frame, Session session) {
        String text = frame.text();
        System.out.println("DefaultTextFrameProtocol parse data into" + text);

        MessageBody body = MessageBody.buildMessageBody(text);
        System.out.println("text message content :"+ body.content);
        return MessageFactory.newTextMessage(body.type, session.getId(), session.getTarget(), body.content, body.timestamp);
    }


}
