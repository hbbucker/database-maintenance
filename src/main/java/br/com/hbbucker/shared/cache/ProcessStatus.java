package br.com.hbbucker.shared.cache;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record ProcessStatus(String status) {
}
