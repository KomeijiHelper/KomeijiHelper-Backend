package komeiji.back.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import komeiji.back.service.FileService;
import komeiji.back.utils.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name="文件相关功能")
@RestController
@RequestMapping(path="/file")
public class FileController {

    @Resource
    private FileService fileService;


    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("400","没有上传文件");
        }

        String url = fileService.fileUploadService(file);

        if(url == null)  {
            return Result.error("500","文件上传时错误");
        }
        return Result.success(url);
    }
}
