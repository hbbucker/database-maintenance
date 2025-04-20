package br.com.hbbucker.database.command;

import io.quarkus.logging.Log;
import lombok.experimental.UtilityClass;

import java.util.Arrays;

import static br.com.hbbucker.database.command.SQLInjectPattern.REJECT_KEY_WORDS;
import static br.com.hbbucker.database.command.SQLInjectPattern.SQL_INJECTION_PATTERNS;

@UtilityClass
class SQLInjectionMonitor {
    public static void monitorSQLInjection(String sqlQuery) {
        if (isEmptyOrNull(sqlQuery) || isPotentialSQLInjection(sqlQuery)) {
            logSQLInjectionAttempt(sqlQuery);
        }
    }

    private static boolean isPotentialSQLInjection(String sqlQuery) {
        return Arrays.stream(SQL_INJECTION_PATTERNS)
                .anyMatch(pattern -> pattern.matcher(sqlQuery).find());
    }

    public static void monitorDDLInjection(String ddlQuery) {

        if (isEmptyOrNull(ddlQuery) || isPotentialDDLInjection(ddlQuery)) {
            logSQLInjectionAttempt(ddlQuery);
        }
    }

    private static boolean isEmptyOrNull(String str) {
        return str == null || str.trim().isEmpty();
    }

    private static boolean isPotentialDDLInjection(String sqlQuery) {
        return Arrays.stream(sqlQuery.toLowerCase().split(" "))
                .anyMatch(REJECT_KEY_WORDS::contains);
    }

    private static void logSQLInjectionAttempt(String sqlQuery) {
        Log.error("Potential SQL Injection Attempt Detected: " + sqlQuery);
        throw new RuntimeException("Potential SQL Injection Attempt Detected");
    }
}
