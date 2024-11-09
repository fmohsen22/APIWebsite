# Use an official OpenJDK runtime as the base image
FROM maven:3.8.5-openjdk-17 AS build

# Copy the project files to the container
COPY . .

# Make the Maven wrapper executable and build the project
RUN mvnw clean package


From openjdk:17.0.1-jdk-slim
COPY --from=build /target/demo-0.0.1-SNAPSHOT.jar demo.jar
EXPOSE 8080
# Set the command to run the JAR file
CMD ["java", "-jar", "target/mosi-0.0.1-SNAPSHOT.jar"]
