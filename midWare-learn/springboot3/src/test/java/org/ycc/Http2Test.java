package org.ycc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HTTP/2 集成测试类（支持 HTTPS）
 * 会自动启动 Spring Boot 应用
 *
 * @author chengcheng.yan
 * @version 1.0.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Http2Test {

    @LocalServerPort
    private int port;

    /**
     * 创建忽略 SSL 证书验证的 HttpClient（用于测试自签名证书）
     */
    private HttpClient createInsecureHttpClient() throws Exception {
        // 创建信任所有证书的 TrustManager
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new SecureRandom());

        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .sslContext(sslContext)
                .build();
    }

    @Test
    public void testHttp2HealthEndpoint() throws Exception {
        HttpClient client = createInsecureHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://localhost:" + port + "/health"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("=== HTTP/2 健康检测测试 ===");
        System.out.println("端口: " + port);
        System.out.println("HTTP 版本: " + response.version());
        System.out.println("状态码: " + response.statusCode());
        System.out.println("响应头: " + response.headers());
        System.out.println("响应体: " + response.body());

        // 验证
        assertEquals(200, response.statusCode(), "HTTP 状态码应该是 200");
        assertTrue(response.body().contains("\"status\":\"UP\""), "响应应该包含 status:UP");
        assertTrue(response.body().contains("\"timestamp\":"), "响应应该包含 timestamp");
        
        if (response.version() == HttpClient.Version.HTTP_2) {
            System.out.println("\n✅ 成功使用 HTTP/2 协议！");
        } else {
            System.out.println("\n⚠️  使用的是 " + response.version() + "，不是 HTTP/2");
        }
    }

    @Test
    public void testHttp2Hi1Endpoint() throws Exception {
        HttpClient client = createInsecureHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://localhost:" + port + "/hi1"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("=== HTTP/2 /hi1 测试 ===");
        System.out.println("端口: " + port);
        System.out.println("HTTP 版本: " + response.version());
        System.out.println("状态码: " + response.statusCode());
        System.out.println("响应体: " + response.body());
        
        assertEquals(200, response.statusCode(), "HTTP 状态码应该是 200");
    }

    public static void main(String[] args) throws Exception {
        System.out.println("请使用 JUnit 运行此测试类");
        System.out.println("此测试会自动启动 Spring Boot 应用");
    }
}
