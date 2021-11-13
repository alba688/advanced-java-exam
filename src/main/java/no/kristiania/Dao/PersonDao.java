package no.kristiania.Dao;

import no.kristiania.Objects.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class PersonDao extends AbstractDao<Person> {

    public PersonDao(DataSource dataSource) {
        super(dataSource);
    }

    private static final Logger logger = LoggerFactory.getLogger(PersonDao.class);

    public void save(Person person) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into person (first_name, last_name, email) values (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {

                statement.setString(1, person.getFirstName());
                statement.setString(2, person.getLastName());
                statement.setString(3, person.getEmail());

                statement.executeUpdate();

                try (ResultSet rs = statement.getGeneratedKeys()) {
                    rs.next();
                    person.setPersonId((rs.getInt("person_id")));
                }
            }
        }
        logger.info("Person "+ person.getFirstName() +" saved");
    }

    public Person retrieve(int id) throws SQLException {
        logger.info("Person "+ id +" retrieved");
            return super.retrieve("select * from person where person_id = (?)", id);
    }

    public List<Person> listAll() throws SQLException {
        logger.info("All persons listed");
        return super.listAll("select * from person");
    }
    @Override
    protected Person mapFromResultSet(ResultSet rs) throws SQLException {
        Person person = new Person();
        person.setPersonId(rs.getInt("person_id"));
        person.setFirstName(rs.getString("first_name"));
        person.setLastName(rs.getString("last_name"));
        person.setEmail(rs.getString("email"));
        return person;
    }
}
