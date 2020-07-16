package com.google.firebase.samples.apps.mlkit;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.samples.apps.mlkit.models.StudentModel;
import com.google.firebase.samples.apps.mlkit.others.SharedPref;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentProfileActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    SharedPreferences sp;

    DatabaseReference reference;

    StorageReference storageReference;
    private static final int image_request = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    TextView studentIdView;
    TextView nameView;
    TextView classNameView;
    TextView phoneNumberView;
    SharedPref sharedPref;
    Context mContext;
    Button logoutButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);
        logoutButton = findViewById(R.id.btn_logout);
        studentIdView = findViewById(R.id.tv_profile_id);
        nameView = findViewById(R.id.tv_profile_name);
        classNameView = findViewById(R.id.tv_profile_class);
        profileImage = findViewById(R.id.profile_image);

        sp=getSharedPreferences("profilePicture",MODE_PRIVATE);

        if(!sp.getString("dp","").equals("")){
            byte[] decodedString = Base64.decode(sp.getString("dp", ""), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            profileImage.setImageBitmap(decodedByte);
        }

        mContext = this;
        sharedPref = new SharedPref(mContext);
        studentIdView.setText(sharedPref.getID());
        nameView.setText(sharedPref.getNAME());
        classNameView.setText(sharedPref.getDIVISION());
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPref.logout();
                sharedPref.setIsLoggedIn(false);
                Intent intent = new Intent(StudentProfileActivity.this, LoginActivity.class);
                finishAffinity();
                startActivity(intent);
                finish();
            }
        });

        getSupportActionBar().setTitle("Profile Activity");

        profileImage.setOnClickListener(new View.OnClickListener() {
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

                profileImage.setImageBitmap(bitmap);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                byte[] b = baos.toByteArray();
                String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                sp.edit().putString("dp", encodedImage).commit();

            } catch (FileNotFoundException e) {


                e.printStackTrace();
            }

        }

    }

    /*private String getFileExtension(Uri uri) {

        ContentResolver contentResolver = getBaseContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    private void uploadImage() {

        final ProgressDialog pd = new ProgressDialog(getBaseContext());
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null) {

            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            uploadTask = fileReference.getFile(imageUri);
            uploadTask.continueWith(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()) {

                        throw task.getException();

                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()) {

                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("Users").child

                    }

                }
            })

        }*/


}
