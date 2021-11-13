package no.kristiania.Controller;

import no.kristiania.Dao.CategoryDao;
import no.kristiania.Dao.QuestionnaireDao;
import no.kristiania.Http.HttpReader;
import no.kristiania.Objects.Category;
import no.kristiania.Objects.Questionnaire;

import java.sql.SQLException;

public class ListCategoriesController implements HttpController {
    private final CategoryDao categoryDao;

    public ListCategoriesController(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Override
    public HttpReader handle(HttpReader request) throws SQLException {
        String responseText = "";
        for (Category category : categoryDao.listAll()) {
            responseText += "<option value=\""+ category.getCategoryId() +"\">"+ category.getCategoryTitle() +"</option>";
        }
        return new HttpReader("HTTP/1.1 200 OK", responseText);

    }
}
