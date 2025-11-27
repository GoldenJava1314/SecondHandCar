package com.example.demo.shcar.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.shcar.model.dto.CarDTO;
import com.example.demo.shcar.model.dto.CarUploadDTO;
import com.example.demo.shcar.model.dto.UserResponseDTO;
import com.example.demo.shcar.model.entity.User;
import com.example.demo.shcar.repository.UserRepository;
import com.example.demo.shcar.response.ApiResponse;
import com.example.demo.shcar.service.CarService;
import com.example.demo.shcar.service.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/cars")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class CarController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserService userService;
	
    @Autowired
    private CarService carService;

    @GetMapping
    public List<CarDTO> list() {
        return carService.getAllCars();
    }

    @GetMapping("/{id}")
    public CarDTO findOne(@PathVariable Long id) {
        return carService.getCarById(id);
    }

    // 建立 car（不包含上傳圖片）
    @PostMapping
    public CarDTO createCar(@RequestBody CarUploadDTO carDTO, HttpSession session) {
        User user = (User) session.getAttribute("LOGIN_USER");
        if (user == null) {
            throw new RuntimeException("未登入，請重新登入後再嘗試");
        }

        return carService.createCarFromUploadDTO(carDTO, user);
    }

    @PostMapping("/{id}/images")
    public CarDTO uploadImages(
            @PathVariable Long id,
            @RequestParam("images") MultipartFile[] images,
            HttpSession session
    ) {
    	
    	System.out.println("收到 images 長度: " + images.length);
    	for(MultipartFile f : images) {
    	    System.out.println("檔名: " + f.getOriginalFilename() + ", size=" + f.getSize());
    	}
    	
        User user = (User) session.getAttribute("LOGIN_USER");
        if (user == null) {
            throw new RuntimeException("未登入，無法上傳圖片");
        }

        return carService.updateCarImages(id, images);
    }
    
 // 刪除車輛
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCar(@PathVariable Long id, HttpSession session) {
        // 先確認使用者是否登入
        User userDTO = (User) session.getAttribute("LOGIN_USER");
        if (userDTO == null) {
            return new ApiResponse<>(401, "未登入", null);
        }

        try {
            // 可以額外檢查這台車是否是這個使用者的
            carService.deleteCar(id, userDTO.getId());
            return new ApiResponse<>(200, "刪除成功", null);
        } catch (RuntimeException e) {
            return new ApiResponse<>(400, e.getMessage(), null);
        }
    }
}