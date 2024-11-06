# Use an official OpenJDK runtime as the base image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the project files to the container
COPY . .

# Make the Maven wrapper executable and build the project
RUN chmod +x mvnw && ./mvnw clean package

# Set the command to run the JAR file
CMD ["java", "-jar", "target/mosi-0.0.1-SNAPSHOT.jar"]
