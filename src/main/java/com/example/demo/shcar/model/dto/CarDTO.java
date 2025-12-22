package com.example.demo.shcar.model.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CarDTO {
    private Long id;
    private String brand;
    private String model;
    private Integer year;
    private Integer mileage;
    private Integer price;
    private String description;
    private String imageUrl;
    private List<String> images; // 轉換自 imagesJson
    private Long sellerId;
    private String sellerName;
    private String sellerLineId; // 回傳前端
    private boolean deleted; // 軟刪除狀態（前端判斷按鈕用）
}