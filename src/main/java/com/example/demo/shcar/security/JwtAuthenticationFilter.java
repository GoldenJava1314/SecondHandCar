package com.example.demo.shcar.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.shcar.model.entity.User;
import com.example.demo.shcar.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        try {

            //  取得 Bearer token
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                username = jwtUtil.extractUsername(token); // 這裡可能丟例外
            }

            //  如果 username 不為 null 且尚未登入
            if (username != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

                User user = userRepository.findByUsername(username);

                if (user != null && jwtUtil.validateToken(token, username)) {

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    user,  //登入使用者
                                    null,  //密碼 (已驗證過)
                                    Collections.emptyList()   // 之後可放角色/權限
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder.getContext()
                            .setAuthentication(authToken);
                }
            }

        } catch (io.jsonwebtoken.ExpiredJwtException e) {

            System.out.println("JWT 已過期: " + e.getMessage());

        } catch (io.jsonwebtoken.JwtException e) {

            System.out.println("JWT 無效: " + e.getMessage());

        } catch (Exception e) {

            System.out.println("JWT 驗證發生錯誤: " + e.getMessage());
        }

        //  一定要放行
        filterChain.doFilter(request, response);
    }
}
