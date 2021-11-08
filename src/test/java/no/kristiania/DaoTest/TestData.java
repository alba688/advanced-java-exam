package no.kristiania.DaoTest;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;

import javax.sql.DataSource;
import java.util.Random;

public class TestData {
    public static DataSource testDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:Questionnairedb;DB_CLOSE_DELAY=-1");

        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;
    }

    public static String pickOne(String... alternatives) {
        return alternatives[new Random().nextInt(alternatives.length)];
    }

}
