package no.kristiania.Objects;

public class Questionnaire {
    private int questionnaireId;
    private String questionnaireTitle;
    private String questionnaireText;


    public Questionnaire() {

    }

    public int getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(int questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public String getQuestionnaireTitle() {
        return questionnaireTitle;
    }

    public void setQuestionnaireTitle(String questionnaireTitle) {
        this.questionnaireTitle = questionnaireTitle;
    }

    public String getQuestionnaireText() {
        return questionnaireText;
    }

    public void setQuestionnaireText(String questionnaireText) {
        this.questionnaireText = questionnaireText;
    }
}
