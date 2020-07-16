package com.google.firebase.samples.apps.mlkit.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.samples.apps.mlkit.LivePreviewActivity;
import com.google.firebase.samples.apps.mlkit.R;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private Spinner spinner;
    private Button mBtnAttendance;
    private ChipGroup chipGroup;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        galleryViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });


        spinner = root.findViewById(R.id.spinner_list);
        mBtnAttendance = root.findViewById(R.id.btn_take_attendance);
        chipGroup = root.findViewById(R.id.cg_stud_names);

        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item, galleryViewModel.getListOfSubjects());
        spinner.setAdapter(spinner_adapter);

        mBtnAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipGroup.removeAllViews();
                Chip chip = new Chip(getContext());Chip chip1 = new Chip(getContext());Chip chip2 = new Chip(getContext());Chip chip3 = new Chip(getContext());
                chip.setText("Kunal Kamra");chip1.setText("John Doe");chip2.setText("Lorem ipsum");chip3.setText("Tanmay Bhatt");
                chipGroup.addView(chip);chipGroup.addView(chip1);
                chipGroup.addView(chip2);chipGroup.addView(chip3);
            }
        });

        return root;
    }
}