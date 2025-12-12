package com.example.demo.shcar.model.entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
@Table(name = "car")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String brand;

    @Column(nullable = false, length = 100)
    private String model;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer mileage;

    @Column(nullable = false)
    private int price;

    @Column()
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = true) 
    private User seller;

    // 儲存多張圖片 URL 的 JSON 陣列字串: '["http://...","http://..."]'
    @Column(name = "images_json", columnDefinition = "TEXT")
    private String imagesJson;
    
    
    @ManyToMany(mappedBy = "favoriteCars")
    @JsonIgnore
    private Set<User> likedByUsers;
    
    @Column(name = "seller_line_id", length = 100, nullable = true)
    private String sellerLineId; // 賣家 Line ID
    
    @Column(nullable = false)
    private boolean deleted = false;
    
 
    
}