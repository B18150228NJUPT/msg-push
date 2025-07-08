package com.ycc.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author chengcheng.yan@youhualin.com
 * @Description
 * @createTime 2025/7/7
 * @Copyright 悠桦林信息科技（上海）有限公司
 * @Version 1.0
 */
public class TcpServer {
    public static void main(String[] args) {
        int port = 7741; // 指定服务器端口

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("服务器已启动，等待客户端连接...");

            while (true) {
                Socket clientSocket = serverSocket.accept(); // 接受客户端连接
                System.out.println("客户端已连接：" + clientSocket.getInetAddress());

                // 启动一个新线程处理客户端通信
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 客户端处理线程类
    static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
            ) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("收到客户端消息: " + inputLine);
                    out.println("服务器回复: " + inputLine); // 将收到的消息回传给客户端
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close(); // 关闭客户端连接
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

