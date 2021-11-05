package no.kristiania.Objects;

public class Questionnaire {
    private int questionnaire_id;
    private String questionnaireTitle;
    private String questionnaireText;

    public int getQuestionnaire_id() {
        return questionnaire_id;
    }

    public void setQuestionnaire_id(int questionnaire_id) {
        this.questionnaire_id = questionnaire_id;
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
