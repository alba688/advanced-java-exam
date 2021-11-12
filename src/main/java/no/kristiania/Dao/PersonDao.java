package no.kristiania.Dao;

import no.kristiania.Objects.Person;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class PersonDao extends AbstractDao<Person> {

    public PersonDao(DataSource dataSource) {
        super(dataSource);
    }

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
    }

    public Person retrieve(int id) throws SQLException {
            return super.retrieve("select * from person where person_id = (?)", id);
    }

    public List<Person> listAll() throws SQLException {
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
