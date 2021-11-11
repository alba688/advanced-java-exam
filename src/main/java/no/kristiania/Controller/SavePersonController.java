package no.kristiania.Controller;

import no.kristiania.Dao.PersonDao;
import no.kristiania.Http.HttpReader;
import no.kristiania.Objects.Person;

import java.sql.SQLException;
import java.util.Map;


public class SavePersonController implements HttpController{
        private final PersonDao personDao;

        public SavePersonController(PersonDao personDao) {
            this.personDao = personDao;
        }

    @Override
    public HttpReader handle(HttpReader request) throws SQLException {
        Map<String, String> queryMap = HttpReader.parseRequestParameters(request.messageBody);
        Person person = new Person();
        person.setFirstName(queryMap.get("firstName"));
        person.setLastName(queryMap.get("lastName"));
        person.setEmail(queryMap.get("email"));
        personDao.save(person);
            return new HttpReader("HTTP/1.1 200 OK", "Person created");
    }
}
