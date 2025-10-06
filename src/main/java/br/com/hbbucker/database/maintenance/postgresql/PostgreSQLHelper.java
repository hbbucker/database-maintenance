package br.com.hbbucker.database.maintenance.postgresql;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
final class PostgreSQLHelper {

    private PostgreSQLHelper() {
        //do nothing
    }

    public static final String REINDEX_CONCURRENTLY = "REINDEX INDEX CONCURRENTLY %s.%s";

    public static final String SQL_GET_INDEX_INFO = """
            SELECT schemaname as "schema_name"
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
            WITH ix AS (
            SELECT current_database()
                 , schemaname as schema_name
                 , tablename as table_name
                 , ROUND((CASE WHEN otta=0 THEN 0.0 ELSE sml.relpages::float/otta END)::numeric(6,3),1) AS table_bloat_ratio
                 , CASE WHEN relpages < otta THEN 0 ELSE bs*(sml.relpages-otta)::BIGINT END AS wasted_bytes
                 , iname AS index_name
                 , ROUND((CASE WHEN iotta=0 OR ipages=0 THEN 0.0 ELSE ipages::float/iotta END)::numeric(6,3),1) AS bloat_ratio
                 , CASE WHEN ipages < iotta THEN 0 ELSE bs*(ipages-iotta) END AS wasted_index_bytes
              FROM (SELECT schemaname
                         , tablename
                         , cc.reltuples
                         , cc.relpages
                         , bs
                         , CEIL((cc.reltuples*((datahdr+ma-
                                              (CASE WHEN datahdr%ma=0 THEN ma ELSE datahdr%ma END))+nullhdr2+4))/(bs-20::float)) AS otta
                         , COALESCE(c2.relname,'noname') AS iname
                         , COALESCE(c2.reltuples,0) AS ituples
                         , COALESCE(c2.relpages,0) AS ipages
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
                                         , hdr+(SELECT 1+count(*)/8
                                                  FROM pg_stats s2
                                                 WHERE null_frac<>0 AND s2.schemaname = s.schemaname AND s2.tablename = s.tablename) AS nullhdr
                                      FROM pg_stats s
                                         , (SELECT (SELECT current_setting('block_size')::numeric) AS bs
                                                 , CASE WHEN substring(v,12,3) IN ('8.0','8.1','8.2') THEN 27 ELSE 23 END AS hdr
                                                 , CASE WHEN v ~ 'mingw32' THEN 8 ELSE 4 END AS ma
                                              FROM (SELECT version() AS v) AS foo) AS constants
                                    GROUP BY 1,2,3,4,5) AS foo) AS rs
                      JOIN pg_class cc ON cc.relname = rs.tablename
                      JOIN pg_namespace nn ON cc.relnamespace = nn.oid AND nn.nspname = rs.schemaname AND nn.nspname <> 'information_schema'
                      LEFT JOIN pg_index i ON indrelid = cc.oid
                      LEFT JOIN pg_class c2 ON c2.oid = i.indexrelid
                     WHERE NOT i.indisprimary) AS sml
             WHERE schemaname NOT IN ('pg_catalog'))
            SELECT ix.*
                 , pg_get_indexdef((ix.schema_name || '.' || index_name)::regclass) as ddl
                 , psai.idx_scan
                 , last_idx_scan
                 , idx_tup_fetch
                 , idx_tup_read
                 , pg_relation_size((ix.schema_name || '.' || index_name)::regclass) as idx_size
                 , pg_relation_size((ix.schema_name || '.' || table_name)::regclass) as tbl_size
              FROM ix
              JOIN pg_stat_all_indexes psai ON psai.schemaname = ix.schema_name
                                           AND psai.relname = ix.table_name
                                           AND psai.indexrelname = ix.index_name
             ORDER BY wasted_index_bytes DESC
            """;
}

