package com.google.firebase.samples.apps.mlkit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.samples.apps.mlkit.others.SharedPref;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherProfileActivity extends AppCompatActivity {

    SharedPreferences sp1;
    TextView teacherIdView;
    TextView nameViewTeacher;
    TextView mobileNoView;
    SharedPref sharedPref;
    Context mContext;
    Button logoutButton1;
    private CircleImageView profileImage1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);

        logoutButton1 = findViewById(R.id.btn_logout1);
        teacherIdView = findViewById(R.id.tv_profile_id_teacher);
        nameViewTeacher = findViewById(R.id.tv_profile_name_teacher);
        mobileNoView = findViewById(R.id.tv_profile_mobno);
        profileImage1 = findViewById(R.id.profile_image_teacher);

        sp1=getSharedPreferences("profilePicture",MODE_PRIVATE);

        if(!sp1.getString("dp","").equals("")){
            byte[] decodedString = Base64.decode(sp1.getString("dp", ""), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            profileImage1.setImageBitmap(decodedByte);
        }

        mContext = this;
        sharedPref = new SharedPref(mContext);
        teacherIdView.setText(sharedPref.getID());
        nameViewTeacher.setText(sharedPref.getNAME());
        mobileNoView.setText(sharedPref.getMOBILE());
        logoutButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPref.logout();
                sharedPref.setIsLoggedIn(false);
                Intent intent = new Intent(TeacherProfileActivity.this, LoginActivity.class);
                finishAffinity();
                startActivity(intent);
                finish();
            }
        });

        getSupportActionBar().setTitle("Profile Activity");

        profileImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");

                startActivityForResult(Intent.createChooser(intent, "Pick a Image"), 1);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1) {

            try {

                InputStream inputStream = getContentResolver().openInputStream(data.getData());

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                profileImage1.setImageBitmap(bitmap);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                byte[] b = baos.toByteArray();
                String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                sp1.edit().putString("dp", encodedImage).commit();

            } catch (FileNotFoundException e) {

                e.printStackTrace();

            }
        }
    }
}
