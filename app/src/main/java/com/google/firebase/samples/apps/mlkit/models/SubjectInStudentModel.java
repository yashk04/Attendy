package com.google.firebase.samples.apps.mlkit.models;

import java.util.ArrayList;

public class SubjectInStudentModel {
    int id;
    ArrayList<String> dates=new ArrayList<String>();
    public SubjectInStudentModel()
    {

    }

    public int getId() {
        return id;
    }
    public SubjectInStudentModel(int id, ArrayList<String> dates)
    {
        this.id=id;
        this.dates=dates;


    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<String> getDates() {
        return dates;
    }

    public void setDates(ArrayList<String> dates) {
        this.dates = dates;
    }
}
