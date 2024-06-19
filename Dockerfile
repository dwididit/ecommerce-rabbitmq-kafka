# Use an official OpenJDK runtime as a parent image
FROM openjdk:21-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the executable JAR file to the container
COPY target/ecommerce-rabbitmq-kafka-0.0.1-SNAPSHOT.jar app.jar

# Expose the port that the application will run on
EXPOSE 9090

# Define the entry point for the container
ENTRYPOINT ["java", "-jar", "app.jar"]
