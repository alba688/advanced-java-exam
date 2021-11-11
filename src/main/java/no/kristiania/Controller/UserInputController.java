package no.kristiania.Controller;

import no.kristiania.Dao.PersonDao;
import no.kristiania.Http.HttpReader;

import java.sql.SQLException;

public class UserInputController implements HttpController{
    private PersonDao personDao;

    public UserInputController(PersonDao personDao) {
        this.personDao = personDao;
    }

    @Override
    public HttpReader handle(HttpReader request) throws SQLException {
        String responseTxt = "<form method=\"POST\" action=\"/api/savePerson\">" +
                " <label>First Name: <input type=\"text\" name=\"firstName\"/></label><br>\n" +
                "<label>Last Name: <input type=\"text\" name=\"lastName\" /></label><br>\n" +
                "<label>Email <input type=\"text\" name=\"email\" /></label><br>\n" +
                "<Button>Send</button>" +
                "</form>";

        return new HttpReader("HTTP/1.1 200 OK", responseTxt);
    }
}
