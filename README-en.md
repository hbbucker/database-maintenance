# 🛠️ Database Index Maintenance Service

![Java](https://img.shields.io/badge/Language-Java-yellow)
![Public](https://img.shields.io/badge/Visibility-Public-brightgreen)

This project automates the process of identifying and recreating indexes with high levels of *bloat*
in databases, using Java with Quarkus, Apache Camel, and Prometheus for observability.

---

## 🚀 Features

- 🔍 Automatically identifies indexes with high bloat (`> 1`)
- ❌ Ignores primary key indexes for safety
- 🏗️ Recreates indexes with the same original configuration
- 🔄 Safely drops the old index
- 🔁 Renames the new index to the original name
- 📊 Observability with **Micrometer + Prometheus**

## TODO
- 🔒 Lock detection and handling: aborts if blocking other processes, waits with timeout if being blocked
- 🌐 REST endpoints for monitoring process status

---

## ⚙️ Technologies

- [Quarkus](https://quarkus.io/)
- [Apache Camel](https://camel.apache.org/)
- [PostgreSQL JDBC](https://jdbc.postgresql.org/)
- [Micrometer](https://micrometer.io/)
- [Prometheus](https://prometheus.io/)
- [Docker](https://www.docker.com/)

---

## 🧪 How to Test Locally

### Requirements

- Docker and Docker Compose
- Java 17+
- Maven 3.8+

### Starting the environment:

```bash
docker-compose up -d
```

## Running locally:
```bash
./mvnw quarkus:dev
```

## 🔧 API Endpoints

| Method | Route | Description |
|--------|-------|-------------|
| GET    | /health | Checks application health |
| GET    | /metrics | Prometheus metrics |
| POST   | /index-maintenance/start | Starts the maintenance process |
| GET    | /index-maintenance/status | Retrieves maintenance process status |

## ⚠️ Best Practices and Recommendations

- This process does **not** act on PK/FK indexes by default.
- Ensure it runs during low-traffic periods.
- Always test in a staging environment before production.
- Use the status and metrics endpoints for continuous monitoring.

## 📈 Example Exported Metrics

- index_maintenance_bloat_detected_total
- index_maintenance_success_total
- index_maintenance_failure_total
- index_maintenance_duration_seconds

## 👥 Contribution

Contributions are welcome! Suggestions, improvements, and PRs are encouraged.

## Contact

Creator: [hbbucker](https://github.com/hbbucker)