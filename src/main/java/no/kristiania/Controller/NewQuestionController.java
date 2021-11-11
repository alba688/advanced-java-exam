package no.kristiania.Controller;

import no.kristiania.Dao.QuestionDao;
import no.kristiania.Http.HttpReader;
import no.kristiania.Objects.Question;

import java.sql.SQLException;
import java.util.Map;

public class NewQuestionController implements HttpController {

    private final QuestionDao questionDao;

    public NewQuestionController(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    @Override
    public HttpReader handle(HttpReader request) throws SQLException {
        Map<String, String> queryMap = HttpReader.parseRequestParameters(request.messageBody);
        Question question = new Question();

        int categoryId = Integer.parseInt(queryMap.get("categories"));
        question.setCategoryId(categoryId);
        question.setQuestionTitle(queryMap.get("title"));
        question.setLowLabel(queryMap.get("low_label"));
        question.setHighLabel(queryMap.get("high_label"));
        int numberOfValues = Integer.parseInt(queryMap.get("values"));
        question.setNumberOfValues(numberOfValues);

        questionDao.save(question);

        return new HttpReader("HTTP/1.1 301 Person saved\r\nLocation: /index.html", "Question added");
    }
}
