package com.ycc.tcp;

import java.io.*;
import java.net.Socket;

/**
 * @author chengcheng.yan@youhualin.com
 * @Description TCP 客户端示例
 * @createTime 2025/7/7
 * @Copyright 悠桦林信息科技（上海）有限公司
 * @Version 1.0
 */
public class TcpClient {
    private static final String SERVER_HOST = "localhost"; // 服务器地址
    private static final int SERVER_PORT = 7741; // 与服务端保持一致的端口

    public static void main(String[] args) {
        try (
            Socket socket = new Socket(SERVER_HOST, SERVER_PORT); // 连接服务器
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            System.out.println("已连接到服务器");

            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput); // 发送消息给服务器
                String response = in.readLine(); // 接收服务器响应
                if (response == null) {
                    System.out.println("服务器已关闭连接");
                    break;
                }
                System.out.println("收到服务器回复: " + response);
            }
        } catch (IOException e) {
            System.err.println("客户端发生异常: ");
            e.printStackTrace();
        }
    }
}
