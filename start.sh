#!/bin/bash
export DATABASE_CONFIG_FILE=$(pwd)/../index-maintenance.conf
export JAVA_HOME=/usr/lib/jvm/java-1.21.0-openjdk-amd64
mvn clean install
mvn quarkus:dev -Dquarkus.http.port=8181
