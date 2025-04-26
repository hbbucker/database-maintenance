package br.com.hbbucker.database.command.validation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public final class SQLValidator {

    public void validateQuery(final String sql) {
        SQLInjectionMonitor.validateSQLQuery(sql);
    }

    public void validateDDL(final String ddl) {
        SQLInjectionMonitor.validateDDLInjection(ddl);
    }
}
