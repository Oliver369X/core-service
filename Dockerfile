# Etapa de compilación
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B clean package -DskipTests

# Etapa de runtime
FROM eclipse-temurin:17-jre-alpine
ENV JAVA_OPTS=""
WORKDIR /app
COPY --from=builder /workspace/target/core-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 5010
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

