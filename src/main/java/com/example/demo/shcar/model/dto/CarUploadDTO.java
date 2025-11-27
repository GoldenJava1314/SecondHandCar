package com.example.demo.shcar.model.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CarUploadDTO {
    private String brand;
    private String model;
    private Integer year;
    private Integer mileage;
    private Integer price;
}