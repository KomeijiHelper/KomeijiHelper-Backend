package komeiji.back;

import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.logging.LogLevel;
import io.netty.util.concurrent.GlobalEventExecutor;
import komeiji.back.utils.InitUtils;
import komeiji.back.websocket.WebSocketServer;
import komeiji.back.websocket.message.fowardqueue.impl.CLMessageQueue;
import komeiji.back.websocket.persistence.ConversationManager;
import komeiji.back.websocket.session.SessionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
public class BackApplication {

    @Value("${useSsl}")
    private static boolean ssl;

    public static void main(String[] args) {
        SpringApplication.run(BackApplication.class, args);
        InitUtils.init();
        WebSocketServer webSocketServer = WebSocketServer.getWebSocketSingleServer(ssl,LogLevel.INFO,1048576*16,"/ws",
                new SessionManager(new DefaultChannelGroup(GlobalEventExecutor.INSTANCE)),
                new CLMessageQueue(),
                new ConversationManager());
        webSocketServer.startServer(54950);
    }
}

