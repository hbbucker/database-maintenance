# DOCKER.md

## Running the Application with Docker

This document provides detailed instructions on how to run the application using Docker, including the required parameters, environment variables, and configuration details specific to the current project.

---

## 1. Prerequisites

Before running the application, ensure you have the following installed:
- Docker (version 20.10 or later)
- Docker Compose (if using a `docker-compose.yml` file)

---

## 2. Building the Docker Image

To build the Docker image for the application, use the following command:

```bash
docker build -f src/main/docker/Dockerfile -t index-maintenance-app:latest .
```
---

## 3. Environment Variables

The application requires several environment variables to be set for proper configuration. Below is a detailed explanation of each variable:

### **Database Configuration**
| Variable Name          | Description                                 | Example Value        |
|------------------------|---------------------------------------------|----------------------|
| `DATABASE_CONFIG_FILE` | Configuration file to database connections. | `/app/database.conf` |

### **Volumes**
Map the configuration file to the container:
```yaml
volumes:
  - ./database.conf:/app/database.conf
```
---

## 4. Running the Docker Container

To run the application, use the following command:

```bash
docker run -dit \
  --name index-maintenance-app \
  -p 8080:8080 \
  -v "$(pwd)/index-maintenance.conf:/app/database.conf" \
  -e DATABASE_CONFIG_FILE=/app/database.conf \
  hbbucker/index-maintenance

```

### Explanation of Parameters:
- `-d`: Runs the container in detached mode.
- `--name`: Assigns a name to the container.
- `-p 8080:8080`: Maps port 8080 on the host to port 8080 in the container.
- `-e`: Sets environment variables required by the application.
- `-v`: Mounts a volume to the container, allowing you to use a local configuration file.
---

## 5. Using a `docker-compose.yml` File

To start the application using Docker Compose, run:

```bash
docker-compose -f src/main/docker/Dockerfile up -d
```

---

## 6. Verifying the Application

After starting the container, verify that the application is running by accessing it in your browser or using a tool like `curl`:

```bash
curl http://localhost:8080
```

---

This document provides all the necessary details to run the application using Docker. If you encounter any issues, refer to the troubleshooting section or consult the application logs for more information.