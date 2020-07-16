package com.google.firebase.samples.apps.mlkit.models;

import java.util.ArrayList;

public class TeacherModel {
    String name;
    int id;
    String phoneNumber;
    ArrayList<Integer> subjectId=new ArrayList<>();
    public TeacherModel(){}
    public TeacherModel(int id, String name, String phoneNumber)
    {
        this.id=id;
        this.name=name;
        this.phoneNumber=phoneNumber;

    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public ArrayList<Integer> getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(ArrayList<Integer> subjectId) {
        this.subjectId = subjectId;
    }
}
