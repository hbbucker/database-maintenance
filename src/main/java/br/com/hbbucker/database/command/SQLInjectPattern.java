package br.com.hbbucker.database.command;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.regex.Pattern;

@UtilityClass
class SQLInjectPattern {
    public static final Pattern[] SQL_INJECTION_PATTERNS = {
            // Condições sempre verdadeiras
            Pattern.compile("(?i)\\bOR\\s+1=1\\b"),
            Pattern.compile("(?i)\\b'\\s+OR\\s+'.+=.+\\b"),
            Pattern.compile("(?i)\\b'\\s+OR\\s+'1'='1\\b"),

            // Comentários para ignorar o restante da consulta
            Pattern.compile("--"),
            Pattern.compile("#"),

            // Union-based SQL Injection
            Pattern.compile("(?i)\\bUNION\\b"),
            Pattern.compile("(?i)\\bUNION\\s+SELECT\\b"),

            // Subconsultas maliciosas
            Pattern.compile("(?i)\\bSELECT\\s+COUNT\\s*\\(\\*\\)\\b"),

            // Comandos de exclusão ou alteração
            Pattern.compile("(?i)\\bDROP\\s+TABLE\\b"),
            Pattern.compile("(?i)\\bALTER\\s+TABLE\\b"),
            Pattern.compile("(?i)\\bINSERT\\s+INTO\\b"),
            Pattern.compile("(?i)\\bDELETE\\s+FROM\\b"),

            // Sleep ou atrasos
            Pattern.compile("(?i)\\bWAITFOR\\s+DELAY\\b"),
            Pattern.compile("(?i)\\bSLEEP\\s*\\(\\d+\\)\\b"),

            // Subconsultas booleanas
            Pattern.compile("(?i)\\bAND\\s+1=1\\b"),
            Pattern.compile("(?i)\\bAND\\s+1=2\\b")
    };

    public static final List<String> REJECT_KEY_WORDS = List
            .of("select", "union", "insert", "update", "delete", "join", "truncate", "table", "--", ";", "'");
}
