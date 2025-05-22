package soon.capstone.infrastructure.s3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;

@RequiredArgsConstructor
@Service
public class S3Service {

    private final S3Client s3Client;

    public byte[] getFileBytes(String bucketName, String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();

        ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
        return objectBytes.asByteArray();
    }

    public List<String> listFiles(String bucketName) {
        return s3Client.listObjectsV2Paginator(builder -> builder.bucket(bucketName).build())
            .contents()
            .stream()
            .map(S3Object::key)
            .toList();
    }

    public long getFileSize(String bucket, String key) {
        return s3Client.headObject(builder -> builder.bucket(bucket).key(key).build())
            .contentLength();
    }

}