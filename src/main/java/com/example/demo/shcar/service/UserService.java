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
import com.example.demo.shcar.security.JwtUtil;



@Service
public class UserService {

	@Autowired
    private JwtUtil jwtUtil;
	
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

    // 登入（回傳完整 User）
    public User login(UserLoginDTO loginDTO) {

        if (loginDTO.getUsername() == null || loginDTO.getUsername().trim().isEmpty() ||
            loginDTO.getPassword() == null || loginDTO.getPassword().trim().isEmpty()) {
            throw new RuntimeException("帳號或密碼不能為空");
        }

        User user = userRepository.findByUsername(loginDTO.getUsername().trim());

        if (user == null) {
            throw new RuntimeException("帳號或密碼錯誤");
        }

        if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("帳號或密碼錯誤");
        }

        return user;
    }

        
        
//        return modelMapper.map(user, UserResponseDTO.class);
        
//        <<<登入失敗次數鎖帳號>>>
//        User user = userRepository.findByUsername(loginDTO.getUsername());
//        if (user == null) {
//            throw new RuntimeException("帳號或密碼錯誤");
//        }
//
//        // 1️ 是否被鎖
//        if (user.isLocked()) {
//
//            // 到期自動解鎖
//            if (user.getLockUntil() != null &&
//                user.getLockUntil().isBefore(LocalDateTime.now())) {
//
//                user.setLocked(false);
//                user.setLoginFailCount(0);
//                user.setLockUntil(null);
//                userRepository.save(user);
//
//            } else {
//                throw new RuntimeException("帳號已鎖定，請稍後再試");
//            }
//        }
//
//        // 2️ 密碼檢查
//        if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
//
//            int failCount = user.getLoginFailCount() + 1;
//            user.setLoginFailCount(failCount);
//
//            // 3️ 失敗達上限 → 鎖帳
//            if (failCount >= 5) {
//                user.setLocked(true);
//                user.setLockUntil(LocalDateTime.now().plusMinutes(15));
//            }
//
//            userRepository.save(user);
//            throw new RuntimeException("帳號或密碼錯誤");
//        }
//
//        // 4️ 登入成功 → 重置
//        user.setLoginFailCount(0);
//        user.setLocked(false);
//        user.setLockUntil(null);
//        userRepository.save(user);
//
//        return modelMapper.map(user, UserResponseDTO.class);
        
//    }
    
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到使用者"));
    }
}