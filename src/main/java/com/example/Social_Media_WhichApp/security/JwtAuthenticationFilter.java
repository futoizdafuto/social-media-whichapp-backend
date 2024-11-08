package com.example.Social_Media_WhichApp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
@Autowired
private JwtUtil jwtUtil;

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//            throws ServletException, IOException {
//        String token = extractToken(request);// Trích xuất token từ yêu cầu
//
//        String path = request.getRequestURI();// Lấy đường dẫn yêu cầu
//        if (isLoginOrRegisterRequest(path)) {
//            // Nếu token hợp lệ và yêu cầu là login hoặc register, trả về thông báo yêu cầu đăng xuất
//            if (token != null && jwtUtil.validateToken(token)) {
//                sendErrorResponse(response, "Please log out before " + (path.equals("/api/users/register") ? "registering a new account" : "logging"));
//                return;
//            } else {
//                // Nếu là login hoặc register mà không có token hợp lệ, tiếp tục xử lý
//                chain.doFilter(request, response);
//                return;
//            }
//        }
//
//        // Kiểm tra token cho các endpoint khác ngoài login/register
//        if (token != null && jwtUtil.validateToken(token)) {
//            authenticateUser(token, request);// Xác thực người dùng nếu token hợp lệ
//        } else {
//            // Nếu không có token hoặc token không hợp lệ, gửi phản hồi lỗi
//            sendErrorResponse(response, token == null ? "Token is required" : "Invalid or expired token");
//            return;
//        }
//
//        chain.doFilter(request, response); // Nếu token hợp lệ, tiếp tục xử lý yêu cầu
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String token = extractToken(request); // Trích xuất token từ yêu cầu
        String path = request.getRequestURI(); // Lấy đường dẫn yêu cầu

        // Bỏ qua kiểm tra token cho các endpoint không yêu cầu xác thực
        if (isPublicRequest(path)) {
            chain.doFilter(request, response); // Cho phép tiếp tục xử lý mà không cần token
            return;
        }

        // Kiểm tra token cho các endpoint khác
        if (token != null && jwtUtil.validateToken(token)) {
            authenticateUser(token, request); // Xác thực người dùng nếu token hợp lệ
        } else {
            sendErrorResponse(response, token == null ? "Token is required" : "Invalid or expired token");
            return;
        }

        chain.doFilter(request, response); // Nếu token hợp lệ, tiếp tục xử lý yêu cầu
    }

    private boolean isPublicRequest(String path) {
        return path.equals("/api/users/login") || path.equals("/api/users/register") || path.startsWith("/uploads/") || path.startsWith("/api/");
    }


    // Phương thức để trích xuất token từ request
    private String extractToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");// Lấy token từ header Authorization
        return (token != null && token.startsWith("Bearer ")) ? token.substring(7) : null; // Trả về token nếu có
    }

    // Phương thức để kiểm tra nếu yêu cầu đến từ các endpoint không yêu cầu token hoặc từ endpoint /register khi người dùng đã có token hợp lệ
    private boolean isLoginOrRegisterRequest(String path) {
        return path.equals("/api/users/login") || path.equals("/api/users/register") || path.equals("/upload/") || path.equals("/api/");
    }

    // Phương thức để xác thực người dùng
    private void authenticateUser(String token, HttpServletRequest request) {
        String username = jwtUtil.getUsernameFromToken(token); // Lấy tên người dùng từ token
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                username, null, null);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));; // Thiết lập chi tiết xác thực
        SecurityContextHolder.getContext().setAuthentication(authentication); // Lưu trữ xác thực trong SecurityContext
    }

    // Phương thức để gửi phản hồi lỗi
    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Đặt trạng thái phản hồi là 401 Unauthorized
        response.setContentType("application/json"); // Đặt loại nội dung là JSON
        PrintWriter out = response.getWriter(); // Tạo đối tượng PrintWriter để gửi nội dung phản hồi
        out.print("{\"Login\":false,\"message\":\"" + message + "\"}"); // Gửi thông báo lỗi dưới dạng JSON
        out.flush(); // Đảm bảo nội dung được gửi đi
    }
}
