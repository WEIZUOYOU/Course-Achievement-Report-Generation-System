FROM maven:3.8-openjdk-17 AS builder
ENV LANG=C.UTF-8
ENV JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8
WORKDIR /app
COPY pom.xml .
COPY ruoyi-admin/ ruoyi-admin/
COPY ruoyi-common/ ruoyi-common/
COPY ruoyi-framework/ ruoyi-framework/
COPY ruoyi-generator/ ruoyi-generator/
COPY ruoyi-quartz/ ruoyi-quartz/
COPY ruoyi-system/ ruoyi-system/
# 使用 package 即可，不需要 dependency:list 和 -Pproduction
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
ENV LANG=C.UTF-8
ENV JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8
WORKDIR /app
COPY --from=builder /app/ruoyi-admin/target/ruoyi-admin.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]