package no.kristiania.DaoTest;

import no.kristiania.Dao.*;
import no.kristiania.Objects.*;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class PersonDaoTest {
    private PersonDao personDao = new PersonDao(TestData.testDataSource());


    @Test
    void shouldSaveAndRetrievePersonFromDatabase() throws SQLException {
        Person person = new Person();
        person.setPersonId(1);
        person.setFirstName("Test");
        person.setLastName("Person");
        person.setEmail("test.person@mail.com");
        personDao.save(person);

        assertThat(personDao.retrieve(person.getPersonId()))
                .usingRecursiveComparison()
                .isEqualTo(person);
    }


    }

