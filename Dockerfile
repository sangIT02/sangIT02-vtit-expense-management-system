# Stage 1: Môi trường Build (Dùng image có sẵn Maven và JDK 17)
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
LABEL authors="PC"

WORKDIR /app
# Copy file pom.xml vào trước để tận dụng cache của Docker
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy toàn bộ source code và thực hiện build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Môi trường chạy (Chỉ lấy file .jar từ Stage 1, giúp image cực nhẹ)
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Sửa lại đường dẫn target một chút để chắc chắn lấy đúng file jar
COPY --from=builder /app/target/*.jar app.jar

# Expose port (Thay đổi nếu ứng dụng của bạn chạy port khác 8080)
EXPOSE 8080

# Khởi chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]