package com.example.Social_Media_WhichApp.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Collections;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Đăng ký endpoint WebSocket với SockJS fallback và xác thực user
        registry.addEndpoint("/notification")
                .setAllowedOrigins("http://localhost:8443")  // Đảm bảo cho phép nguồn từ React app nếu bạn đang chạy trên cổng 3000
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    protected Principal determineUser(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler) {
                        String token = request.getHeaders().getFirst("Authorization");
                        System.out.println("Authorization header: " + token);  // Log sẽ xuất hiện ở đây
                        if (token != null && token.startsWith("Bearer ")) {
                            String jwtToken = token.substring(7);
                            String username = validateTokenAndGetUsername(jwtToken);
                            if (username != null) {
                                return new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
                            }
                        }
                        // Log thông tin nếu không tìm thấy token hợp lệ
                        System.out.println("Token không hợp lệ hoặc không tồn tại");
                        return null;
                    }
                })
                .withSockJS();

    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Cấu hình broker cho WebSocket
        registry.enableSimpleBroker("/topic", "/queue");  // Kích hoạt các topic/queue cho message broker
        registry.setApplicationDestinationPrefixes("/app");  // Tiền tố cho các destination ứng dụng
        registry.setUserDestinationPrefix("/user");  // Tiền tố cho các destination dành riêng cho user
    }

    private String validateTokenAndGetUsername(String jwtToken) {
        // Kiểm tra và xác thực JWT token, sau đó lấy thông tin user từ token
        try {
            JwtConfig jwtConfig = new JwtConfig();
//            String token = Jwts.builder()
//                    .setSubject("username")
//                    .signWith(SignatureAlgorithm.HS256, jwtConfig.getJwtSecretKey()) // Sử dụng secret key từ cấu hình
//                    .compact();
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtConfig.getJwtSecretKey()) // Sử dụng secret key để xác minh
                    .parseClaimsJws(jwtToken)
                    .getBody();
            return claims.getSubject();  // Trả về username từ token
        } catch (JwtException e) {
            return null;  // Trả về null nếu token không hợp lệ
        }
    }
}

