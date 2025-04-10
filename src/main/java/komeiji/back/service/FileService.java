package komeiji.back.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String fileUploadService(MultipartFile file);
}
