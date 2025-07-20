package com.ycc.minio;

import io.minio.*;
import io.minio.errors.*;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ThreadLocalRandom;

public class LargeExcelUploader {

    public static void main(String[] args) {
        String endpoint = "http://127.0.0.1:9000";
        String accessKey = "YOUR_ACCESS_KEY";
        String secretKey = "YOUR_SECRET_KEY";
        String bucketName = "excel-bucket";
        String objectName = "large_excel_800mb.xlsx";

        try {
            // 初始化 MinIO 客户端
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();

            // 创建存储桶（如果不存在）
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            // 生成临时大文件
            File tempFile = generateLargeExcel(800 * 1024 * 1024); // 800 MB

            // 上传到 MinIO
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .filename(tempFile.getAbsolutePath())
                            .build());

            System.out.println("文件已上传至 MinIO: " + objectName);

            // 删除临时文件
            if (tempFile.exists()) {
                tempFile.delete();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成一个大约指定大小的 Excel 文件
     */
    private static File generateLargeExcel(long targetSizeBytes) throws IOException {
        File tempFile = File.createTempFile("large_excel_", ".xlsx");
        tempFile.deleteOnExit();

        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) { // 内存保留100行
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Data");

            int rowsWritten = 0;
            byte[] dummyData = new byte[1024]; // 每个单元格填充约1KB数据
            ThreadLocalRandom.current().nextBytes(dummyData);

            while (new File(tempFile.getAbsolutePath()).length() < targetSizeBytes) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowsWritten++);
                org.apache.poi.ss.usermodel.Cell cell = row.createCell(0);
                cell.setCellValue(new String(dummyData));
            }

            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                workbook.write(fos);
            }
        }

        return tempFile;
    }
}
