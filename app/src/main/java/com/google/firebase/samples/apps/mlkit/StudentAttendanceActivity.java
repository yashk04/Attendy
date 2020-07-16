package com.google.firebase.samples.apps.mlkit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import android.view.View;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.samples.apps.mlkit.adapters.StudentAttendanceAdapter;
import com.google.firebase.samples.apps.mlkit.models.StudentAttendanceModel;
import com.google.firebase.samples.apps.mlkit.models.StudentModel;
import com.google.firebase.samples.apps.mlkit.models.SubjectInStudentModel;
import com.google.firebase.samples.apps.mlkit.models.SubjectModel;
import com.google.firebase.samples.apps.mlkit.others.CustomAlertDialog;
import com.google.firebase.samples.apps.mlkit.others.SharedPref;

import java.util.ArrayList;

public class StudentAttendanceActivity extends AppCompatActivity {

    CustomAlertDialog customAlertDialog;
    SwipeRefreshLayout swipeRefreshLayout;
    private static final String TAG = "StudentAttendanceActivi";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference studentCollection = db.collection("studentCollection");
    private CollectionReference subjectCollection = db.collection("subjectCollection");
    private RecyclerView mRecyclerView;
    private StudentAttendanceAdapter studentAttendanceAdapter;
    private TextView studentNameTextView;
    private TextView mTvPercent;
    private String studentName;
    private SharedPref sharedPref;
    private Context mContext;
    private String studentId;
    private float studentAvgPercent;
    private float studentAverageTotal;
//    private ArrayList<Integer> attendedCount = new ArrayList<>();
//    private ArrayList<Integer> totalCount=new ArrayList<>();
//    private ArrayList<String> nameOfSubject=new ArrayList<>();
//    private ArrayList<SubjectInStudentModel> subjects = new ArrayList<>();
    private ArrayList<Integer> subjectIds = new ArrayList<>();
    private ArrayList<StudentAttendanceModel> studentAttendanceModels = new ArrayList<>();
    private int iterator=0;
    private int subjectsWithAttendanceCount = 0;
    private NestedScrollView constraintLayout;
    CardView profileCard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studence_attendance);

        getSupportActionBar().setTitle("Student Attendance");

//        Log.i("subject activity",getApplicationContext().getApplicationInfo().toString());
        mContext = this;
        studentNameTextView = (TextView) findViewById(R.id.textView2);

        sharedPref=new SharedPref(mContext);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        studentName = sharedPref.getNAME();
        studentId = sharedPref.getID();
        Log.i("subjectid",studentId);
        studentNameTextView = findViewById(R.id.tv_student_name);
        studentNameTextView.setText(studentName);
        constraintLayout = findViewById(R.id.main_content);
        customAlertDialog = new CustomAlertDialog(StudentAttendanceActivity.this);
        customAlertDialog.setTextViewText("Fetching Data");
        customAlertDialog.show();
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        profileCard = findViewById(R.id.cv_student_info);
        profileCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StudentAttendanceActivity.this,StudentProfileActivity.class));
            }
        });
        mTvPercent = findViewById(R.id.student_percent);
        getMyList();
        Log.i("subjectarraylist",studentAttendanceModels.toString());

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                constraintLayout.setVisibility(View.GONE);
                getMyList();
            }
        });

    }

    public void getMyList() {

        studentCollection.whereEqualTo("id",studentId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                studentAverageTotal = 0;
                subjectsWithAttendanceCount = 0;
                studentAttendanceModels.clear();
                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                StudentModel student = documentSnapshot.toObject(StudentModel.class);
                for(SubjectInStudentModel subject : student.getSubjects())
                {
                    subjectIds.add(subject.getId());
                    StudentAttendanceModel model = new StudentAttendanceModel();
                    model.setSubjectId(subject.getId());
                    if(subject.getDates() != null)
                        model.setAttendedCount(subject.getDates().size());
                    else
                        model.setAttendedCount(0);
                    studentAttendanceModels.add(model);
                }
                Log.d(TAG, "onSuccess: " + subjectIds);
                for( final int id : subjectIds )
                {
                    subjectCollection.whereEqualTo("id",id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            studentAvgPercent = 0;
                            Log.d(TAG, "onSuccess: id:" + id);
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            Log.d(TAG, "onSuccess: size" + queryDocumentSnapshots.getDocuments().size());
                            SubjectModel subjectModel = documentSnapshot.toObject(SubjectModel.class);
                            Log.d(TAG, "onSuccess: subjectModel" + subjectModel.getName());
                            StudentAttendanceModel test = new StudentAttendanceModel();

                            for ( StudentAttendanceModel temp : studentAttendanceModels )
                            {
                                if(temp.getSubjectId() == subjectModel.getId())
                                {
                                    test = temp;
                                    break;
                                }
                            }

                            test.setSubject(subjectModel.getName());
                            test.setType(subjectModel.getType());
                            if(subjectModel.getDates() != null)
                                test.setTotalCount(subjectModel.getDates().size());
                            else
                            {
                                test.setTotalCount(0);
                                subjectsWithAttendanceCount++;
                            }
                            //test.setType(subjectModel.getDiv());
                            if(test.getTotalCount()==0)
                            {
                                test.setPercent("0");
                            }
                            else
                            {
                                float percentage  = (float)test.getAttendedCount()/test.getTotalCount();
                                percentage = percentage*100;
                                test.setPercent(String.format("%.2f", percentage));
                                studentAverageTotal = studentAverageTotal + percentage ;
                            }
                            Log.d(TAG, "onSuccess: size of models" + studentAttendanceModels.size());
                            studentAttendanceAdapter = new StudentAttendanceAdapter(mContext,studentAttendanceModels);
                            mRecyclerView.setAdapter(studentAttendanceAdapter);
                            if(customAlertDialog.isShowing())
                                customAlertDialog.dismiss();
                            constraintLayout.setVisibility(View.VISIBLE);
                            swipeRefreshLayout.setRefreshing(false);
                            studentAvgPercent = studentAverageTotal /(float)(subjectIds.size() - subjectsWithAttendanceCount );
                            mTvPercent.setText(String.format("%.2f", studentAvgPercent) + " %");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: " + id);
                            Log.d(TAG, "onFailure: " + e.getMessage());
                        }
                    });
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.student_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {

            case R.id.myprofile :

                startActivity(new Intent(getApplicationContext(), StudentProfileActivity.class));

                return true;

            case R.id.logout:

                sharedPref.logout();
                sharedPref.setIsLoggedIn(false);
                Intent intent = new Intent(StudentAttendanceActivity.this, LoginActivity.class);
                finishAffinity();
                startActivity(intent);
                finish();

                return true;

            default:
                return false;
        }
    }

}