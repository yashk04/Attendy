package com.google.firebase.samples.apps.mlkit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.samples.apps.mlkit.AttendanceActivity;
import com.google.firebase.samples.apps.mlkit.R;
import com.google.firebase.samples.apps.mlkit.models.StudentMarkRvModel;

import java.util.ArrayList;

public class StudentMarkAdapter extends RecyclerView.Adapter<StudentMarkAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<StudentMarkRvModel> studentModels;
    private int numLectures;

    public StudentMarkAdapter(Context context, ArrayList<StudentMarkRvModel> studentModels, int numOfLectures) {
        this.context = context;
        this.studentModels = studentModels;
        this.numLectures = numOfLectures;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_student_info,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final StudentMarkRvModel studentModel = studentModels.get(position);
        holder.name.setText(studentModel.getName());
        holder.rollNumber.setText(studentModel.getRollNumber());
        holder.lecture1.setChecked(studentModel.isLecture1());
        holder.lecture1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                studentModel.setLecture1(isChecked);
                AttendanceActivity.updatePresentStudents();
            }
        });
//        holder.lecture2.setChecked(studentModel.isLecture2());
    }

    @Override
    public int getItemCount() {
        return studentModels.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private CheckBox lecture1;
//        private CheckBox lecture2;
        private TextView name;
        private TextView rollNumber;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_student_name);
            rollNumber = itemView.findViewById(R.id.tv_student_r_no);
            lecture1 = itemView.findViewById(R.id.lecture1);
//            lecture2 = itemView.findViewById(R.id.lecture2);
        }
    }
}
