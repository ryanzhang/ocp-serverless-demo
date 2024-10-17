# Dockerfile

# Stage 1: Build the Vue.js application
FROM registry.access.redhat.com/ubi9/nodejs-20 AS build

# Set working directory
WORKDIR /opt/app-root/src

# Copy package.json and package-lock.json
COPY package*.json ./

# Install dependencies
RUN npm install --registry=http://mirrors.cloud.tencent.com/npm --verbose

# Copy the rest of the application code
COPY . .

# Build the application
RUN npm run build

# Stage 2: Serve the application with Nginx
FROM registry.access.redhat.com/ubi9/nginx-124:latest

USER root
# Remove the default Nginx configuration
RUN rm -rf /etc/nginx/nginx.conf /etc/nginx/conf.d

# Copy custom Nginx configuration
COPY nginx.conf /etc/nginx/nginx.conf

# Copy the build output to Nginx's html directory
COPY --from=build /opt/app-root/src/dist /usr/share/nginx/html

# Expose port 80
EXPOSE 8080

USER 1001

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]
