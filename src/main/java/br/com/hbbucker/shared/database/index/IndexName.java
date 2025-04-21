package br.com.hbbucker.shared.database.index;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record IndexName(String name) {
}
