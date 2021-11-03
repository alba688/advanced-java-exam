package no.kristiania.Eksamen;

public class Question {
    private String questionText;
    private String questionTitle;

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
}
