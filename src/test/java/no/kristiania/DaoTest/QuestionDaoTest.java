package no.kristiania.DaoTest;

import no.kristiania.Dao.QuestionDao;
import no.kristiania.Dao.CategoryDao;
import no.kristiania.Objects.Category;
import no.kristiania.Objects.Question;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionDaoTest {
    private QuestionDao dao = new QuestionDao(TestData.testDataSource());
    private CategoryDao categoryDao = new CategoryDao(TestData.testDataSource());

    @Test
    void shouldSaveAndRetrieveQuestionFromDatabase() throws SQLException {
        Category category = new Category();
        category.setCategory_id(1);
        category.setCategoryTitle("Title");
        category.setCategoryText("Text");
        categoryDao.save(category);

        Question question = exampleQuestion();

        dao.save(question);

        assertThat(dao.retrieve(question.getQuestionId()))
                .usingRecursiveComparison()
                .isEqualTo(question);
    }

    @Test
    void shouldListAllQuestions() throws SQLException {
        Category category = new Category();
        category.setCategory_id(1);
        category.setCategoryTitle("Title");
        category.setCategoryText("Text");
        categoryDao.save(category);

        Question question = exampleQuestion();
        dao.save(question);
        Question anotherQuestion = exampleQuestion();
        dao.save(anotherQuestion);

        assertThat(dao.listAll())
        .extracting(Question::getQuestionId)
                .contains(question.getQuestionId(), anotherQuestion.getQuestionId());
    }

    @Test
    void shouldEditASpecificQuestion() throws SQLException {
        Question question = exampleQuestion();
        dao.save(question);

        Question fixedQuestion = new Question();
        fixedQuestion.setQuestionId(question.getQuestionId());
        fixedQuestion.setQuestionTitle("This question is fixed");
        fixedQuestion.setLowLabel("Low test");
        fixedQuestion.setHighLabel("High test");
        fixedQuestion.setNumberOfValues(2);
        fixedQuestion.setQuestionnaireId(1);
        dao.edit(fixedQuestion);

        assertThat(dao.retrieve(question.getQuestionId()))
                .usingRecursiveComparison().isEqualTo(fixedQuestion);
    }

    @Test
    void shouldDeleteASpecificQuestion() throws SQLException {
        Category category = new Category();
        category.setCategory_id(1);
        category.setCategoryTitle("Title");
        category.setCategoryText("Text");
        categoryDao.save(category);

        Question questionToDelete = exampleQuestion();
        dao.save(questionToDelete);

        Question questionToSave = exampleQuestion();
        dao.save(questionToSave);

        dao.delete(questionToDelete.getQuestionId());

        assertThat(dao.listAll())
                .extracting(Question::getQuestionId)
                .contains(questionToSave.getQuestionId())
                .doesNotContain(questionToDelete.getQuestionId());

    }

    private Question exampleQuestion() {
        Question question = new Question();
        question.setQuestionTitle(TestData.pickOne("Coffee or tea?", "Apple or Banana?", "Pizza or Hamburger?", "Black or White?"));
        question.setLowLabel(TestData.pickOne("No", "None", "Negative"));
        question.setHighLabel(TestData.pickOne("Yes", "Good", "Amazing"));
        question.setNumberOfValues(new Random().nextInt(10));
        question.setQuestionnaireId(1);
        return question;
    }

}
