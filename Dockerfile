FROM maven:3.9.8-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN mvn -pl account-service -am -B clean package -DskipTests  # <-- change module name

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/account-service/target/account-service-*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
