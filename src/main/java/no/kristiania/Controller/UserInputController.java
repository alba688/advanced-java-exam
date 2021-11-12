package no.kristiania.Controller;

import no.kristiania.Dao.PersonDao;
import no.kristiania.Http.HttpReader;
import no.kristiania.Objects.Person;
import org.logevents.util.JsonUtil;

import java.sql.SQLException;

public class UserInputController implements HttpController{
    private PersonDao personDao;

    public UserInputController(PersonDao personDao) {
        this.personDao = personDao;
    }


    @Override
    public HttpReader handle(HttpReader request) throws SQLException {
        String responseTxt = "";
        if (request.getResponseHeader("Cookie") != null) {
            int personID = Integer.parseInt(request.parseRequestParameters(request.getResponseHeader("Cookie")).get("user"));
            for (Person person : personDao.listAll()) {
                if (person.getPersonId() == personID) {
                    responseTxt = "<h2>Velkommen igjen," + personDao.retrieve(personID).getFirstName() + "</h2>";
                }
            }
        }
        else {
            responseTxt = "<form method=\"POST\" action=\"/api/savePerson\">" +
                    " <label>First Name: <input type=\"text\" name=\"firstName\"/></label><br>\n" +
                    "<label>Last Name: <input type=\"text\" name=\"lastName\" /></label><br>\n" +
                    "<label>Email <input type=\"text\" name=\"email\" /></label><br>\n" +
                    "<Button>Send</button>" +
                    "</form>";
        }
        return new HttpReader("HTTP/1.1 200 OK", responseTxt);
    }
}
