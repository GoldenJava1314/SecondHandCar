package com.example.demo.shcar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.shcar.model.entity.User;


public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
    
    List<User> findAllByFavoriteCars_Id(Long carId);
}