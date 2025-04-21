package br.com.hbbucker.shared.database;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record DataSourceName(String name) {
}
