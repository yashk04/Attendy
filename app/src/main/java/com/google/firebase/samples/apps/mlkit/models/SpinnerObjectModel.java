package com.google.firebase.samples.apps.mlkit.models;

public class SpinnerObjectModel{
    private String nameOfSubject;
    private int subjectId;
    public SpinnerObjectModel()
    {

    }

    public SpinnerObjectModel(String nameOfSubject, int subjectId) {
        this.nameOfSubject = nameOfSubject;
        this.subjectId = subjectId;
    }

    public String getNameOfSubject() {
        return nameOfSubject;
    }

    public void setNameOfSubject(String nameOfSubject) {
        this.nameOfSubject = nameOfSubject;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }
}
