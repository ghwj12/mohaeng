package org.poolpool.mohaeng;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MySQL DB 연결 확인용 JUnit 테스트
 * - application.properties 의 spring.datasource.* 설정을 그대로 사용하여 접속을 시도함
 */
@SpringBootTest //  스프링부트 전체 컨텍스트를 띄워서 DataSource를 구성함
class DbConnectionTest {

    @Autowired
    private DataSource dataSource; // 스프링이 구성한 커넥션 풀(DataSource)을 주입받음

    @Test
    void mysql_connection_ok() throws Exception {

        // 1) DataSource가 정상 주입되었는지 먼저 확인
        // - 주입이 안 되면 스프링 컨텍스트 로딩/의존성/설정에 문제가 있다는 뜻임
        assertNotNull(dataSource, "DataSource가 null 입니다. DB 설정 또는 의존성을 확인하세요.");

        // 2) 실제 DB 커넥션을 열기함
        // - try-with-resources를 사용하면 테스트 종료 시 자동으로 커넥션을 닫아줌(누수 방지).
        try (Connection conn = dataSource.getConnection()) {

            // 3) 커넥션 객체가 null이 아닌지 확인
            assertNotNull(conn, "Connection이 null 입니다. 접속 정보를 확인하세요.");

            // 4) 커넥션이 유효한지 확인
            // - isValid(2)는 2초 안에 서버 응답이 오면 true를 반환함
            assertTrue(conn.isValid(2), "DB 커넥션이 유효하지 않습니다. (서버 down / 인증 실패 등)");

            // 5) 연결된 DB의 메타정보를 출력해 확인함
            DatabaseMetaData meta = conn.getMetaData();

            System.out.println("DB 연결 성공!");
            System.out.println(" - DB Product   : " + meta.getDatabaseProductName());
            System.out.println(" - DB Version   : " + meta.getDatabaseProductVersion());
            System.out.println(" - Driver Name  : " + meta.getDriverName());
            System.out.println(" - URL          : " + meta.getURL());
            System.out.println(" - User         : " + meta.getUserName());

            // 6) 테스트의 최종 성공 조건(커넥션이 열려있어야 함)
            assertFalse(conn.isClosed(), "커넥션이 이미 닫혀 있습니다.");
        }
    }
}
