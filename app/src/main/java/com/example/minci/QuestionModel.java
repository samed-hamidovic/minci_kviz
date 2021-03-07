package com.example.minci;

public class QuestionModel {

    private String question;
    private int setNo;
    private int idQ;
    public QuestionModel(){

    }

    public QuestionModel(String question, int setNo, int idQ) {
        this.question = question;
        this.setNo = setNo;
        this.idQ = idQ;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getSetNo() {
        return setNo;
    }

    public void setSetNo(int setNo) {
        this.setNo = setNo;
    }

    public int getIdQ() {
        return idQ;
    }

    public void setIdQ(int idQ) {
        this.idQ = idQ;
    }
}
