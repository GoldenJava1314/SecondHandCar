//package com.example.demo.shcar.test;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//import java.io.IOException;
//import java.util.List;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.example.demo.shcar.controller.ImageController;
//import com.example.demo.shcar.service.ImageService;
//
//class ImageControllerTest {
//
//    @Test
//    void uploadImages_shouldReturnUrlsFromService() throws IOException {
//
//        // 1. Mock ImageService
//        ImageService imageService = mock(ImageService.class);
//
//        // 2. 建立 Controller（不靠 Spring）
//        ImageController controller = new ImageController(imageService);
//
//        // 3. 準備假資料
//        MultipartFile file1 = mock(MultipartFile.class);
//        MultipartFile file2 = mock(MultipartFile.class);
//        List<MultipartFile> files = List.of(file1, file2);
//
//        List<String> fakeResult = List.of(
//                "http://img/1.jpg",
//                "http://img/2.jpg"
//        );
//
//        when(imageService.uploadImages(files)).thenReturn(fakeResult);
//
//        // 4. 呼叫 Controller
//        List<String> result = controller.uploadImages(files);
//
//        // 5. 驗證結果
//        assertEquals(2, result.size());
//        assertEquals("http://img/1.jpg", result.get(0));
//
//        // 6. 驗證 service 有被呼叫
//        verify(imageService).uploadImages(files);
//    }
//}