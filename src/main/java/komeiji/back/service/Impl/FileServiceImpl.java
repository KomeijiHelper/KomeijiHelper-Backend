package komeiji.back.service.Impl;

import komeiji.back.service.FileService;
import net.coobird.thumbnailator.Thumbnailator;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.*;
import java.nio.file.Files;

@Service
public class FileServiceImpl implements FileService {

    private static final Tika tika = new Tika();

    private static final File uploadDir = new File("/var/www/file/image");
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    private static final String urlHead = "https://komeiji.cyou/file/image/";
//    private static final String urlHead = "http://localhost:3000/";

    static {
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            logger.error("error in upload dir {}", uploadDir);
            System.exit(1);
        }
    }

    private String fileName;

    @Override
    public String fileUploadService(MultipartFile file) {
        String fileName;
        byte[] data;
        try {
            data = file.getBytes();
        } catch (IOException e) {
            logger.error("error when get file bytes: {}", e.getMessage());
            return null;
        }
        try {
            fileName = fileMD5(data);
            fileName += ".jpeg";
        } catch (IOException e) {
            logger.error("error when get md5: {}", e.getMessage());
            return null;
        }

        File dst = new File(uploadDir, fileName);
        if (dst.exists()) {
            return urlHead + fileName;
        }
        try {
            storeFile(data,file.getOriginalFilename(), dst);
        } catch (IOException e) {
            logger.error("error to upload file {}:{}", dst, e.getMessage());
            return null;
        }

        return urlHead + fileName;
    }

    private String fileMD5(byte[] data) throws IOException {
        return DigestUtils.md5Hex(data);
    }

    private String getFileType(byte[] data,String name) {
        return tika.detect(data,name);
    }

    private void storeFile(byte[] data,String name, File dst) throws IOException {
        String mimeType = getFileType(data,name);
        if(mimeType != null && mimeType.startsWith("image/")) {
            Thumbnails.of(ImageIO.read(new ByteArrayInputStream(data)))
                    .scale(1).outputQuality(0.5).toFile(dst);
        }
        else {
            Files.write(dst.toPath(),data);
        }
    }
}
