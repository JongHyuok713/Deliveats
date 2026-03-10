package com.example.deliveats.controller.image;

import com.example.deliveats.service.image.ImageUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
public class UploadController {

    private final ImageUploadService imageUploadService;

    @PostMapping("/{type}/{targetId}")
    public ResponseEntity<?> uploadImage(
            @PathVariable String type,
            @PathVariable Long targetId,
            @RequestParam("file") MultipartFile file
    ) {
        String url = imageUploadService.upload(file, type);

        return ResponseEntity.ok(Map.of(
                "url", url,
                "type", type,
                "targetId", targetId
        ));
    }
}
