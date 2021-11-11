package no.kristiania.Controller;

import no.kristiania.Dao.QuestionDao;
import no.kristiania.Http.HttpReader;
import no.kristiania.Objects.Question;

import java.sql.SQLException;

public class ListQuestionController implements HttpController {
    private final QuestionDao questionDao;

    public ListQuestionController(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    @Override
    public HttpReader handle(HttpReader request) throws SQLException {
        String responseText = "";

        for (Question question : questionDao.listAll()) {
            responseText += "<option value=\""+ question.getQuestionId() +"\">"+ question.getQuestionTitle() +"</option>";
        }

        return new HttpReader("HTTP/1.1 200 OK", responseText);
    }
}
