# Use the official Maven image to build the application
FROM maven:3.8.4-openjdk-11 AS build

# Set working directory
WORKDIR /app

# Copy the Maven wrapper and make it executable
COPY . .
RUN chmod +x mvnw

# Build the application using the Maven wrapper
RUN ./mvnw clean package

# Use the official Java image to run the application
FROM openjdk:11-jre-slim

# Set working directory
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/target/mosi-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the jar file
CMD ["java", "-jar", "/app/app.jar"]
