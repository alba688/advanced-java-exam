package no.kristiania.Objects;

public class Answer {
    private int questionId;
    private int answerValue;
    private int answerId;
    private int personId;


    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getAnswerValue() {
        return answerValue;
    }

    public void setAnswerValue(int answerValue) {
        this.answerValue = answerValue;
    }

    public void setPersonId(int personId) {

        this.personId = personId;
    }

    public int getPersonId() {
        return personId;
    }
}
