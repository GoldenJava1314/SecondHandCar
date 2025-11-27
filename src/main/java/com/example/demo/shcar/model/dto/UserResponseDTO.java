package com.example.demo.shcar.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserResponseDTO {//（回傳前端用）
	    private Long id;
	    private String username;
	    
	    //❗ 注意：我故意不回傳 password → 符合資安
	    
	    private String Name;
	    private String phone;
	    private String email;

	    // Getter & Setter
	}
