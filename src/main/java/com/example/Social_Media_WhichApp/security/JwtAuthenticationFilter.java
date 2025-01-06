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

@Component // Đánh dấu lớp này là một bean của Spring
public class JwtAuthenticationFilter extends OncePerRequestFilter { // Kế thừa lớp bộ lọc để kiểm tra JWT
    @Autowired // Tự động tiêm phụ thuộc JwtUtil để sử dụng trong lớp
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException { // Phương thức chính để xử lý yêu cầu
        String token = extractToken(request); // Trích xuất token từ yêu cầu

        String path = request.getRequestURI(); // Lấy đường dẫn yêu cầu
        if (isLoginOrRegisterRequest(path)) { // Kiểm tra xem yêu cầu có phải là login hoặc register không
            // Nếu token hợp lệ và yêu cầu là login hoặc register, trả về thông báo yêu cầu đăng xuất
            if (token != null && jwtUtil.validateToken(token)) {
                sendErrorResponse(response, "Please log out before " + (path.equals("/api/users/register") ? "registering a new account" : "logging"));
                return; // Ngừng xử lý yêu cầu nếu người dùng đã đăng nhập và đang cố gắng đăng nhập lại
            } else {
                // Nếu là login hoặc register mà không có token hợp lệ, tiếp tục xử lý
                chain.doFilter(request, response); // Cho phép yêu cầu đi qua nếu không có token
                return;
            }
        }
        // Bỏ qua kiểm tra token cho các endpoint không yêu cầu xác thực
        if (isPublicRequest(path)) {
            chain.doFilter(request, response); // Cho phép tiếp tục xử lý mà không cần token
            return;
        }
        // Kiểm tra token cho các endpoint khác ngoài login/register
        if (token != null && jwtUtil.validateToken(token)) { // Nếu token không null và hợp lệ
            authenticateUser(token, request); // Xác thực người dùng nếu token hợp lệ
        } else {
            sendErrorResponse(response, token == null ? "Token is required" : "Invalid or expired token");
            return; // Ngừng xử lý yêu cầu nếu token không hợp lệ
        }
        // Kiểm tra nếu yêu cầu là cho reLogin
        if (isReLogin(path)) {
            // Nếu token hợp lệ, cho phép tiếp tục
            if (token != null && jwtUtil.validateToken(token)) {
                authenticateUser(token, request); // Xác thực người dùng
            } else {
                sendErrorResponse(response, token == null ? "Token is required" : "Invalid or expired token");
                return; // Ngừng xử lý yêu cầu nếu token không hợp lệ
            }
        }

        // Kiểm tra yêu cầu DELETE cho bài viết
        if (isDeleteRequest(path)) {
            // Đảm bảo yêu cầu DELETE có token hợp lệ và người dùng có quyền thực hiện thao tác này
            if (token != null && jwtUtil.validateToken(token)) {
                authenticateUser(token, request); // Xác thực người dùng nếu token hợp lệ
            } else {
                sendErrorResponse(response, "Token is required for delete operation");
                return; // Ngừng xử lý yêu cầu nếu token không hợp lệ
            }
        }

        chain.doFilter(request, response); // Nếu token hợp lệ, tiếp tục xử lý yêu cầu
    }

    private boolean isPublicRequest(String path) {
        return path.equals("/api/users/login")
                || path.equals("/api/users/register")
                || path.equals("/api/users/oauth2/callback/google")
                || path.startsWith("/uploads/");

//        return path.equals("/api/users/login") || path.equals("/api/users/register") || path.startsWith("/uploads/") || path.startsWith("/api/");
    }
    // Kiểm tra yêu cầu DELETE
    private boolean isDeleteRequest(String path) {
        return path.startsWith("/api/posts/delete/");
    }


    // Phương thức để trích xuất token từ request
    private String extractToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization"); // Lấy token từ header Authorization
        return (token != null && token.startsWith("Bearer ")) ? token.substring(7) : null; // Trả về token nếu có, loại bỏ "Bearer "
    }

    // Phương thức để kiểm tra nếu yêu cầu đến từ các endpoint không yêu cầu token hoặc từ endpoint /register khi người dùng đã có token hợp lệ
    private boolean isLoginOrRegisterRequest(String path) {
        return path.equals("/api/users/login")
                || path.equals("/api/users/register")
                || path.equals("/api/users/reLogin")
//                || path.equals("/api/users/oauth2/callback/google"
                || path.startsWith("/uploads/");
//        return path.equals("/api/users/login") || path.equals("/api/users/register") || path.equals("/upload/") || path.equals("/api/")|| path.equals("/api/users/reLogin");

    }
    private boolean isReLogin(String path) {
        return path.equals("/api/users/reLogin"); // Kiểm tra đường dẫn
    }
    // Phương thức để xác thực người dùng
    private void authenticateUser(String token, HttpServletRequest request) {
        String username = jwtUtil.getUsernameFromToken(token); // Lấy tên người dùng từ token
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                username, null, null); // Tạo đối tượng xác thực với tên người dùng
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // Thiết lập chi tiết xác thực
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