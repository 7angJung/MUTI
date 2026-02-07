package com.muti.infrastructure.database;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Supabase PostgreSQL 데이터베이스 연결 테스트
 *
 * 이 테스트는 prod 프로필로 실행되며, 실제 Supabase DB에 연결합니다.
 * 실행 전에 .env 파일에 올바른 DB 연결 정보가 설정되어 있어야 합니다.
 *
 * 실행 방법:
 * 1. .env.example을 복사하여 .env 파일 생성
 * 2. .env 파일에 실제 Supabase 비밀번호 입력
 * 3. 테스트 실행: ./gradlew test --tests SupabaseDatabaseConnectionTest
 */
@SpringBootTest
@ActiveProfiles("prod")
@DisplayName("Supabase 데이터베이스 연결 테스트")
class SupabaseDatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Supabase PostgreSQL 연결 성공")
    void supabase_Connection_Success() throws Exception {
        // when
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            // then
            assertThat(connection).isNotNull();
            assertThat(connection.isValid(5)).isTrue();
            assertThat(metaData.getDatabaseProductName()).containsIgnoringCase("PostgreSQL");

            System.out.println("✅ Database Connection Successful!");
            System.out.println("   - Database: " + metaData.getDatabaseProductName());
            System.out.println("   - Version: " + metaData.getDatabaseProductVersion());
            System.out.println("   - URL: " + metaData.getURL());
            System.out.println("   - Driver: " + metaData.getDriverName());
        }
    }

    @Test
    @DisplayName("Supabase 쿼리 실행 테스트")
    void supabase_Query_Execution() {
        // when
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);

        // then
        assertThat(result).isEqualTo(1);
        System.out.println("✅ Query Execution Successful!");
    }

    @Test
    @DisplayName("Supabase 스키마 확인")
    void supabase_Schema_Check() {
        // when
        String query = """
            SELECT table_name
            FROM information_schema.tables
            WHERE table_schema = 'public'
            AND table_type = 'BASE TABLE'
            ORDER BY table_name
            """;

        var tables = jdbcTemplate.queryForList(query, String.class);

        // then
        System.out.println("✅ Existing Tables in Supabase:");
        if (tables.isEmpty()) {
            System.out.println("   - No tables found (Fresh database - Flyway will create them)");
        } else {
            tables.forEach(table -> System.out.println("   - " + table));
        }
    }

    @Test
    @DisplayName("Flyway 마이그레이션 히스토리 확인")
    void supabase_Flyway_History() {
        // when
        String query = """
            SELECT EXISTS (
                SELECT FROM information_schema.tables
                WHERE table_schema = 'public'
                AND table_name = 'flyway_schema_history'
            )
            """;

        Boolean flywayTableExists = jdbcTemplate.queryForObject(query, Boolean.class);

        // then
        System.out.println("✅ Flyway Schema History Table: " +
            (Boolean.TRUE.equals(flywayTableExists) ? "EXISTS" : "NOT FOUND"));

        if (Boolean.TRUE.equals(flywayTableExists)) {
            String historyQuery = """
                SELECT version, description, installed_on, success
                FROM flyway_schema_history
                ORDER BY installed_rank
                """;

            jdbcTemplate.query(historyQuery, rs -> {
                System.out.println("   - V" + rs.getString("version") +
                    ": " + rs.getString("description") +
                    " (" + rs.getTimestamp("installed_on") +
                    ") - " + (rs.getBoolean("success") ? "✅" : "❌"));
            });
        }
    }
}