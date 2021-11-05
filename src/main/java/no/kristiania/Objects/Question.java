package no.kristiania.Objects;

public class Question {
    private int questionnaireId;
    private int questionId;
    private String questionTitle;
    private String questionText;
    private String lowLabel;
    private String highLabel;
    private int numberOfValues;



/*    public Question(String question) {
//
//    }
//
//    public Question() {
//      }
*/

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getLowLabel() {
        return lowLabel;
    }

    public void setLowLabel(String lowLabel) {
        this.lowLabel = lowLabel;
    }

    public String getHighLabel() {
        return highLabel;
    }

    public void setHighLabel(String highLabel) {
        this.highLabel = highLabel;
    }

    public int getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(int questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public int getNumberOfValues() {
        return numberOfValues;
    }

    public void setNumberOfValues(int numberOfValues) {
        this.numberOfValues = numberOfValues;
    }
}
