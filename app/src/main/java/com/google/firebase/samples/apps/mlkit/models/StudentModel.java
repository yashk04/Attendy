package com.google.firebase.samples.apps.mlkit.models;

import java.util.ArrayList;

public class StudentModel {
    String id;
    String name;
    String div;
    String phoneNumber;
    ArrayList<SubjectInStudentModel> subjects=new ArrayList<>();

    public StudentModel()
    {

    }

    public StudentModel(String id, String name, String div, String phoneNumber, ArrayList<SubjectInStudentModel> subjects) {
        this.id = id;
        this.name = name;
        this.div = div;
        this.phoneNumber = phoneNumber;
        this.subjects = subjects;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDiv() {
        return div;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public ArrayList<SubjectInStudentModel> getSubjects() {
        return subjects;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDiv(String div) {
        this.div = div;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setSubjects(ArrayList<SubjectInStudentModel> subjects) {
        this.subjects = subjects;
    }
}

