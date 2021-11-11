package no.kristiania.Controller;

import no.kristiania.Dao.QuestionDao;
import no.kristiania.Dao.QuestionnaireDao;
import no.kristiania.Http.HttpReader;
import no.kristiania.Objects.Question;
import no.kristiania.Objects.Questionnaire;

import java.sql.SQLException;
import java.util.Map;

public class ShowQuestionnaireQuestionsController implements HttpController {
    private final QuestionnaireDao questionnaireDao;
    private final QuestionDao questionDao;


    public ShowQuestionnaireQuestionsController(QuestionnaireDao questionnaireDao, QuestionDao questionDao) {
        this.questionnaireDao = questionnaireDao;
        this.questionDao = questionDao;
    }

    @Override
    public HttpReader handle(HttpReader request) throws SQLException {
        String responseTxt = " ";
        Map<String, String> queryMap = HttpReader.parseRequestParameters(request.messageBody);

        Questionnaire questionnaire = questionnaireDao.retrieve(Integer.parseInt(queryMap.get("questionnaires")));

        responseTxt = "<h1>" + questionnaire.getQuestionnaireTitle() + "</h1>";
        int j = 0;
        for (Question question : questionDao.listAllWithParameter(questionnaire.getQuestionnaire_id())) {
            responseTxt += "<p>" + question.getQuestionTitle() +
                    "</p>" +
                    "<form method=\"POST\" action=\"/api/answerQuestionnaire\"><label>" + question.getLowLabel() + "</label>";


            for (int i = 1; i < question.getNumberOfValues(); i++) {
                responseTxt += "<input value=\"" + question.getQuestionId() + "v" + i +"\"" + "type=\"radio\" name=\"question"+j+"\"></input>";
            }
            j++;
            responseTxt += "<label>" + question.getHighLabel() + "</label><br>";

        }

        responseTxt += "<button value=\"Send\">Send</button></form>";

        return new HttpReader("HTTP/1.1 200 OK", responseTxt);

    }
}
