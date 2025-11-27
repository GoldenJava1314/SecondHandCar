package com.example.demo.shcar.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.shcar.model.dto.CarDTO;
import com.example.demo.shcar.model.dto.CarUploadDTO;
import com.example.demo.shcar.model.entity.Car;
import com.example.demo.shcar.model.entity.User;
import com.example.demo.shcar.repository.CarRepository;
import com.example.demo.shcar.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CarService {
	
	

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private ModelMapper modelMapper;

    // 建立車輛 (不含圖片上傳)
    public CarDTO createCarFromUploadDTO(CarUploadDTO carDTO, User seller) {

        if (seller == null) {
            throw new RuntimeException("建立車輛時 seller 資料遺失");
        }

        Car car = new Car();
        car.setBrand(carDTO.getBrand());
        car.setModel(carDTO.getModel());
        car.setYear(carDTO.getYear());
        car.setMiles(carDTO.getMileage());
        car.setPrice(carDTO.getPrice());
        car.setImagesJson("[]");
        car.setSeller(seller);

        Car saved = carRepository.save(car);
        return convertToDTO(saved);
    }

    // 取得所有車輛
    public List<CarDTO> getAllCars() {
        return carRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // 取得單一
    public CarDTO getCarById(Long id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new RuntimeException("Car not found"));
        return convertToDTO(car);
    }

    // 更新多張圖片
    public CarDTO updateCarImages(Long id, MultipartFile[] files) {

        if (files == null || files.length == 0) {
            throw new RuntimeException("沒有選擇任何圖片");
        }

        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到車輛"));

        try {
            String uploadDir = "uploads/";
            Files.createDirectories(Paths.get(uploadDir));

            List<String> newUrls = new ArrayList<>();

            for (MultipartFile f : files) {

                if (f.isEmpty()) continue;

                String fileName = "car_" + id + "_" + System.currentTimeMillis()
                        + "_" + f.getOriginalFilename();

                Path path = Paths.get(uploadDir + fileName);
                Files.write(path, f.getBytes());
                newUrls.add("http://localhost:8080/uploads/" + fileName);
            }

            // 合併原有圖片
            ObjectMapper mapper = new ObjectMapper();
            List<String> existing = new ArrayList<>();

            if (car.getImagesJson() != null && !car.getImagesJson().isBlank()) {
                existing = List.of(mapper.readValue(car.getImagesJson(), String[].class));
            }

            existing = new ArrayList<>(existing);
            existing.addAll(newUrls);

            car.setImagesJson(mapper.writeValueAsString(existing));

            return convertToDTO(carRepository.save(car));

        } catch (Exception e) {
            throw new RuntimeException("圖片上傳失敗：" + e.getMessage());
        }
    }

    // Entity -> DTO (會把 imagesJson 轉成 List<String>)
    private CarDTO convertToDTO(Car car) {
        CarDTO dto = modelMapper.map(car, CarDTO.class);

        if (car.getImagesJson() != null && !car.getImagesJson().isBlank()) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                String[] arr = mapper.readValue(car.getImagesJson(), String[].class);
                dto.setImages(java.util.Arrays.asList(arr));
            } catch (Exception ignored) {}
        }
        return dto;
    }
    
    public void deleteCar(Long carId, Long userId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("找不到車輛"));

        // 可選：檢查該車是否是該使用者上傳
        if (!car.getSeller().getId().equals(userId)) {
            throw new RuntimeException("你沒有權限刪除這台車");
        }

        carRepository.delete(car);
    }
    
}