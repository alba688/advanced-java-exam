package no.kristiania.Dao;

import no.kristiania.Objects.Person;

import javax.sql.DataSource;
import java.sql.*;

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




    @Override
    protected Person mapFromResultSet(ResultSet rs) throws SQLException {
        return null;
    }
}
