package no.kristiania.Controller;

import no.kristiania.Dao.QuestionnaireDao;
import no.kristiania.Http.HttpReader;
import no.kristiania.Objects.Questionnaire;

import java.sql.SQLException;
import java.util.Map;

public class NewQuestionnaireController implements HttpController {
    private final QuestionnaireDao questionnaireDao;

    public NewQuestionnaireController (QuestionnaireDao questionnaireDao) {
        this.questionnaireDao = questionnaireDao;
    }

    @Override
    public HttpReader handle(HttpReader request) throws SQLException {
        Map<String, String> queryMap = HttpReader.parseRequestParameters(request.messageBody);
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setQuestionnaireTitle(queryMap.get("title"));
        questionnaire.setQuestionnaireText(queryMap.get("text"));
        questionnaireDao.save(questionnaire);
        return new HttpReader("HTTP/1.1 301 Questionnaire made\r\nLocation: /index.html", "Questionnaire created");
    }
}
