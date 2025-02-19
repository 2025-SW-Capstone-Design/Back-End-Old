package soon.capstone.external.s3.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import soon.capstone.global.exception.common.S3FileUploadException;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class S3StorageProvider {

    private final S3Client s3Client;
    private final String bucketName;

    public S3StorageProvider(
        S3Client s3Client,
        @Value("${cloud.aws.s3.bucket}") String bucketName
    ) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    private void uploadFileToS3(MultipartFile file, String fileName) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(fileName)
            .contentType(file.getContentType())
            .build();

        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            log.error("IOException S3 파일 업로드 실패: {}", e.getMessage(), e);
            throw new S3FileUploadException();
        } catch (S3Exception e) {
            log.error("S3Exception AWS S3 업로드 오류: {}", e.awsErrorDetails().errorMessage(), e);
            throw new S3FileUploadException();
        }
    }

    private String getExtension(String originalFilename) {
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }

    private String generateFileName(String originalFilename) {
        return UUID.randomUUID() + getExtension(originalFilename);
    }

    private String getFileURL(String fileName) {
        GetUrlRequest getUrlRequest = GetUrlRequest.builder().bucket(bucketName).key(fileName).build();
        return String.valueOf(s3Client.utilities()
            .getUrl(getUrlRequest)
        );
    }

}