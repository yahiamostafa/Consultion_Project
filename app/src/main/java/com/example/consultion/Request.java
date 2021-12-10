package com.example.consultion;

public class Request {
    String question , answer , uid,id;
    boolean isAnswered;
    public Request(String question ,String answer, String uid , String id ,boolean isAnswered ){
        this.question = question;
        this.id = id;
        this.isAnswered = isAnswered;
        this.answer = answer;
        this.uid = uid;
        this.isAnswered = isAnswered;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isAnswered() {
        return isAnswered;
    }

    public void setAnswered(boolean answered) {
        isAnswered = answered;
    }
}

