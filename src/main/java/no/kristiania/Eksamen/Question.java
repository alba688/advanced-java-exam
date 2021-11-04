package no.kristiania.Eksamen;

public class Question {
    private String questionText;
    private String questionTitle;
    private String lowLabel;
    private String highLabel;


/*    public Question(String question) {
//
//    }
//
//    public Question() {
//      }
*/

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
}
