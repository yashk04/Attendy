package com.google.firebase.samples.apps.mlkit;

import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.MenuItem;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.samples.apps.mlkit.others.CustomAlertDialog;
import com.google.firebase.samples.apps.mlkit.others.SharedPref;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;

import java.util.ArrayList;

public class TeacherAttendanceActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    SharedPref sharedPref;
    Context mContext;
    public static ArrayList<ImageView> imageViews;
    public static CustomAlertDialog customAlertDialog;
    public static boolean startRecog = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;
        sharedPref = new SharedPref(mContext);
        imageViews = new ArrayList<>();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        customAlertDialog = new CustomAlertDialog(mContext);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.action_logout:
            {
                sharedPref.logout();
                sharedPref.setIsLoggedIn(false);
                Intent intent = new Intent(mContext, LoginActivity.class);
                finishAffinity();
                startActivity(intent);
                finish();
                break;
            }
            case R.id.action_profile:
                startActivity(new Intent(mContext,TeacherProfileActivity.class));
                break;
            case R.id.action_settings:
                startActivity(new Intent(mContext,SettingsActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        imageViews.clear();
    }
}
