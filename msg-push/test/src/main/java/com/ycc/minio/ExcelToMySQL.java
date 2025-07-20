package com.ycc.minio;

import io.minio.MinioClient;
import io.minio.GetObjectArgs;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ExcelToMySQL {
    private static final String MINIO_URL = "http://your-minio-url:9000";
    private static final String MINIO_ACCESS_KEY = "your-access-key";
    private static final String MINIO_SECRET_KEY = "your-secret-key";
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/yourDatabase";
    private static final String DATABASE_USER = "yourUser";
    private static final String DATABASE_PASSWORD = "yourPassword";
    private static final int BATCH_SIZE = 1000; // 每批处理1000条记录

    public static void main(String[] args) {
        try {
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(MINIO_URL)
                    .credentials(MINIO_ACCESS_KEY, MINIO_SECRET_KEY)
                    .build();

            // 下载Excel文件
            InputStream excelStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket("your-bucket")
                            .object("path/to/your/excel.xlsx")
                            .build()
            );

            // 读取Excel文件
            Workbook workbook = new XSSFWorkbook(excelStream);
            Sheet sheet = workbook.getSheetAt(0);

            try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {
                String insertSQL = "INSERT INTO your_table (column1, column2, column3) VALUES (?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                    int count = 0;

                    for (Row row : sheet) {
                        if (row.getRowNum() == 0) { // 跳过标题行
                            continue;
                        }

                        // 假设Excel的列数和类型，实际使用中请根据需要调整
                        preparedStatement.setString(1, row.getCell(0).getStringCellValue());
                        preparedStatement.setString(2, row.getCell(1).getStringCellValue());
                        preparedStatement.setDouble(3, row.getCell(2).getNumericCellValue());

                        preparedStatement.addBatch();
                        count++;

                        if (count % BATCH_SIZE == 0) {
                            preparedStatement.executeBatch(); // 执行批处理
                        }
                    }
                    preparedStatement.executeBatch(); // 执行剩余的记录
                }
            }
            workbook.close();
            excelStream.close();
            System.out.println("Data imported successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}