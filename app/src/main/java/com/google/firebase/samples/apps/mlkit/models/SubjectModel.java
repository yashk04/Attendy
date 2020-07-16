package com.google.firebase.samples.apps.mlkit.models;

import java.util.ArrayList;

public class SubjectModel {
    int id;
    String name;
    String div;
    int teacherId;
    ArrayList<String> dates=new ArrayList<>();
    String Type;
    String batch;

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public SubjectModel()
    {

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDiv() {
        return div;
    }


    public int getTeacherId() {
        return teacherId;
    }

    public SubjectModel(String name, int id, String div, ArrayList<String> dates, int teacherId,String type)
    {
        this.name=name;
        this.id=id;
        this.div=div;
        this.dates=dates;
        this.teacherId=teacherId;
        this.Type = type;

    }
    public SubjectModel(int id)
    {
        this.id=id;

    }
    public void setDates(ArrayList<String> dates) {
        this.dates = dates;
    }

    public ArrayList<String> getDates() {
        return dates;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDiv(String div) {
        this.div = div;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        this.Type = type;
    }
}
