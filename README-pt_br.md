# 🛠️ Dababase Index Maintenance Service

![Java](https://img.shields.io/badge/Language-Java-yellow)
![Public](https://img.shields.io/badge/Visibility-Public-brightgreen)

Este projeto automatiza o processo de identificação e recriação de índices com alto nível de *bloat* 
em bancos de dados, utilizando Java com Quarkus, Apache Camel e Prometheus para observabilidade.

---

## 🚀 Funcionalidades

- 🔍 Identificação automática de índices com bloat elevado (`> 1`)
- ❌ Ignora índices de chaves primárias por segurança
- 🏗️ Recriação de índices com as mesmas configurações originais
- 🔄 Exclusão segura do índice antigo
- 🔁 Renomeação do índice novo com o nome original
- 📊 Observabilidade com **Micrometer + Prometheus**

## TODO 
- 🔒 Detecção e controle de **locks**: aborta se travando outros processos, aguarda com timeout se estiver sendo travado
- 🌐 Endpoints REST para acompanhamento do processo

---

## ⚙️ Tecnologias

- [Quarkus](https://quarkus.io/)
- [Apache Camel](https://camel.apache.org/)
- [PostgreSQL JDBC](https://jdbc.postgresql.org/)
- [Micrometer](https://micrometer.io/)
- [Prometheus](https://prometheus.io/)
- [Docker](https://www.docker.com/)

---

## 🧪 Como testar localmente

### Requisitos

- Docker e Docker Compose
- Java 17+
- Maven 3.8+

### Subindo o ambiente:

```bash
docker-compose up -d
```

## Rodando localmente:
```bash
./mvnw quarkus:dev
```
## 🔧 Endpoints de API
| Método | Rota | Descrição |
|-------|------|-----------|
| GET   | /health | Verifica saúde da aplicação |
| GET   | /metrics | Métricas Prometheus |
| POST  | /index-maintenance/start | Inicia o processo de manutenção |
| GET   | /index-maintenance/status | Consulta andamento do processo |

## ⚠️ Cuidados e boas práticas
- Este processo não atua sobre índices de PK/FK por padrão.
- Certifique-se de rodar em horários de baixa concorrência.
- Use o ambiente de staging para testes antes de produção.
- Utilize os endpoints de status e métricas para monitoramento contínuo.

## 📈 Exemplo de Métricas Exportadas
- index_maintenance_bloat_detected_total
- index_maintenance_success_total
- index_maintenance_failure_total
- index_maintenance_duration_seconds
  
## 👥 Contribuição

Contribuições são bem-vindas! Sugestões, melhorias e PRs são encorajados.

## Contato

Criador: [hbbucker](https://github.com/hbbucker)