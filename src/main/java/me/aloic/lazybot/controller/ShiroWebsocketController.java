//package me.aloic.lazybot.controller;
//
//import jakarta.websocket.*;
//import jakarta.websocket.server.ServerEndpoint;
//import kotlin.Suppress;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.concurrent.CopyOnWriteArraySet;
//
//@RestController
//@ServerEndpoint("/qq-ws")
//public class ShiroWebsocketController
//{
//    private static final CopyOnWriteArraySet<Session> SESSIONS = new CopyOnWriteArraySet<>();
//
//    @OnOpen
//    public void onOpen(Session session) {
//        SESSIONS.add(session);
//        System.out.println("连接已建立，当前连接数: " + SESSIONS.size());
//    }
//
//    @OnMessage
//    public void onMessage(String message, Session session) {
//        System.out.println("收到消息: " + message);
//        // 这里可以对消息进行处理，例如广播或存储
//    }
//
//    @OnClose
//    public void onClose(Session session) {
//        SESSIONS.remove(session);
//        System.out.println("连接关闭，当前连接数: " + SESSIONS.size());
//    }
//
//    @OnError
//    public void onError(Session session, Throwable error) {
//        error.printStackTrace();
//    }
//
//    public static void broadcast(String message) {
//        for (Session session : SESSIONS) {
//            try {
//                session.getBasicRemote().sendText(message);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}
