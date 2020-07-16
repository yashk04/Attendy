package com.google.firebase.samples.apps.mlkit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.samples.apps.mlkit.adapters.StudentMarkAdapter;
import com.google.firebase.samples.apps.mlkit.models.StudentMarkRvModel;
import com.google.firebase.samples.apps.mlkit.models.StudentModel;
import com.google.firebase.samples.apps.mlkit.models.SubjectInStudentModel;
import com.google.firebase.samples.apps.mlkit.models.SubjectModel;
import com.google.firebase.samples.apps.mlkit.others.CustomAlertDialog;
import com.google.firebase.samples.apps.mlkit.others.SharedPref;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class AttendanceActivity extends AppCompatActivity {
    Context mContext;
    RecyclerView recyclerView;
    StudentMarkAdapter attendanceAdapter;
    private Button markAttendanceButton;
    LinearLayout horizontalScrollView;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference subjectCollection = db.collection("subjectCollection");
    private CollectionReference studentCollection = db.collection("studentCollection");
    private int subjectId;
    private int teacherId;
    private static TextView totalStudentsView;
    private SharedPref sharedPref;
    private ArrayList<String> presentStudents;
    private static int totalPresentStudents;
    private static ArrayList<StudentMarkRvModel> models;
    private static final String TAG = "AttendanceActivity";
    private static ProgressBar progressBar;
    private static int totalStudents;
    private CustomAlertDialog alertDialog;
    private HashMap<String,String> docIds;
    private ArrayList<StudentModel> studentModelsInDb;
    private int updatingStudentsCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        TeacherAttendanceActivity.startRecog = true;
        horizontalScrollView = findViewById(R.id.test);
        mContext = getApplicationContext();
        markAttendanceButton = findViewById(R.id.mark_attendance_button);
        sharedPref = new SharedPref(mContext);
        alertDialog = new CustomAlertDialog(AttendanceActivity.this);
        progressBar = findViewById(R.id.pb_students_percent);
        teacherId = Integer.valueOf(sharedPref.getID());
        subjectId = getIntent().getIntExtra("subjectId",-1);
        presentStudents = new ArrayList<>();
//        presentStudents.addAll(Arrays.asList(new String[]{"C2K17105589", "C2K17105624"}));
        presentStudents = getIntent().getStringArrayListExtra("recognizedIds");
        totalStudentsView = findViewById(R.id.num_students);
        totalPresentStudents=0;
        totalStudents = 0;
        docIds = new HashMap<>();
        studentModelsInDb = new ArrayList<>();
        recyclerView = findViewById(R.id.rv_student_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        models = new ArrayList<>();
        getAllStudentsRegisteredToSubject();
        Log.d(TAG, "onCreate: " + subjectId);

        markAttendanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();
                alertDialog.setTextViewText("Updating");
                markAttendance();
                updateAttendanceOfSubject();
            }
        });

        Log.d(TAG, "onCreate: " + TeacherAttendanceActivity.imageViews.size());
        if(TeacherAttendanceActivity.imageViews.size() > 0)
        for( ImageView imageView : TeacherAttendanceActivity.imageViews )
        {
            ViewGroup p = (ViewGroup) imageView.getParent();
            if(p!=null)
            {
                p.removeView(imageView);
                imageView.setMaxWidth(150);
                imageView.setMaxHeight(150);
                horizontalScrollView.addView(imageView);
            }
        }
    }

    private void markAttendance() {
        updatingStudentsCount = 0;
        Log.d(TAG, "markAttendance: " + totalPresentStudents);
        for( StudentModel studentModel : studentModelsInDb )
        {
            for(StudentMarkRvModel model : models)
            {
                if(model.isLecture1())
                    if(model.getRollNumber().equals(studentModel.getId()))
                    {
                        String id = docIds.get(studentModel.getId());
                        studentCollection.document(id).set(studentModel, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                updatingStudentsCount++;
                                if(updatingStudentsCount == totalPresentStudents)
                                {
                                    Log.d(TAG, "onComplete: here" + updatingStudentsCount);
                                    alertDialog.dismiss();
                                    Toast.makeText(mContext, "Attendance marked successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
                    }
            }
        }
    }

    public void updateAttendanceOfSubject() {
        models.clear();
        subjectCollection.whereEqualTo("id", subjectId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    ArrayList<String> dates;
                    SubjectModel subjectModel = documentSnapshot.toObject(SubjectModel.class);
                    try {
                        dates = new ArrayList<>(subjectModel.getDates());
                    } catch (NullPointerException e) {
                        dates = new ArrayList<>();
                    }
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    String currentDate = sdf.format(new Date());
                    dates.add(currentDate);
                    Log.i("subjectdate ", dates.toString() + subjectModel.getName());
                    subjectModel.setDates(dates);
                    subjectCollection.document(documentSnapshot.getId()).set(subjectModel, SetOptions.merge());
                }
            }
        });

    }

    public void getAllStudentsRegisteredToSubject() {
        alertDialog.setTextViewText("Fetching");
        alertDialog.show();
        studentCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for( DocumentSnapshot documentSnapshot : queryDocumentSnapshots )
                {
                    StudentModel student = documentSnapshot.toObject(StudentModel.class);
                    ArrayList<SubjectInStudentModel> studentSubjects= student.getSubjects();
                    int index = findIndex(studentSubjects);
                    ArrayList<String> dates;
                    Log.d(TAG, "onSuccess: index" + index);
                    if(index != -1)
                    {
                        totalStudents++;
                        SubjectInStudentModel subject = studentSubjects.get(index);
                        try {
                            dates = new ArrayList<>(subject.getDates());
                        } catch (NullPointerException e) {
                            dates = new ArrayList<>();
                        }
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        String currentDate = sdf.format(new Date());
                        dates.add(currentDate);
                        subject.setDates(dates);
                        studentSubjects.set(index,subject);
                        student.setSubjects(studentSubjects);
                        if(presentStudents.contains(student.getId()))
                        {
                            totalPresentStudents++;
                            models.add(new StudentMarkRvModel(student.getName(),student.getId(),true));
                        }
                        else
                            models.add(new StudentMarkRvModel(student.getName(),student.getId(),false));

                        docIds.put(student.getId(),documentSnapshot.getId());
                        studentModelsInDb.add(student);
                        attendanceAdapter = new StudentMarkAdapter(mContext,models,1);
                        recyclerView.setAdapter(attendanceAdapter);
                        totalStudentsView.setText(String.valueOf(totalPresentStudents));
                        progressBar.setProgress(totalPresentStudents*100/totalStudents);
                        alertDialog.dismiss();
                    }
                }
            }
        });
    }
    private int findIndex(ArrayList<SubjectInStudentModel> subjects)
    {
        int index=0;
        for(SubjectInStudentModel subject:subjects)
        {
            if(subject.getId()==subjectId)
            {
                return index;
            }
            index++;
        }
        return -1;
    }

    public static void updatePresentStudents()
    {
        totalPresentStudents = 0;
        for( StudentMarkRvModel markRvModel : models)
        {
            if(markRvModel.isLecture1())
                totalPresentStudents++;
        }
        totalStudentsView.setText(String.valueOf(totalPresentStudents));
        progressBar.setProgress(totalPresentStudents*100/totalStudents);
    }

}
