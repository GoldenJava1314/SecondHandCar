package com.example.demo.shcar.controller;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.shcar.model.dto.CarDTO;
import com.example.demo.shcar.model.dto.CarUploadDTO;
import com.example.demo.shcar.model.dto.UserResponseDTO;
import com.example.demo.shcar.model.entity.Car;
import com.example.demo.shcar.model.entity.User;
import com.example.demo.shcar.repository.CarRepository;
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
	private CarRepository carRepository;
	
	@Autowired
	private UserService userService;
	
    @Autowired
    private CarService carService;
    
    @Autowired
    private ModelMapper modelMapper;

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
        
        System.out.println("後端收到的 sellerLineId = " + carDTO.getSellerLineId());

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
    
// 加入關注    
    @PostMapping("/favorite/{carId}")
    public ResponseEntity<?> addFavorite(
            @PathVariable Long carId,
            HttpSession session
    ) {
        User loginUser = (User) session.getAttribute("LOGIN_USER");
        if (loginUser == null) {
            return ResponseEntity.status(401).body("未登入");
        }

        User user = userRepository.findById(loginUser.getId())
                        .orElseThrow(() -> new RuntimeException("找不到使用者"));

        Car car = carRepository.findById(carId)
                        .orElseThrow(() -> new RuntimeException("找不到車輛"));

        
        boolean exists = user.getFavoriteCars()
        	    .stream()
        	    .anyMatch(c -> c.getId().equals(car.getId()));

        	if (!exists) {
        	    user.getFavoriteCars().add(car);
        	    userRepository.save(user);
        	}
        
//        if (!user.getFavoriteCars().contains(car)) {
//            user.getFavoriteCars().add(car);
//            userRepository.save(user);
//        }

        // 更新 session
        session.setAttribute("LOGIN_USER", user);

        // 回傳最新收藏 ID 列表
        List<Long> ids = user.getFavoriteCars()
                             .stream()
                             .map(Car::getId)
                             .toList();

        return ResponseEntity.ok(ids);
    }
    
// 取消關注
    @DeleteMapping("/favorite/{carId}")
    public ResponseEntity<?> removeFavorite(
            @PathVariable Long carId,
            HttpSession session
    ) {
        User loginUser = (User) session.getAttribute("LOGIN_USER");
        if (loginUser == null) {
            return ResponseEntity.status(401).body("未登入");
        }

        User user = userRepository.findById(loginUser.getId())
                        .orElseThrow(() -> new RuntimeException("找不到使用者"));

        Car car = carRepository.findById(carId)
                        .orElseThrow(() -> new RuntimeException("找不到車輛"));

        user.getFavoriteCars().remove(car);
        userRepository.save(user);

        // 更新 session
        session.setAttribute("LOGIN_USER", user);

        List<Long> ids = user.getFavoriteCars()
                             .stream()
                             .map(Car::getId)
                             .toList();

        return ResponseEntity.ok(ids);
    }
    
    //查看是否已收藏
    @GetMapping("/favorite/check/{carId}")
    public ResponseEntity<?> checkFavorite(
            @PathVariable Long carId,
            HttpSession session
    ) {
        User user = (User) session.getAttribute("LOGIN_USER");
        if (user == null) return ResponseEntity.ok(false);

        boolean fav = user.getFavoriteCars().stream()
                          .anyMatch(c -> c.getId().equals(carId));

        return ResponseEntity.ok(fav);
    }
    
    //取得我的收藏清單
    @GetMapping("/favorite/list")
    public ResponseEntity<?> getMyFavorites(HttpSession session) {
        User loginUser = (User) session.getAttribute("LOGIN_USER");
        if (loginUser == null) {
            return ResponseEntity.ok(List.of());
        }

        User user = userRepository.findById(loginUser.getId())
                        .orElseThrow(() -> new RuntimeException("找不到使用者"));

        
        List<CarDTO> list = user.getFavoriteCars()
                                .stream()
                                .filter(car -> !car.isDeleted()) 
                                .map(carService::convertToDTO)
                                .toList();

        return ResponseEntity.ok(list);
    }
    
 // 查詢我的刊登 取得「我的刊登」API
    @GetMapping("/my-cars")
    public ApiResponse<List<CarDTO>> getMyCars(HttpSession session) {
        User loginUser = (User) session.getAttribute("LOGIN_USER");
        if (loginUser == null) {
            return new ApiResponse<>(401, "未登入", null);
        }

        List<CarDTO> cars = carService.getCarsBySeller(loginUser.getId());
        return new ApiResponse<>(200, "成功", cars);
    }

    //還原（restore）API
    @PostMapping("/restore/{id}")
    public ApiResponse<String> restoreCar(
            @PathVariable Long id,
            HttpSession session) {

        User user = (User) session.getAttribute("LOGIN_USER");

        if (user == null) {
            return new ApiResponse<>(401, "未登入", null);
        }

        try {
            carService.restoreCar(id, user.getId());
            return new ApiResponse<>(200, "已還原", null);
        } catch (Exception e) {
            return new ApiResponse<>(400, e.getMessage(), null);
        }
    }
    
    @DeleteMapping("/hard-delete/{id}")
    public ApiResponse<String> hardDelete(
            @PathVariable Long id,
            HttpSession session) {

        User user = (User) session.getAttribute("LOGIN_USER");

        if (user == null) {
            return new ApiResponse<>(401, "未登入", null);
        }

        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到車輛"));

        System.out.println("Hard delete carId=" + id + ", userId=" + user.getId());
        System.out.println("car.seller = " + car.getSeller());
        
        // 只有自己能刪自己的車
        if (!car.getSeller().getId().equals(user.getId())) {
            return new ApiResponse<>(403, "你沒權限刪除別人的車", null);
        }
        
        if (car.getSeller() == null) {
            return new ApiResponse<>(400, "此車輛沒有賣家，無法刪除", null);
        }

        if (!car.getSeller().getId().equals(user.getId())) {
            return new ApiResponse<>(403, "你沒權限刪除別人的車", null);
        }

                
        // 真正刪除
        carService.hardDeleteCar(id, user.getId());
        return new ApiResponse<>(200, "已永久刪除", null);
    }
    
}