package no.kristiania.Controller;

import no.kristiania.Dao.QuestionDao;
import no.kristiania.Http.HttpReader;
import no.kristiania.Objects.Question;

import java.sql.SQLException;
import java.util.Map;

public class DeleteQuestionController implements HttpController {
    private final QuestionDao questionDao;

    public DeleteQuestionController(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    @Override
    public HttpReader handle(HttpReader request) throws SQLException {
        Map<String, String> queryMap = HttpReader.parseRequestParameters(request.messageBody);
        Question question = questionDao.retrieve(Integer.parseInt(queryMap.get("questions")));

        questionDao.delete(question.getQuestionId());

        return new HttpReader("HTTP/1.1 200 OK", "Question deleted");
    }
}
