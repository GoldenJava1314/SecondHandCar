package com.example.demo.shcar.model.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")  // 對應資料表名稱
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(length = 50, unique = true, nullable = false)
    private String username;   // 登入用帳號
    
	@Column(length = 255, nullable = false)
    private String password;   // 登入密碼
	
	@Column(length = 255, nullable = true)
    private String sellerName;       // 使用者名稱（賣家名稱）
    
	@Column(length = 255, nullable = true)
	private String phone; 
	
	
    @Column(nullable = true)
    private String email;      
    
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
        name = "user_favorite_cars",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "car_id")
    )
    @JsonIgnore // <- 防止序列化 User -> Car -> User 無限循環
    private List<Car> favoriteCars = new ArrayList<>();
    
    @Column(length = 100, nullable = true)
    private String sellerLineId;  // 賣家 Line ID
    
    @Column(name = "is_admin")
    private boolean isAdmin;  // 預設 false 永久刪除（Hard Delete，後台管理者限定）
    
//    @Column(nullable = false)
//    private int loginFailCount = 0;   // 登入失敗次數
//
//    @Column
//    private LocalDateTime lockUntil;  // 鎖到什麼時候
//
//    @Column(nullable = false)
//    private boolean locked = false;   // 是否被鎖
    
    
}