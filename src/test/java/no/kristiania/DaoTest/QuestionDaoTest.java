package no.kristiania.DaoTest;

import no.kristiania.Dao.QuestionDao;
import no.kristiania.Objects.Question;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionDaoTest {
    private QuestionDao dao = new QuestionDao(testDataSource());


    private DataSource testDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:Questionnairedb;DB_CLOSE_DELAY=-1");

        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;
    }

    @Test
    void shouldSaveAndRetrieveProductFromDatabase() throws SQLException {
        Question question = exampleQuestion();

        dao.save(question);

        assertThat(dao.retrieve(question.getQuestionId()))
                .usingRecursiveComparison()
                .isEqualTo(question);


    }

    private Question exampleQuestion() {
        Question question = new Question();
        question.setQuestionTitle(pickOne("Coffee or tea?", "Apple or Banana?", "Pizza or Hamburger?", "Black or White?"));
        return question;
    }

    private String pickOne(String... alternatives) {
        return alternatives[new Random().nextInt(alternatives.length)];
    }

}
