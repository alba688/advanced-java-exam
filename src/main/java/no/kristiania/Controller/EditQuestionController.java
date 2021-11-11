package no.kristiania.Controller;

import no.kristiania.Dao.QuestionDao;
import no.kristiania.Http.HttpReader;
import no.kristiania.Objects.Question;

import java.sql.SQLException;
import java.util.Map;

public class EditQuestionController implements HttpController {
    private final QuestionDao questionDao;

    public EditQuestionController(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    @Override
    public HttpReader handle(HttpReader request) throws SQLException {
        Map<String, String> queryMap = HttpReader.parseRequestParameters(request.messageBody);

        // retrieves the questionId to be edited
        Question question = questionDao.retrieve(Integer.parseInt(queryMap.get("questions")));

        // updates data using setters
        question.setQuestionTitle(queryMap.get("title"));
        question.setLowLabel(queryMap.get("low_label"));
        question.setHighLabel(queryMap.get("high_label"));
        int numberOfValues = Integer.parseInt(queryMap.get("values"));
        question.setNumberOfValues(numberOfValues);

        // sends updated question to edit method to deploy sql statement
        questionDao.edit(question);

        return new HttpReader("HTTP/1.1 200 OK", "Edit complete.");
    }
}
