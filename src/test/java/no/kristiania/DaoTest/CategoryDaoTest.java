package no.kristiania.DaoTest;

import no.kristiania.Dao.CategoryDao;
import no.kristiania.Dao.QuestionnaireDao;
import no.kristiania.Objects.Category;
import no.kristiania.Objects.Questionnaire;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static no.kristiania.DaoTest.TestData.exampleCategory;
import static org.assertj.core.api.Assertions.assertThat;



public class CategoryDaoTest {
    private CategoryDao dao = new CategoryDao(TestData.testDataSource());
    private QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());



    @Test
    void shouldSaveAndRetrieveCategoryFromDatabase() throws SQLException {
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setQuestionnaireTitle("Title");
        questionnaireDao.save(questionnaire);

        Category category = exampleCategory();
        dao.save(category);

        assertThat(dao.retrieve(category.getCategoryId()))
                .usingRecursiveComparison()
                .isEqualTo(category);
    }

    @Test
    void shouldListAllQuestions() throws SQLException {
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setQuestionnaireTitle("Title");
        questionnaireDao.save(questionnaire);

        Category category = exampleCategory();
        dao.save(category);
        Category anotherCategory = exampleCategory();
        dao.save(anotherCategory);

        assertThat(dao.listAll())
                .extracting(Category::getCategoryId)
                .contains(category.getCategoryId(), anotherCategory.getCategoryId());


    }


}
