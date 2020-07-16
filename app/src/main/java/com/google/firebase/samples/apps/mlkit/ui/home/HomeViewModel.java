package com.google.firebase.samples.apps.mlkit.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.samples.apps.mlkit.others.SharedPref;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference subjectCollection = db.collection("subjectCollection");
    private MutableLiveData<String> mText;




    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Take Attendance");



    }

    public LiveData<String> getText() {
        return mText;
    }
}