package no.kristiania.Controller;

import no.kristiania.Dao.QuestionnaireDao;
import no.kristiania.Http.HttpReader;
import no.kristiania.Objects.Questionnaire;

import java.sql.SQLException;

public class ListQuestionnairesController implements HttpController {
    private final QuestionnaireDao questionnaireDao;

    public ListQuestionnairesController (QuestionnaireDao questionnaireDao) {
        this.questionnaireDao = questionnaireDao;
    }

    @Override
    public HttpReader handle(HttpReader request) throws SQLException {
        String responseText = "";
        for (Questionnaire questionnaire : questionnaireDao.listAll()) {
            responseText += "<option value=\""+ questionnaire.getQuestionnaire_id() +"\">"+ questionnaire.getQuestionnaireTitle() +"</option>";
        }
        return new HttpReader("HTTP/1.1 200 OK", responseText);

    }
}
