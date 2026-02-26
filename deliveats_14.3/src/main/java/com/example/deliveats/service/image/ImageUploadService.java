package com.example.deliveats.service.image;

import com.example.deliveats.exception.CommonErrorCode;
import com.example.deliveats.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private final Path root = Paths.get(System.getProperty("user.dir"), "uploads");

    public String upload(MultipartFile file, String folderName) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(CommonErrorCode.INVALID_REQUEST, "file is required");
        }
        if (folderName == null || folderName.isBlank()) {
            throw new CustomException(CommonErrorCode.INVALID_REQUEST, "folderName is required");
        }

        try {
            // 폴더 생성
            Files.createDirectories(root.resolve(folderName));

            if (file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()) {
                throw new CustomException(CommonErrorCode.INVALID_REQUEST, "original filename is blank");
            }

            // 파일명
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path savePath = root.resolve(folderName).resolve(filename);

            // 저장
            file.transferTo(savePath.toFile());

            // 클라이언트 접근가능 URL 반환
            return "/uploads/" + folderName + "/" + filename;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(CommonErrorCode.INTERNAL_ERROR, e.getMessage());
        }
    }
}
