package com.google.firebase.samples.apps.mlkit.adapters;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.samples.apps.mlkit.R;
import com.google.firebase.samples.apps.mlkit.models.StudentAttendanceModel;

import java.util.ArrayList;

public class StudentAttendanceAdapter extends RecyclerView.Adapter<StudentAttendanceAdapter.MyHolder> {

    Context c;
    ArrayList<StudentAttendanceModel> studentAttendanceModels;

    public StudentAttendanceAdapter(Context c, ArrayList<StudentAttendanceModel> studentAttendanceModels) {
        this.c = c;
        this.studentAttendanceModels = studentAttendanceModels;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.subject_row,viewGroup,false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {

        myHolder.subjectName.setText(studentAttendanceModels.get(i).getSubject());
        myHolder.percent.setText(studentAttendanceModels.get(i).getPercent() + " %");
        myHolder.subjectType.setText(studentAttendanceModels.get(i).getType());
    }

    @Override
    public int getItemCount() {
        return studentAttendanceModels.size();
    }
    public class MyHolder extends RecyclerView.ViewHolder {

        TextView subjectName,percent,subjectType;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            this.subjectName = itemView.findViewById(R.id.subject1);
            this.percent = itemView.findViewById(R.id.percent1);
            this.subjectType = itemView.findViewById(R.id.subject2);
        }
    }

}
