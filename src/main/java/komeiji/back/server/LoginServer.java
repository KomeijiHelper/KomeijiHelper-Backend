//package komeiji.back.server;
//
//import jakarta.servlet.http.HttpSession;
//import jakarta.websocket.*;
//import jakarta.websocket.server.PathParam;
//import jakarta.websocket.server.ServerEndpoint;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//
//@ServerEndpoint("/login/{user_id}")
//@Component
//public class LoginServer {
//    private Session session;
//    public static HashMap<String,Session> loginPool = new HashMap<String,Session>();
//
//    @OnOpen
//    public void onOpen(@PathParam("user_id") String user_id,Session session){
//        this.session = session;
//        loginPool.put(user_id,session);
//        System.out.println("websocket连接");
//        System.out.println(user_id+":"+session);
//    }
//
//    @OnClose
//    public void onClose(@PathParam("user_id") String user_id){
//        loginPool.remove(user_id);
//    }
//
//    @OnError
//    public void onError(Session session, Throwable error){
//        error.printStackTrace();
//    }
//
//    @OnMessage
//    public void onMessage(String message){
//        System.out.println("receive message");
//        System.out.println(message);
//    }
//}
