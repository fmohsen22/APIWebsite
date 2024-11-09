# Use Java 17 image to build the application
FROM openjdk:17-jdk-slim AS build

WORKDIR /app

# Copy the Maven wrapper files and source code
COPY . .

# Make the Maven wrapper executable and build the project
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Use Java 17 image to run the application
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/target/mosi-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the jar file
CMD ["java", "-jar", "/app/app.jar"]
