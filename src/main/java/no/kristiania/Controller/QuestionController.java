package no.kristiania.Controller;

import no.kristiania.Dao.QuestionDao;
import no.kristiania.Http.HttpReader;
import no.kristiania.Objects.Question;

import java.sql.SQLException;
import java.util.Map;

public class QuestionController implements HttpController {

    private final QuestionDao questionDao;

    public QuestionController(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    @Override
    public HttpReader handle(HttpReader request) throws SQLException {
        String responseText = "";
        for (Question question : questionDao.listAll()) {
            responseText +=
                    "<p>" + question.getQuestionTitle() +
                            "</p>" +
                            "<form method=\"\" action=\"\"><label>" + question.getLowLabel() +"</label>";

            for (int i=0; i < question.getNumberOfValues(); i++){
                responseText += "<input value=\"" + i + "\"" + "type=\"radio\" name=\"question" + question.getQuestionId() + "_answer\"></input>";
            }

            responseText +="<label>" + question.getHighLabel() + "</label>" +
                    "</form>";
        }
        
        return new HttpReader("HTTP/1.1 200 OK", responseText);
    }

}
