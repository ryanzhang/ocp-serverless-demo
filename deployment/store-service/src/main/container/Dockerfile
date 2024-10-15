# Use Red Hat Universal Base Image (UBI) 9 with OpenJDK 17 Runtime
FROM registry.access.redhat.com/ubi9/openjdk-17-runtime:latest

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged jar file into the container
COPY target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# Set environment variables (optional)
# ENV SLACK_TOKEN=${SLACK_TOKEN}

# Define the entry point to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
