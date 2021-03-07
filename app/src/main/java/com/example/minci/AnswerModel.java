package com.example.minci;

public class AnswerModel {

    private String answer;
    private int correct;
    private int idQ;

    public  AnswerModel(){};

    public AnswerModel(String answer, int correct, int idQ) {
        this.answer = answer;
        this.correct = correct;
        this.idQ = idQ;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getCorrect() {
        return correct;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }

    public int getIdQ() {
        return idQ;
    }

    public void setIdQ(int idQ) {
        this.idQ = idQ;
    }
}
