package com.example.demo.shcar.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CarSearchDTO {
	
	private String brand;
    private Integer minPrice;
    private Integer maxPrice;
    private Integer minYear;
    private Integer maxYear;

}
