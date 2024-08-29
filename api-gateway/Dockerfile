FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY target/api-gateway.jar /app/api-gateway.jar
EXPOSE 8080-8090
EXPOSE 8761
CMD ["java", "-jar", "/app/api-gateway.jar"]