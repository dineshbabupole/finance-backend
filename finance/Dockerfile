# Use Java 17
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy maven files
COPY pom.xml .
COPY src ./src

# Install maven and build
RUN apk add --no-cache maven && \
    mvn clean package -DskipTests

# Run the jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "target/finance-0.0.1-SNAPSHOT.jar"]