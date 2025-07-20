package com.ycc.minio;

import io.minio.MinioClient;
import io.minio.GetObjectArgs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MinIOExportExcel {
    private static final String MINIO_URL = "http://your-minio-url:9000";
    private static final String MINIO_ACCESS_KEY = "your-access-key";
    private static final String MINIO_SECRET_KEY = "your-secret-key";
    private static final String BUCKET_NAME = "your-bucket";
    private static final String OBJECT_NAME = "path/to/your/excel.xlsx";
    private static final String LOCAL_FILE_PATH = "path/to/save/excel.xlsx";

    public static void main(String[] args) {
        try {
            // 创建MinIO客户端
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(MINIO_URL)
                    .credentials(MINIO_ACCESS_KEY, MINIO_SECRET_KEY)
                    .build();

            // 获取对象，并保存到本地文件
            try (InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(OBJECT_NAME)
                            .build());
                 FileOutputStream outputStream = new FileOutputStream(new File(LOCAL_FILE_PATH))) {

                byte[] buffer = new byte[1024];
                int bytesRead;

                // 循环从输入流读取数据并写入输出流
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                System.out.println("Excel file downloaded successfully to " + LOCAL_FILE_PATH);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}