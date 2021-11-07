package no.kristiania.DaoTest;

import no.kristiania.Dao.QuestionDao;
import no.kristiania.Objects.Question;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionDaoTest {
    private QuestionDao dao = new QuestionDao(TestData.testDataSource());

    @Test
    void shouldSaveAndRetrieveQuestionFromDatabase() throws SQLException {
        Question question = exampleQuestion();

        dao.save(question);

        assertThat(dao.retrieve(question.getQuestionId()))
                .usingRecursiveComparison()
                .isEqualTo(question);
    }

    @Test
    void shouldListAllQuestions() throws SQLException {
        Question question = exampleQuestion();
        dao.save(question);
        Question anotherQuestion = exampleQuestion();
        dao.save(anotherQuestion);

        assertThat(dao.listAll())
        .extracting(Question::getQuestionId)
                .contains(question.getQuestionId(), anotherQuestion.getQuestionId());


    }

    private Question exampleQuestion() {
        Question question = new Question();
        question.setQuestionTitle(TestData.pickOne("Coffee or tea?", "Apple or Banana?", "Pizza or Hamburger?", "Black or White?"));
        question.setLowLabel(TestData.pickOne("No", "None", "Negative"));
        question.setHighLabel(TestData.pickOne("Yes", "Good", "Amazing"));
        question.setNumberOfValues(new Random().nextInt(10));
        return question;
    }

}
