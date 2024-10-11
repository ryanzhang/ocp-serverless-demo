# Makefile for building and running the Spring Boot application

# Variables
IMAGE_NAME=quay.io/rzhang/longprocessapp
TAG=$(shell git rev-parse --short HEAD 2>/dev/null || echo latest)
DOCKERFILE_PATH=src/main/container/Dockerfile
CONTEXT=.

.PHONY: image dev

# Default tag if Git tag is not available
TAG ?= latest

# Target to build the Docker image
image:
	@echo "Building Docker image..."
	podman build --platform linux/amd64 -f $(DOCKERFILE_PATH) -t $(IMAGE_NAME):$(TAG) $(CONTEXT)
	podman tag $(IMAGE_NAME):$(TAG) $(IMAGE_NAME):latest
	@echo "Docker image built and tagged as $(IMAGE_NAME):$(TAG)"

# Target to package and run the application locally
dev: check_slack_token
	@echo "Packaging the application..."
	mvn clean package -DskipTests
	@echo "Starting the application..."
	java -jar target/*.jar

# Target to check if SLACK_TOKEN environment variable is set
check_slack_token:
	@if [ -z "$$slack_token" ]; then \
		echo "Error: slack_token environment variable is not set. In order to run this app, you need to set slack_token, check README.md"; \
		exit 1; \
	else \
		echo "slack_token is set."; \
	fi
