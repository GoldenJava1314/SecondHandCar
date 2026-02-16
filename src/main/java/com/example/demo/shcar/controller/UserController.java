package com.example.demo.shcar.controller;

import javax.security.auth.login.LoginException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.example.demo.shcar.model.dto.UserLoginDTO;
import com.example.demo.shcar.model.dto.UserRegisterDTO;
import com.example.demo.shcar.model.dto.UserResponseDTO;
import com.example.demo.shcar.model.entity.User;
import com.example.demo.shcar.repository.UserRepository;
import com.example.demo.shcar.service.UserService;
import com.example.demo.shcar.response.ApiResponse;
import com.example.demo.shcar.response.LoginResponse;
import com.example.demo.shcar.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


@RestController
@RequestMapping("api/user")
@CrossOrigin(origins = "http://localhost:5173",allowCredentials = "true")
public class UserController {

    private final UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    public UserController(UserRepository userRepository, JwtUtil jwtUtil, UserService userService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    // 註冊
    @PostMapping("/register")
    public ApiResponse<UserResponseDTO> register(@RequestBody UserRegisterDTO dto) {
        try {
        	UserResponseDTO savedUser = userService.register(dto);
            return new ApiResponse<>(200, "註冊成功", savedUser);
        } catch (Exception e) {
            return new ApiResponse<>(400, "註冊失敗：" + e.getMessage(), null);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody UserLoginDTO loginDTO) {

        // 1️ 驗證帳密並取得 User
        User user = userService.login(loginDTO);

        // 2️ 產生 JWT
        String accessToken = jwtUtil.generateToken(user.getUsername());

        // 3️ 回傳 token + 是否為管理員
        return ResponseEntity.ok(
            new LoginResponse(
                accessToken,
                user.isAdmin(),
                user.getUsername()
            )
        );
    }
//        try {
//            UserResponseDTO userDTO = userService.login(loginDTO);
//
//            // 取得完整 User（含 isAdmin）
//            User user = userService.findById(userDTO.getId());
//            
//            if (user == null) {
//                throw new RuntimeException("帳號或密碼錯誤");
//            }
//
//            if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
//                throw new RuntimeException("帳號或密碼錯誤");
//            }

            
        

//            // SESSION：存登入使用者
//            session.setAttribute("LOGIN_USER", user);
//
//            // SESSION：存是否為管理員
//            session.setAttribute("LOGIN_USER_IS_ADMIN", user.isAdmin());
//
//            // 回傳給前端（ userDTO 要包含 isAdmin）
//            return new ApiResponse<>(200, "登入成功", userDTO);
//            
//            String accessToken = jwtUtil.generateAccessToken(user.getId());
//            String refreshToken = jwtUtil.generateRefreshToken(user.getId());
//
//            return new LoginResponse(accessToken, refreshToken);
            

//        } catch (RuntimeException e) {
//            return new ApiResponse<>(400, e.getMessage(), null);
//        }
//    }

//    // 測試 Token API（不需登入）
//    @GetMapping("/test-token")
//    public LoginResponse testToken() {
//        String token = jwtUtil.generateToken("testUser");
//        return new LoginResponse(token);
//    }
    
    // 取得登入者資訊
    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal User user) {
        if (user == null) {
            return "未登入或 Token 無效";
        }
        return "目前登入者：" + user.getUsername();
    }
    
    
    // 登出
    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout() { //只回傳成功訊息，前端管理 token 即可
        return ResponseEntity.ok().build();
    }
    
}
