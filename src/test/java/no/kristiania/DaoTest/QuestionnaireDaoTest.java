package no.kristiania.DaoTest;

import no.kristiania.Dao.CategoryDao;
import no.kristiania.Dao.QuestionnaireDao;
import no.kristiania.Objects.Category;
import no.kristiania.Objects.Questionnaire;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;


public class QuestionnaireDaoTest {
    private QuestionnaireDao dao = new QuestionnaireDao(TestData.testDataSource());



    @Test
    void shouldSaveAndRetrieveQuestionnaireFromDatabase() throws SQLException {
        Questionnaire questionnaire = exampleQuestionnaire();
        dao.save(questionnaire);

        assertThat(dao.retrieve(questionnaire.getQuestionnaireId()))
                .usingRecursiveComparison()
                .isEqualTo(questionnaire);
    }

    @Test
    void shouldListAllQuestions() throws SQLException {
        Questionnaire questionnaire = exampleQuestionnaire();
        dao.save(questionnaire);
        Questionnaire anotherQuestionnaire = exampleQuestionnaire();
        dao.save(anotherQuestionnaire);

        assertThat(dao.listAll())
                .extracting(Questionnaire::getQuestionnaireId)
                .contains(questionnaire.getQuestionnaireId(), anotherQuestionnaire.getQuestionnaireId());
    }


    private Questionnaire exampleQuestionnaire() {
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setQuestionnaireTitle(TestData.pickOne("School Politics", "Food", "Favorite Class"));
        questionnaire.setQuestionnaireText(TestData.pickOne("Either or", "Share Your Opinion", "Some Test Text"));
        return questionnaire;
    }

}
