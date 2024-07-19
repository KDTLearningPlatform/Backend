package ac.su.learningplatform.service;

import ac.su.learningplatform.util.CommonUtils;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.mp4.Mp4Directory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String uploadFile(MultipartFile multipartFile) {

        if(multipartFile.isEmpty()) {
            log.info("image is null");
            return "";
        }

        String fileName = getFileName(multipartFile);

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(multipartFile.getContentType());
            metadata.setContentLength(multipartFile.getSize());
            amazonS3.putObject(bucketName, fileName, multipartFile.getInputStream(), metadata);
        } catch (IOException e) {
            log.error("cannot upload image", e);
            throw new RuntimeException(e);
        }

        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    private String getFileName(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) return "";
        return CommonUtils.buildFileName(Objects.requireNonNull(multipartFile.getOriginalFilename()));
    }

    public int getVideoDuration(MultipartFile file) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file.getInputStream());
            Mp4Directory directory = metadata.getFirstDirectoryOfType(Mp4Directory.class);
            if (directory != null) {
                long durationInSeconds = directory.getLong(Mp4Directory.TAG_DURATION) / directory.getLong(Mp4Directory.TAG_TIME_SCALE);
                return (int) durationInSeconds;
            }
        } catch (Exception e) {
            log.error("Error reading video duration", e);
        }
        return 0;
    }
}