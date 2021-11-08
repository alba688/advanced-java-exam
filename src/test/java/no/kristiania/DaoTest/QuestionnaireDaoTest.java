package no.kristiania.DaoTest;

import no.kristiania.Dao.QuestionnaireDao;
import no.kristiania.Objects.Question;
import no.kristiania.Objects.Questionnaire;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;



public class QuestionnaireDaoTest {
    private QuestionnaireDao dao = new QuestionnaireDao(TestData.testDataSource());



    @Test
    void shouldSaveAndRetrieveQuestionnaireFromDatabase() throws SQLException {
        Questionnaire questionnaire = exampleQuestionnaire();
        dao.save(questionnaire);

        assertThat(dao.retrieve(questionnaire.getQuestionnaire_id()))
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
                .extracting(Questionnaire::getQuestionnaire_id)
                .contains(questionnaire.getQuestionnaire_id(), anotherQuestionnaire.getQuestionnaire_id());


    }


    private Questionnaire exampleQuestionnaire() {
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setQuestionnaireTitle(TestData.pickOne("Favorite Food", "Favorite Drink", "Favorite Class"));
        questionnaire.setQuestionnaireText(TestData.pickOne("Questionnaire Description", "Share Your Opinion", "Some Test Text"));
        return questionnaire;
    }

}
