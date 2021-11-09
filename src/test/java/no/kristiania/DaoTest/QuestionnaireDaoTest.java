package no.kristiania.DaoTest;

import no.kristiania.Dao.CategoryDao;
import no.kristiania.Objects.Category;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;


public class QuestionnaireDaoTest {
    private CategoryDao dao = new CategoryDao(TestData.testDataSource());



    @Test
    void shouldSaveAndRetrieveQuestionnaireFromDatabase() throws SQLException {

    }

    @Test
    void shouldListAllQuestions() throws SQLException {



    }


    private Questionnaire exampleQuestionnaire() {
        Questionnaire questionnaire = new Category();
        questionnaire.setCategoryTitle(TestData.pickOne("Favorite Food", "Favorite Drink", "Favorite Class"));
        questionnaire.setCategoryText(TestData.pickOne("Questionnaire Description", "Share Your Opinion", "Some Test Text"));
        return questionnaire;
    }

}
