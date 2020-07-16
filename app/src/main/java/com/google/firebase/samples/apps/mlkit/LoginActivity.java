package com.google.firebase.samples.apps.mlkit;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.samples.apps.mlkit.models.StudentModel;
import com.google.firebase.samples.apps.mlkit.models.TeacherModel;
import com.google.firebase.samples.apps.mlkit.others.CustomAlertDialog;
import com.google.firebase.samples.apps.mlkit.others.SharedPref;


public class LoginActivity extends AppCompatActivity {

    Context mContext;
    Context context;
    EditText mEtMobNo;
    TextView textView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference teacherCollection = db.collection("teacherCollection");
    CollectionReference studentCollection = db.collection("studentCollection");
    CreateDatabase database = new CreateDatabase();
    int subjectId = 1;
    private String phoneNumber;
    boolean isStudent=false;
    private String name;
    private String division;
    private String studentId;
    private int teacherId;
    private SharedPref sharedPref;
    private CustomAlertDialog alertDialog;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = getApplicationContext();
        sharedPref = new SharedPref(mContext);
        alertDialog = new CustomAlertDialog(LoginActivity.this);
        alertDialog.setTextViewText("Logging In");
        if(sharedPref.isLoggedIn())
        {
            if(sharedPref.isStudent())
            {
                startActivity(new Intent(mContext,StudentAttendanceActivity.class));
                finish();
            }
            else
            {
                startActivity(new Intent(mContext,TeacherAttendanceActivity.class));
                finish();
            }
        }
        Log.i("subjectLogin","in login activity");
        context=this;
        mEtMobNo = (EditText)findViewById(R.id.et_mobno);

        mEtMobNo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if((motionEvent.getRawX() >= (mEtMobNo.getRight() - mEtMobNo.getCompoundDrawables()[2].getBounds().width() - 20))) {
                        if(validate())
                        {
                            phoneNumber = mEtMobNo.getText().toString();
                            validateUserLogin();
                            alertDialog.show();
                        }

                        return true;
                    }
                }
            return false;
            }
        });
    }

    private void validateUserLogin() {
        studentCollection.whereEqualTo("phoneNumber",phoneNumber).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty())
                {
                    isStudent=true;
                    phoneNumber="+91"+phoneNumber;
                    DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                    StudentModel student = document.toObject(StudentModel.class);
                    name = student.getName();
                    studentId = student.getId();
                    division = student.getDiv();
                    sharedPref=new SharedPref(mContext, studentId,name,phoneNumber,division,isStudent);
                    alertDialog.dismiss();
                    Intent intent = new Intent(LoginActivity.this, OTPActivity.class);
                    intent.putExtra("phoneNumber",phoneNumber);
                    startActivity(intent);
                }
                else{
                    teacherCollection.whereEqualTo("phoneNumber",phoneNumber).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (queryDocumentSnapshots.isEmpty()) {
                                alertDialog.dismiss();
                                Toast.makeText(mContext, "No Such User", Toast.LENGTH_SHORT).show();
                            } else{
                                isStudent=false;
                                DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                                TeacherModel teacher = document.toObject(TeacherModel.class);
                                name = teacher.getName();
                                teacherId = teacher.getId();
                                sharedPref=new SharedPref(mContext, String.valueOf(teacherId),name,phoneNumber,"",isStudent);
                                phoneNumber="+91"+phoneNumber;
                                alertDialog.dismiss();
                                Intent intent = new Intent(LoginActivity.this, OTPActivity.class);
                                intent.putExtra("phoneNumber", phoneNumber);
                                intent.putExtra("isStudent",isStudent);
                                startActivity(intent);
                            }
                        }
                    });
                }

            }
        });
    }

    private boolean validate() {
        if(mEtMobNo.getText().length() != 10) {
            mEtMobNo.setError("Invalid mobile number ");
            mEtMobNo.requestFocus();
            return false;
        }
        return true;
    }
}
