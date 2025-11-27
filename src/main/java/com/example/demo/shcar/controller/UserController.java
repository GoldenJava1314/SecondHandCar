package com.example.demo.shcar.controller;

import javax.security.auth.login.LoginException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import com.example.demo.shcar.model.dto.UserLoginDTO;
import com.example.demo.shcar.model.dto.UserRegisterDTO;
import com.example.demo.shcar.model.dto.UserResponseDTO;
import com.example.demo.shcar.model.entity.User;
import com.example.demo.shcar.repository.UserRepository;
import com.example.demo.shcar.service.UserService;
import com.example.demo.shcar.response.ApiResponse;

import jakarta.servlet.http.HttpSession;


@RestController
@RequestMapping("api/user")
@CrossOrigin(
		origins = "http://localhost:5173",
	    allowCredentials = "true"
	)
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    private UserService userService;

    UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    // 登入
    @PostMapping("/login")
    public ApiResponse<UserResponseDTO> login(@RequestBody UserLoginDTO loginDTO, HttpSession session) {
        try {
            UserResponseDTO userDTO = userService.login(loginDTO);

            // 取得完整 User 物件
            User user = userService.findById(userDTO.getId());

            // 存進 session（統一 key）
            session.setAttribute("LOGIN_USER", user);

            return new ApiResponse<>(200, "登入成功", userDTO);

        } catch (Exception e) {
            return new ApiResponse<>(400, "其他錯誤: " + e.getMessage(), null);
        }
    }
}
