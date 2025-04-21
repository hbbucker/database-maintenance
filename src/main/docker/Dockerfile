#FROM quay.io/quarkus/quarkus-micro-image:2.0
#COPY target/*-runner.jar /application.jar
#CMD ["java", "-jar", "/application.jar"]
# quay.io/quarkus/ubi-quarkus-mandrel-builder-image:23.1-jdk-21
# Stage 1: Build the native executable
FROM quay.io/quarkus/ubi-quarkus-mandrel-builder-image:23.0-jdk-17 AS build

USER root
RUN microdnf module enable -y nodejs:20 && \
    microdnf install -y nodejs && \
    npm install -g npm@latest && \
    microdnf clean all

# Set the working directory
WORKDIR /workspace

# Copy the Maven project files
COPY pom.xml ./
COPY src ./src
COPY .mvn ./.mvn
COPY mvnw ./

# Build the native executable
RUN ./mvnw package -Pnative #-Dquarkus.native.container-build=true

# Stage 2: Create a minimal runtime image
FROM registry.access.redhat.com/ubi8/ubi-minimal:8.7

# Set the working directory
WORKDIR /app

# Copy the native executable from the build stage
COPY --from=build /workspace/target/*-runner /app/application

# Expose the application port
EXPOSE 8080

# Set the default command to run the application
CMD ["./application"]