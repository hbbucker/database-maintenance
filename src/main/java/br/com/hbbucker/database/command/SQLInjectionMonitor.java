package br.com.hbbucker.database.command;

import io.quarkus.logging.Log;

import java.util.Arrays;

import static br.com.hbbucker.database.command.SQLInjectPattern.REJECT_KEY_WORDS;
import static br.com.hbbucker.database.command.SQLInjectPattern.SQL_INJECTION_PATTERNS;

final class SQLInjectionMonitor {

    private SQLInjectionMonitor() {
        // Prevent instantiation
    }

    public static void validateSQLQuery(final String sqlQuery) {
        if (isEmptyOrNull(sqlQuery) || containsSQLInjection(sqlQuery)) {
            logSQLInjectionAttempt(sqlQuery);
        }
    }

    private static boolean containsSQLInjection(final String sqlQuery) {
        return Arrays.stream(SQL_INJECTION_PATTERNS)
                .anyMatch(pattern -> pattern.matcher(sqlQuery).find());
    }

    public static void validateDDLInjection(final String ddlQuery) {

        if (isEmptyOrNull(ddlQuery) || isPotentialDDLInjection(ddlQuery)) {
            logSQLInjectionAttempt(ddlQuery);
        }
    }

    private static boolean isEmptyOrNull(final String str) {
        return str == null || str.trim().isEmpty();
    }

    private static boolean isPotentialDDLInjection(final String sqlQuery) {
        return Arrays.stream(sqlQuery.toLowerCase().split(" "))
                .anyMatch(REJECT_KEY_WORDS::contains);
    }

    private static void logSQLInjectionAttempt(final String sqlQuery) {
        Log.error("Potential SQL Injection Attempt Detected: " + sqlQuery);
        throw new RuntimeException("Potential SQL Injection Attempt Detected");
    }
}
