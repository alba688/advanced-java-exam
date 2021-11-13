package no.kristiania.DaoTest;

import no.kristiania.Objects.Category;
import no.kristiania.Objects.Question;
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

    public static Question exampleQuestion() {
        Question question = new Question();
        question.setQuestionTitle(pickOne("Coffee or tea?", "Apple or Banana?", "Pizza or Hamburger?", "Black or White?"));
        question.setLowLabel(pickOne("No", "None", "Negative"));
        question.setHighLabel(pickOne("Yes", "Good", "Amazing"));
        question.setNumberOfValues(new Random().nextInt(10));
        question.setCategoryId(1);
        return question;
    }
    public static Category exampleCategory() {
        Category category = new Category();
        category.setCategoryTitle(pickOne("Favorite Food", "Favorite Drink", "Favorite Class"));
        category.setCategoryText(pickOne("Category Description", "Share Your Opinion", "Some Test Text"));
        category.setQuestionnaireId(1);
        return category;
    }
}
