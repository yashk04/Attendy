package com.google.firebase.samples.apps.mlkit.models;

public class StudentAttendanceModel {

    private String subject;
    private String percent;
    private String type;
    private int subjectId;
    private int totalCount;
    private int attendedCount;

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getAttendedCount() {
        return attendedCount;
    }

    public void setAttendedCount(int attendedCount) {
        this.attendedCount = attendedCount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }
}
