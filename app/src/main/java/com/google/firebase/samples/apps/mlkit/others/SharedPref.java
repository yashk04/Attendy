package com.google.firebase.samples.apps.mlkit.others;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPref {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String IS_LOGGED_IN = "IS_LOGGED_IN";
    public static String NAME = "NAME";
    private String MOBILE = "MOBILE";
    private Context mContext;
    private String PREF_NAME = "USER_INFO";
    private String ID = "ID";
    private String DIVISION = "DIVISION";
    private String IS_STUDENT = "IS_STUDENT";
    private String IP = "IP_ADDR";
    private String PORT = "PORT_NUM";

    public SharedPref(Context context) {
        this.mContext = context;
        sharedPreferences = mContext.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    //for student info
    public SharedPref(Context context,String student_id, String name, String mobile,String division,boolean isStudent) {
        this.mContext = context;
        sharedPreferences = mContext.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(ID,student_id);
        editor.putString(NAME,name);
        editor.putString(MOBILE,mobile);
        editor.putString(DIVISION,division);
        editor.putBoolean(IS_STUDENT,isStudent);
//        setIsLoggedIn(true);
        editor.apply();
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public String getPREF_NAME() {
        return PREF_NAME;
    }


    public void setIsLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(IS_LOGGED_IN,isLoggedIn);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(IS_LOGGED_IN,false);
    }

    public void logout() {

        editor.remove(NAME);
        editor.remove(MOBILE);
        editor.remove(ID);
        editor.remove(DIVISION);
        editor.remove(IS_STUDENT);
        editor.commit();
    }

    public void setPort(String port)
    {
        editor.putString(PORT,port);
        editor.commit();
    }

    public void setIp(String ip)
    {

        editor.putString(IP,ip);
        editor.commit();
    }

    public String getIP() {
        return sharedPreferences.getString(IP,"");
    }
    public String getPort() {
        return sharedPreferences.getString(PORT,"");
    }
    public String getNAME() {
        return sharedPreferences.getString(NAME,"");
    }

    public Boolean isStudent() { return sharedPreferences.getBoolean(IS_STUDENT, true); }

    public String getDIVISION() {
        return sharedPreferences.getString(DIVISION,"");
    }

    public String getID() {
        return sharedPreferences.getString(ID,"");
    }

    public String getMOBILE() {
        return sharedPreferences.getString(MOBILE,"");
    }
}
