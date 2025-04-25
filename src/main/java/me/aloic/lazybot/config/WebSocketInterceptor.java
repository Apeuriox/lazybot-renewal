//package me.aloic.lazybot.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.http.server.ServerHttpResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.server.HandshakeInterceptor;
//
//import java.util.Map;
//@Component
//@Configuration
//public class WebSocketInterceptor implements HandshakeInterceptor
//{
//    @Override
//    public boolean beforeHandshake(ServerHttpRequest request,
//                                   ServerHttpResponse response,
//                                   WebSocketHandler wsHandler,
//                                   Map<String, Object> attributes) throws Exception {
//        HttpHeaders headers = request.getHeaders();
//        if (!headers.containsKey("Upgrade") ||
//                !headers.getUpgrade().equalsIgnoreCase("websocket")) {
//            // 非 WebSocket Upgrade 请求，直接拒绝
//            response.setStatusCode(HttpStatus.BAD_REQUEST);
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
//
//    }
//
//}
