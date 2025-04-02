# Use an official OpenJDK runtime as the base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy pom.xml
COPY pom.xml .

# Copy source code
COPY src src

# Copy the JAR file built in the previous step
#COPY target/*.jar app.jar

# Expose the application port (update if your app uses a different port)
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]