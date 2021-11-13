package no.kristiania.Controller;

import no.kristiania.Dao.AnswerDao;
import no.kristiania.Dao.QuestionnaireDao;
import no.kristiania.Http.HttpReader;
import no.kristiania.Objects.Answer;

import java.sql.SQLException;
import java.util.Map;

public class AnswerQuestionnaireController implements HttpController {
    private final QuestionnaireDao questionnaireDao;
    private final AnswerDao answerDao;

    public AnswerQuestionnaireController(QuestionnaireDao questionnaireDao, AnswerDao answerDao) {
        this.questionnaireDao = questionnaireDao;
        this.answerDao = answerDao;
    }

    @Override
    public HttpReader handle(HttpReader request) throws SQLException {
        Map<String, String> queryMap = HttpReader.parseRequestParameters(request.messageBody);
        Answer answer = new Answer();

        for (int i = 0; i < queryMap.size(); i++) {
            if(queryMap.get("question"+i) != null) {
                String buffer = queryMap.get("question" + i);
                int valuePos = buffer.indexOf('v');
                int questionId = Integer.parseInt(buffer.substring(0, valuePos));
                int answerValue = Integer.parseInt(buffer.substring(valuePos+1));

                answer.setQuestionId(questionId);
                answer.setAnswerValue(answerValue);
                int personID = Integer.parseInt(request.parseRequestParameters(request.getResponseHeader("Cookie")).get("user"));
                answer.setPersonId(personID);
                answerDao.save(answer);
            }
        }

        return new HttpReader("HTTP/1.1 301 Questionnaire Answered", "Thank You", "Location: ../showQuestionnaire.html");

    }
}
