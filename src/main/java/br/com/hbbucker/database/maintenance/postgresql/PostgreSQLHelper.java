package br.com.hbbucker.database.maintenance.postgresql;

import lombok.experimental.UtilityClass;

@UtilityClass
class PostgreSQLHelper {
    public static final String INDEX_CONCURRENTLY = "INDEX CONCURRENTLY %s";
    public static final String DROP_INDEX = "DROP INDEX CONCURRENTLY IF EXISTS %s.%s";
    public static final String ALTER_INDEX = "ALTER INDEX %s.%s RENAME TO %s";
    public static final String INDEX = "INDEX %s";

    public static final String SQL_INDEX_INFO = """
            SELECT schemaname as "schema"
                 , indexrelname AS index_name
                 , relname AS table_name
                 , null AS bloat_ratio
                 , pg_get_indexdef((schemaname || '.' || indexrelname)::regclass) as ddl
              FROM pg_stat_all_indexes
             WHERE schemaname = '%s'
               AND indexrelname = '%s'
            """;

    //  query from https://wiki.postgresql.org/wiki/Show_database_bloat
    public static final String SQL_BLOATED_INDEX = """        
                         SELECT schemaname AS "schema"
                              , iname AS index_name
                              , tablename AS table_name
                              , ROUND((CASE WHEN iotta=0 OR ipages=0 THEN 0.0 ELSE ipages::float/iotta END)::numeric,1) AS bloat_ratio
                              , pg_get_indexdef((schemaname || '.' || iname)::regclass) as ddl
                           FROM (SELECT schemaname
                                      , tablename
                                      , cc.reltuples
                                      , cc.relpages
                                      , bs
                                      , CEIL((cc.reltuples*((datahdr+ma-(CASE WHEN datahdr%ma=0 THEN ma ELSE datahdr%ma END))+nullhdr2+4))/(bs-20::float)) AS otta
                                      , COALESCE(c2.relname, i.INDRELID::text) AS iname, COALESCE(c2.reltuples,0) AS ituples, COALESCE(c2.relpages,0) AS ipages
                                      , COALESCE(CEIL((c2.reltuples*(datahdr-12))/(bs-20::float)),0) AS iotta
                                   FROM (SELECT ma
                                              , bs
                                              , schemaname
                                              , tablename
                                              , (datawidth+(hdr+ma-(case when hdr%ma=0 THEN ma ELSE hdr%ma END)))::numeric AS datahdr
                                              , (maxfracsum*(nullhdr+ma-(case when nullhdr%ma=0 THEN ma ELSE nullhdr%ma END))) AS nullhdr2
                                           FROM (SELECT schemaname
                                                      , tablename
                                                      , hdr
                                                      , ma
                                                      , bs
                                                      , SUM((1-null_frac)*avg_width) AS datawidth
                                                      , MAX(null_frac) AS maxfracsum
                                                      , hdr+(SELECT 1+count(*)/8 FROM pg_stats s2 WHERE null_frac<>0 AND s2.schemaname = s.schemaname AND s2.tablename = s.tablename) AS nullhdr
                                                   FROM pg_stats s
                                                      , (SELECT (SELECT current_setting('block_size')::numeric) AS bs
                                                              , CASE WHEN substring(v,12,3) IN ('8.0','8.1','8.2') THEN 27 ELSE 23 END AS hdr
                                                              , CASE WHEN v ~ 'mingw32' THEN 8 ELSE 4 END AS ma
                                                           FROM (SELECT version() AS v) AS foo) AS constants
                                                  GROUP BY 1,2,3,4,5) AS foo) AS rs
                                   JOIN pg_class cc ON cc.relname = rs.tablename
                                   JOIN pg_namespace nn ON cc.relnamespace = nn.oid AND nn.nspname = rs.schemaname AND nn.nspname <> 'information_schema'
                              LEFT JOIN pg_index i ON indrelid = cc.oid AND NOT i.indisprimary
                              LEFT JOIN pg_class c2 ON c2.oid = i.indexrelid) AS sml
                          WHERE SCHEMANAME NOT IN ('pg_catalog')
                            AND ROUND((CASE WHEN iotta=0 OR ipages=0 THEN 0.0 ELSE ipages::float/iotta END)::numeric,1) > 0.1
                          ORDER BY bloat_ratio DESC
            """;

}
