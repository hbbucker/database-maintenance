package br.com.hbbucker.shared.database.table;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record TableName(String name) {
}
