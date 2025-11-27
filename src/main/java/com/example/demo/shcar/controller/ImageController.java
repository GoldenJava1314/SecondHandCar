package com.example.demo.shcar.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.shcar.service.ImageService;



@RestController
@RequestMapping("/api/images")
@CrossOrigin(
		origins = "http://localhost:5173",
	    allowCredentials = "true"
	)
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    // 支援一次多張上傳
    @PostMapping("/upload")
    public List<String> uploadImages(@RequestParam("files") List<MultipartFile> files) throws IOException {
        return imageService.uploadImages(files);
    }
}