@echo off
echo ========================================
echo 生成自签名 SSL 证书
echo ========================================
echo.

cd /d "%~dp0src\main\resources"

echo 正在生成证书...
keytool -genkeypair ^
  -alias tomcat ^
  -keyalg RSA ^
  -keysize 2048 ^
  -storetype PKCS12 ^
  -keystore keystore.p12 ^
  -validity 3650 ^
  -storepass changeit ^
  -dname "CN=localhost, OU=Development, O=YCC, L=Beijing, ST=Beijing, C=CN"

echo.
if %errorlevel% equ 0 (
    echo ✅ 证书生成成功！
    echo 证书位置: %~dp0src\main\resources\keystore.p12
) else (
    echo ❌ 证书生成失败！
    echo 请确保已安装 JDK 并配置 JAVA_HOME
)

echo.
pause
