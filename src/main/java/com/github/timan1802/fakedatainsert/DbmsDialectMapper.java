package com.github.timan1802.fakedatainsert;

import com.intellij.database.Dbms;
import net.datafaker.transformations.sql.SqlDialect;

/**
 * intellij Dbms 클래스와 datafaker SqlDialect enum 객체 매핑.
 *
 * Dbms에는 있고, SqlDialect에는 없는 경우 모두 SqlDialect.ANSI
 */
public enum DbmsDialectMapper {

    SYNAPSE(Dbms.SYNAPSE, SqlDialect.ANSI),
    UNKNOWN(Dbms.UNKNOWN, SqlDialect.ANSI),
    ORACLE(Dbms.ORACLE, SqlDialect.ORACLE),
    MEMSQL(Dbms.MEMSQL, SqlDialect.ANSI),
    MARIA(Dbms.MARIA, SqlDialect.MARIADB),
    MYSQL_AURORA(Dbms.MYSQL_AURORA, SqlDialect.MYSQL),
    MYSQL(Dbms.MYSQL, SqlDialect.MYSQL),
    POSTGRES(Dbms.POSTGRES, SqlDialect.POSTGRES),
    REDSHIFT(Dbms.REDSHIFT, SqlDialect.REDSHIFT),
    GREENPLUM(Dbms.GREENPLUM, SqlDialect.ANSI),
    SYBASE(Dbms.SYBASE, SqlDialect.ANSI),
    AZURE(Dbms.AZURE, SqlDialect.ANSI),
    MSSQL_LOCALDB(Dbms.MSSQL_LOCALDB, SqlDialect.MSSQL),
    MSSQL(Dbms.MSSQL, SqlDialect.MSSQL),
    DB2_LUW(Dbms.DB2_LUW, SqlDialect.ANSI),
    DB2_IS(Dbms.DB2_IS, SqlDialect.ANSI),
    DB2_ZOS(Dbms.DB2_ZOS, SqlDialect.ANSI),
    DB2(Dbms.DB2, SqlDialect.ANSI),
    SQLITE(Dbms.SQLITE, SqlDialect.ANSI),
    HSQL(Dbms.HSQL, SqlDialect.ANSI),
    H2(Dbms.H2, SqlDialect.H2),
    DERBY(Dbms.DERBY, SqlDialect.ANSI),
    EXASOL(Dbms.EXASOL, SqlDialect.EXASOL),
    CLICKHOUSE(Dbms.CLICKHOUSE, SqlDialect.CLICKHOUSE),
    CASSANDRA(Dbms.CASSANDRA, SqlDialect.ANSI),
    VERTICA(Dbms.VERTICA, SqlDialect.VERTICA),
    HIVE(Dbms.HIVE, SqlDialect.ANSI),
    SPARK(Dbms.SPARK, SqlDialect.ANSI),
    SNOWFLAKE(Dbms.SNOWFLAKE, SqlDialect.SNOWFLAKE),
    MONGO(Dbms.MONGO, SqlDialect.ANSI),
    COCKROACH(Dbms.COCKROACH, SqlDialect.ANSI),
    BIGQUERY(Dbms.BIGQUERY, SqlDialect.ANSI),
    COUCHBASE_QUERY(Dbms.COUCHBASE_QUERY, SqlDialect.ANSI)
    ;

    private final Dbms dbms;
    private final SqlDialect sqlDialect;

    DbmsDialectMapper(Dbms dbms, SqlDialect sqlDialect) {
        this.dbms = dbms;
        this.sqlDialect = sqlDialect;
    }

    public static SqlDialect getSqlDialect(Dbms dbms) {
        for (DbmsDialectMapper mapper : values()) {
            if (mapper.dbms.equals(dbms)) {
                return mapper.sqlDialect;
            }
        }
        return SqlDialect.POSTGRES; // 기본값
    }

    public static SqlDialect getSqlDialectOrNull(Dbms dbms) {
        for (DbmsDialectMapper mapper : values()) {
            if (mapper.dbms.equals(dbms)) {
                return mapper.sqlDialect;
            }
        }
        return null;
    }

    public Dbms getDbms() {
        return dbms;
    }

    public SqlDialect getSqlDialect() {
        return sqlDialect;
    }
}