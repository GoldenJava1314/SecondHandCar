package com.example.demo.shcar.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.example.demo.shcar.model.dto.UserLoginDTO;
import com.example.demo.shcar.model.dto.UserRegisterDTO;
import com.example.demo.shcar.model.dto.UserResponseDTO;
import com.example.demo.shcar.model.entity.User;
import com.example.demo.shcar.repository.UserRepository;



@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

 // 註冊
    public UserResponseDTO register(UserRegisterDTO dto) {

        // 檢查帳號是否重複
        if (userRepository.findByUsername(dto.getUsername()) != null) {
            throw new RuntimeException("Username already exists");
        }

        // DTO → Entity
        User user = modelMapper.map(dto, User.class);

        // 密碼加密
        String hashed = BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt());
        user.setPassword(hashed);

        

        User saved = userRepository.save(user);

        UserResponseDTO response = modelMapper.map(saved, UserResponseDTO.class);
        //response.setPassword("******"); // 不回傳密碼

        return response;
    }

    // 登入
    public UserResponseDTO login(UserLoginDTO loginDTO) {
        // ✅ 新增：檢查空輸入
        if (loginDTO.getUsername() == null || loginDTO.getUsername().trim().isEmpty() ||
            loginDTO.getPassword() == null || loginDTO.getPassword().trim().isEmpty()) {
            throw new RuntimeException("帳號或密碼不能為空");
        }

        User user = userRepository.findByUsername(loginDTO.getUsername().trim());
        if (user == null) {
            throw new RuntimeException("用戶不存在");
        }

        if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("密碼錯誤");
        }

        return modelMapper.map(user, UserResponseDTO.class);
    }
    
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到使用者"));
    }
}