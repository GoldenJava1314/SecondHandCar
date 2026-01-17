package com.example.demo.shcar.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {
	private final String UPLOAD_DIR = "uploads/";

    public List<String> uploadImages(List<MultipartFile> files) throws IOException {

        List<String> fileUrls = new ArrayList<>();

        for (MultipartFile file : files) {

            // 1. 安全檢查：只允許圖片
            if (!isImageFile(file)) {
                throw new RuntimeException("Only image files are allowed.");
            }

            // 2. 用 UUID 產生新檔名，避免覆蓋
            String extension = Objects.requireNonNull(file.getOriginalFilename())
                    .substring(file.getOriginalFilename().lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + extension;

            // 3. 儲存到本地 uploads/
            Path path = Paths.get(UPLOAD_DIR + newFileName);

            try (InputStream is = file.getInputStream()) {
                Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
            }
            // 4. 回傳可給前端使用的 URL
            fileUrls.add("/uploads/" + newFileName);
        }

        return fileUrls;
    }

    // 檢查是否為圖片類型
    private boolean isImageFile(MultipartFile file) {
        return Objects.requireNonNull(file.getContentType()).startsWith("image/");
    }
}