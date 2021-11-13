package no.kristiania.DaoTest;

import no.kristiania.Dao.*;
import no.kristiania.Objects.*;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class AnswerDaoTest {


    @BeforeEach
    void setup() {
        Flyway.configure().dataSource(TestData.testDataSource()).load().clean();
    }

    @Test
    void shouldSaveAndRetrievePersonFromDatabase() throws SQLException {
        AnswerDao answerDao = new AnswerDao(TestData.testDataSource());
        PersonDao personDao = new PersonDao(TestData.testDataSource());
        QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
        CategoryDao categoryDao = new CategoryDao(TestData.testDataSource());
        QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());

        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setQuestionnaireTitle("Title");
        questionnaireDao.save(questionnaire);

        Category category = TestData.exampleCategory();
        categoryDao.save(category);

        Question question = TestData.exampleQuestion();
        questionDao.save(question);

        Person person = new Person();
        person.setFirstName("Test");
        person.setLastName("Person");
        person.setEmail("test.person@mail.com");
        personDao.save(person);

        Answer answer = new Answer();
        answer.setAnswerValue(5);
        answer.setQuestionId(1);
        answer.setPersonId(1);
        answerDao.save(answer);

        assertThat(answerDao.retrieve(answer.getAnswerId()))
                .usingRecursiveComparison()
                .isEqualTo(answer);
    }

    @Test
    void shouldListAllAnswers() throws SQLException {
        AnswerDao answerDao = new AnswerDao(TestData.testDataSource());
        PersonDao personDao = new PersonDao(TestData.testDataSource());
        QuestionDao questionDao = new QuestionDao(TestData.testDataSource());
        CategoryDao categoryDao = new CategoryDao(TestData.testDataSource());
        QuestionnaireDao questionnaireDao = new QuestionnaireDao(TestData.testDataSource());

        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setQuestionnaireTitle("Title");
        questionnaireDao.save(questionnaire);

        Category category = TestData.exampleCategory();
        categoryDao.save(category);

        Question question = TestData.exampleQuestion();
        questionDao.save(question);

        Person person = new Person();
        person.setFirstName("Test");
        person.setLastName("Person");
        person.setEmail("test.person@mail.com");
        personDao.save(person);

        Person secondPerson = new Person();
        secondPerson.setFirstName("Test");
        secondPerson.setLastName("Person");
        secondPerson.setEmail("test.person@mail.com");
        personDao.save(secondPerson);

        Answer firstAnswer = new Answer();
        firstAnswer.setAnswerValue(1);
        firstAnswer.setPersonId(1);
        firstAnswer.setQuestionId(1);
        answerDao.save(firstAnswer);

        Answer secondAnswer = new Answer();
        secondAnswer.setAnswerValue(1);
        secondAnswer.setPersonId(2);
        secondAnswer.setQuestionId(1);
        answerDao.save(secondAnswer);

        assertThat(answerDao.listAll())
                .extracting(Answer::getAnswerId)
                .contains(firstAnswer.getAnswerId(), secondAnswer.getAnswerId());
    }
}

