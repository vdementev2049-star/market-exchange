
# Stage 1: Build the Java application with Maven.
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy the Maven project configuration.
COPY pom.xml .

# Copy the Java source code.
COPY src ./src

# Compile the application and create an executable JAR.
RUN mvn -B -DskipTests package

# Stage 2: Run the application with a smaller Java image.
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the compiled JAR from the build stage.
COPY --from=build /app/target/*.jar app.jar

# Document the port used by Spring Boot.
EXPOSE 8080

# Start the application when the container runs.
ENTRYPOINT ["java", "-jar", "app.jar"]
