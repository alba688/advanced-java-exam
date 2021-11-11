package no.kristiania.DaoTest;

import no.kristiania.Dao.QuestionDao;
import no.kristiania.Dao.CategoryDao;
import no.kristiania.Dao.QuestionnaireDao;
import no.kristiania.Objects.Category;
import no.kristiania.Objects.Question;

import no.kristiania.Objects.Questionnaire;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionDaoTest {
    private QuestionDao dao = new QuestionDao(TestData.testDataSource());
    private CategoryDao categoryDao = new CategoryDao(TestData.testDataSource());
    private QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());

    Questionnaire questionnaire = new Questionnaire();

    @Test
    void shouldSaveAndRetrieveQuestionFromDatabase() throws SQLException {
        questionnaire.setQuestionnaireTitle("Title");
        questionnaireDao.save(questionnaire);

        Category category = new Category();
        category.setCategoryId(1);
        category.setCategoryTitle("Title");
        category.setCategoryText("Text");
        category.setQuestionnaireId(1);
        categoryDao.save(category);

        Question question = TestData.exampleQuestion();

        dao.save(question);

        assertThat(dao.retrieve(question.getQuestionId()))
                .usingRecursiveComparison()
                .isEqualTo(question);
    }

    @Test
    void shouldListAllQuestions() throws SQLException {
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setQuestionnaireTitle("title");
        questionnaireDao.save(questionnaire);

        Category category = TestData.exampleCategory();
        categoryDao.save(category);

        Question question = TestData.exampleQuestion();
        dao.save(question);
        Question anotherQuestion = TestData.exampleQuestion();
        dao.save(anotherQuestion);

        assertThat(dao.listAll())
        .extracting(Question::getQuestionId)
                .contains(question.getQuestionId(), anotherQuestion.getQuestionId());
    }

    @Test
    void shouldEditASpecificQuestion() throws SQLException {
        Category category = TestData.exampleCategory();


        Question question = TestData.exampleQuestion();
        dao.save(question);

        Question fixedQuestion = new Question();
        fixedQuestion.setQuestionId(question.getQuestionId());
        fixedQuestion.setQuestionTitle("This question is fixed");
        fixedQuestion.setLowLabel("Low test");
        fixedQuestion.setHighLabel("High test");
        fixedQuestion.setNumberOfValues(2);
        fixedQuestion.setCategoryId(1);
        dao.edit(fixedQuestion);

        assertThat(dao.retrieve(question.getQuestionId()))
                .usingRecursiveComparison().isEqualTo(fixedQuestion);
    }

    @Test
    void shouldDeleteASpecificQuestion() throws SQLException {

        questionnaire.setQuestionnaireTitle("title");
        questionnaireDao.save(questionnaire);

        Category category = TestData.exampleCategory();
        categoryDao.save(category);

        Question questionToDelete = TestData.exampleQuestion();
        dao.save(questionToDelete);

        Question questionToSave = TestData.exampleQuestion();
        dao.save(questionToSave);

        dao.delete(questionToDelete.getQuestionId());

        assertThat(dao.listAll())
                .extracting(Question::getQuestionId)
                .contains(questionToSave.getQuestionId())
                .doesNotContain(questionToDelete.getQuestionId());

    }



}
