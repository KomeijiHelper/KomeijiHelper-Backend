package komeiji.back.websocket;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import komeiji.back.websocket.channel.handlers.*;
import komeiji.back.websocket.message.fowardqueue.MessageForwardQueue;
import komeiji.back.websocket.persistence.ConversationManager;
import komeiji.back.websocket.session.SessionManager;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


public class WebSocketServer {

    private static WebSocketServer server = null;

    public static WebSocketServer getWebSocketSingleServer(LogLevel logLevel, int httpMaxContentLength, String url,
                                                           SessionManager sessionManager, MessageForwardQueue messageForwardQueue,
                                                           ConversationManager conversationManager) {
        if (server != null) {
            logger.error("Websocket server has already been build");
            System.exit(1);
        }
        server = new WebSocketServer(logLevel, httpMaxContentLength, url, sessionManager,messageForwardQueue,conversationManager);
        return server;
    }

    public static WebSocketServer getWebSocketSingleServer() {
        if(server == null) {
            logger.error("Websocket server has not been build");
            System.exit(1);
        }
        return server;
    }

    private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);
    private static final int MESSAGE_FORWARD_INTERVAL = 1;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final EventLoopGroup msgForwardGroup;
    @Getter
    private final String url;

    // 子组件,用来扩展功能
    @Getter
    private final MessageForwardQueue messageForwardQueue;
    @Getter
    private final SessionManager sessionManager;
    @Getter
    private final ConversationManager conversationManager;

    private ServerBootstrap bootstrap;

    public WebSocketServer(LogLevel logLevel, int httpMaxContentLength, String url,
                           SessionManager sessionManager, MessageForwardQueue messageForwardQueue,
                           ConversationManager conversationManager) {
       this.bossGroup = new NioEventLoopGroup();
       this.workerGroup = new NioEventLoopGroup();
       this.msgForwardGroup = new DefaultEventLoopGroup();
       this.sessionManager = sessionManager;
       this.messageForwardQueue = messageForwardQueue;
       this.conversationManager = conversationManager;
       this.url = url;
       InitServer(logLevel,httpMaxContentLength,url);
    }

    private void InitServer(LogLevel logLevel,int httpMaxContentLength,String url) {
            bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(logLevel))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();

                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(new ChunkedWriteHandler());
                            pipeline.addLast(new HttpObjectAggregator(httpMaxContentLength));

                            pipeline.addLast(new WebSocketServerProtocolHandler(url,true));

                            pipeline.addLast(new WebSocketConnectHandler());
                            pipeline.addLast(new FrameProtocolHandler());

                            // handlers for different types of message
                            pipeline.addLast(new TextMessageHandler());
                            pipeline.addLast(new BinaryMessageHandler());
                            pipeline.addLast(new NotifyMessageHandler());

                            // message persistence
                            pipeline.addLast(new PersistenceHandler());
                            // message forward
                            pipeline.addLast(msgForwardGroup,new MessageForwardHandler());
                        }
                    });
    }

    public void startServer(int port) {
        try {
            msgForwardGroup.scheduleAtFixedRate(()->{
                logger.debug("Calling schedule forward");
                messageForwardQueue.sendMessage();
            },0,MESSAGE_FORWARD_INTERVAL,TimeUnit.SECONDS);
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            System.exit(1);
        }
        finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            msgForwardGroup.shutdownGracefully();
        }
    }

}