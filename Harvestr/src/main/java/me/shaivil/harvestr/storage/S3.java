package me.shaivil.harvestr.storage;

import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.MinioException;
import me.shaivil.loggerutil.LogType;
import me.shaivil.loggerutil.Logger;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class S3 {
    public static void uploadFile(String filePath, String fileName){
        try {
            //Connect to S3
            MinioClient minioClient = MinioClient.builder()
                    .endpoint("http://10.158.57.20:9000")
                    .credentials("2KzK7cPs08AZ83fulWGM", "9kEQ7x6Z71R8BlcwcEAl4Bme3JhFX7kFPfFfXWKt")
                    .build();

            //Upload file to S3
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket("data-collection")
                            .object(fileName)
                            .filename(filePath)
                            .build());

            Logger.log(LogType.SUCCESS, "Successfully uploaded file " + fileName);
        } catch (MinioException | IOException e) {
            Logger.log(LogType.ERROR, "Error while uploading file " + fileName);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }
}
