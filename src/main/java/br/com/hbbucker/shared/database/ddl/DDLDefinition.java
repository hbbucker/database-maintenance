package br.com.hbbucker.shared.database.ddl;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record DDLDefinition(String ddl) { }
