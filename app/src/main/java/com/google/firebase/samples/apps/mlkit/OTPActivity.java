package com.google.firebase.samples.apps.mlkit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.samples.apps.mlkit.others.CustomAlertDialog;
import com.google.firebase.samples.apps.mlkit.others.SharedPref;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {

    private Context mContext;
    private Button btnVerify;

    private EditText mEtOtp1, mEtOtp2, mEtOtp3, mEtOtp4, mEtOtp5, mEtOtp6;
    private String verificationId;
    private FirebaseAuth mAuth;
    String phoneNumber;
    boolean isStudent;
    private TextView mTvResendOtp, mTvDidNotReceive, mTvTimer, mTvPhoneno;
    private CountDownTimer mCountDownTimer;
    private PhoneAuthProvider.ForceResendingToken mForceResendingToken;
    private ArrayList<EditText> mArrayList;
    private CustomAlertDialog customAlertDialog;
    SharedPref sharedPref;
    private static final String TAG = "OTPActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        mContext = OTPActivity.this;
        btnVerify = findViewById(R.id.btn_verify);
        mEtOtp1 = findViewById(R.id.otp_edit_text1);
        mEtOtp2 = findViewById(R.id.otp_edit_text2);
        mEtOtp3 = findViewById(R.id.otp_edit_text3);
        mEtOtp4 = findViewById(R.id.otp_edit_text4);
        mEtOtp5 = findViewById(R.id.otp_edit_text5);
        mEtOtp6 = findViewById(R.id.otp_edit_text6);
        mTvResendOtp = findViewById(R.id.tv_resend);
        customAlertDialog = new CustomAlertDialog(mContext);
        sharedPref =  new SharedPref(mContext);
        customAlertDialog.setTextViewText("Validating...");

        addTextWatcher(mEtOtp1);
        addTextWatcher(mEtOtp2);
        addTextWatcher(mEtOtp3);
        addTextWatcher(mEtOtp4);
        addTextWatcher(mEtOtp5);
        addTextWatcher(mEtOtp6);
        mArrayList = new ArrayList<>();
        mArrayList.add(mEtOtp1);
        mArrayList.add(mEtOtp2);
        mArrayList.add(mEtOtp3);
        mArrayList.add(mEtOtp4);
        mArrayList.add(mEtOtp5);
        mArrayList.add(mEtOtp6);

        mTvDidNotReceive = findViewById(R.id.tv_did_not_receive);
        mTvPhoneno = findViewById(R.id.tv_phone_no);
        mTvTimer = findViewById(R.id.tv_timer);
        mTvResendOtp.setVisibility(View.GONE);
        mTvDidNotReceive.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
        phoneNumber = getIntent().getStringExtra("phoneNumber");

        isStudent = getIntent().getBooleanExtra("isStudent",true);

        sendVerificationCode(phoneNumber);

        mTvPhoneno.setText(phoneNumber.substring(10));

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringBuilder OTP = null;
                for(int i = 0; i < mArrayList.size(); i++) {
                    OTP = (OTP == null ? new StringBuilder("") : OTP).append(mArrayList.get(i).getText().toString());
                }
                if(!OTP.toString().equals(""))
                    verifyOTP(OTP.toString().trim());
                else
                    Toast.makeText(mContext, "Please enter OTP", Toast.LENGTH_SHORT).show();
            }
        });

        mTvResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTvResendOtp.setVisibility(View.GONE);
                mTvDidNotReceive.setVisibility(View.GONE);
                resendOTP(phoneNumber,mForceResendingToken);
            }
        });

    }

    private void verifyOTP(String code) {
        if(!customAlertDialog.isShowing())
            customAlertDialog.show();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential){

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if(isStudent)
                            {
                                Intent intent = new Intent(OTPActivity.this,StudentAttendanceActivity.class);
                                //intent.putExtra("phoneNumber",phoneNumber);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                sharedPref.setIsLoggedIn(true);
                                startActivity(intent);
                            }
                            else
                            {
                                Intent intent = new Intent(OTPActivity.this, TeacherAttendanceActivity.class);
                               // intent.putExtra("phoneNumber",phoneNumber);
                                sharedPref.setIsLoggedIn(true);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }



                        } else {
                                customAlertDialog.dismiss();
                                String message = "Something is wrong, we will fix it soon...";

                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    message = "Invalid code";
                                }
                                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: " + task.getException().getMessage());
                        }
                    }
                });

    }

    private void sendVerificationCode(String phoneNumber) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,60, TimeUnit.SECONDS, this, mCallBack);

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            mForceResendingToken = forceResendingToken;
            mTvTimer.setVisibility(View.VISIBLE);
            startTimer();
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();

            if(code != null) {
                for(int i = 0; i < mArrayList.size(); i++) {
                    mArrayList.get(i).setText(String.valueOf(code.charAt(i)));
                }
                if(!customAlertDialog.isShowing())
                    customAlertDialog.show();
                verifyOTP(code);
            }
            else {
                Toast.makeText(mContext, "Instant Verified", Toast.LENGTH_SHORT).show();
                signInWithCredential(phoneAuthCredential);
            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

            Toast.makeText(OTPActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    };

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(60000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTvTimer.setText("Expires in: "+ millisUntilFinished/1000 + " sec");
            }

            @Override
            public void onFinish() {
                mTvResendOtp.setVisibility(View.VISIBLE);
                mTvDidNotReceive.setVisibility(View.VISIBLE);
                mTvTimer.setVisibility(View.INVISIBLE);
                mTvTimer.setVisibility(View.GONE);
                if(mCountDownTimer != null)
                    mCountDownTimer.cancel();
            }
        };
        mCountDownTimer.start();
    }

    private void resendOTP(String mob, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(mob, 60, TimeUnit.SECONDS, this, mCallBack, forceResendingToken);
    }

    private void addTextWatcher(final EditText one) {
        one.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                switch(one.getId()) {
                    case R.id.otp_edit_text1:
                        if(one.length() == 1) {
                            mEtOtp2.requestFocus();
                        }
                        break;
                    case R.id.otp_edit_text2:
                        if(one.length() == 1) {
                            mEtOtp3.requestFocus();
                        }
                        else if(one.length() == 0) {
                            mEtOtp1.requestFocus();
                        }
                        break;
                    case R.id.otp_edit_text3:
                        if(one.length() == 1) {
                            mEtOtp4.requestFocus();
                        }
                        else if(one.length() == 0) {
                            mEtOtp2.requestFocus();
                        }
                        break;
                    case R.id.otp_edit_text4:
                        if(one.length() == 1) {
                            mEtOtp5.requestFocus();
                        }
                        else if(one.length() == 0) {
                            mEtOtp3.requestFocus();
                        }
                        break;
                    case R.id.otp_edit_text5:
                        if(one.length() == 1) {
                            mEtOtp6.requestFocus();
                        }
                        else if(one.length() == 0) {
                            mEtOtp4.requestFocus();
                        }
                        break;
                    case R.id.otp_edit_text6:
                        if(one.length() == 1) {
                            InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputManager.hideSoftInputFromWindow(OTPActivity.this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                        else if(one.length() == 0) {
                            mEtOtp5.requestFocus();
                        }
                        break;
                }
            }
        });
    }

}