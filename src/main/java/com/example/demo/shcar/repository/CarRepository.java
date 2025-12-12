package com.example.demo.shcar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.shcar.model.entity.Car;

public interface CarRepository extends JpaRepository<Car, Long> {

   @Query("""
        SELECT c FROM Car c
        WHERE (:brand IS NULL OR c.brand = :brand)
        AND (:minPrice IS NULL OR c.price >= :minPrice)
        AND (:maxPrice IS NULL OR c.price <= :maxPrice)
        AND (:minYear IS NULL OR c.year >= :minYear)
        AND (:maxYear IS NULL OR c.year <= :maxYear)
    """)
	
    List<Car> searchCars(
            String brand,
            Integer minPrice,
            Integer maxPrice,
            Integer minYear,
            Integer maxYear
    );
   
   List<Car> findByDeletedFalse();//只查詢未刪除車輛資料的方法
   
   List<Car> findBySellerIdOrderByIdDesc(Long sellerId);//查詢 seller 的車
   
}
