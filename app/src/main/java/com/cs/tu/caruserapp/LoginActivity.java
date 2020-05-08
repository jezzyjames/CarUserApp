package com.cs.tu.caruserapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    CountryCodePicker ccp;
    TextInputLayout layoutPhone;
    TextInputEditText editText_phone;
    TextView phone_refer;
    TextInputLayout layoutVerifyCode;
    TextInputEditText editText_code;
    TextView resend_code;
    Button verify_btn;
    ProgressBar verify_progress;

    boolean code_sent = false;
    String phoneNumber = "";
    LinearLayout phone_view_part;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    FirebaseAuth firebaseAuth;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;

    private static final String TAG = "Login";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");

        phone_view_part = findViewById(R.id.phone_view_part);
        ccp = findViewById(R.id.ccp);
        layoutPhone = findViewById(R.id.layoutPhone);
        editText_phone = findViewById(R.id.edt_phone);
        phone_refer = findViewById(R.id.phone_refer);
        layoutVerifyCode = findViewById(R.id.layoutVerifyCode);
        editText_code = findViewById(R.id.edt_verify_code);
        resend_code = findViewById(R.id.resend_code);
        verify_btn = findViewById(R.id.verify_btn);
        verify_progress = findViewById(R.id.verify_progress);

        ccp.registerCarrierNumberEditText(editText_phone);

        firebaseAuth = FirebaseAuth.getInstance();

        verify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //*******verify code*******
                if(verify_btn.getText().equals("Submit") || code_sent){
                    String verificationCode = editText_code.getText().toString();

                    if(verificationCode.equals("")){
                        Toast.makeText(LoginActivity.this, "Please enter verification code", Toast.LENGTH_SHORT).show();
                    }else{
                        verify_progress.setVisibility(View.VISIBLE);

                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                        signInWithPhoneAuthCredential(credential);
                    }

                    //*******verify phone number*******
                }else{
                    verify_btn.setVisibility(View.GONE);
                    verify_progress.setVisibility(View.VISIBLE);

                    if (!editText_phone.getText().toString().equals("")) {
                        layoutPhone.setErrorEnabled(false);
                        phoneNumber = ccp.getFullNumberWithPlus();

                        //Check if phone number is exist
                        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phone_number").equalTo(phoneNumber);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    layoutPhone.setErrorEnabled(false);
                                    verify_btn.setVisibility(View.GONE);
                                    verify_progress.setVisibility(View.VISIBLE);

                                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                            phoneNumber,                                          //phone number to verify
                                            60,                                                //timeout duration
                                            TimeUnit.SECONDS,                                     //unit of timeout
                                            LoginActivity.this,                   //activity for callback
                                            mCallbacks);                                          //onVerificationStateChangedCallbacks
                                } else {
                                    layoutPhone.setError("Can't find this phone number in system, please register before login.");
                                    Toast.makeText(LoginActivity.this, "Can't find this phone number in system, please register before login.", Toast.LENGTH_SHORT).show();
                                    verify_btn.setVisibility(View.VISIBLE);
                                    verify_progress.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e("", databaseError.getMessage());
                            }
                        });

                    } else {
                        layoutPhone.setError("Please enter a valid phone number");
                        Toast.makeText(LoginActivity.this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                        verify_btn.setVisibility(View.VISIBLE);
                        verify_progress.setVisibility(View.GONE);
                    }

                }

            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            //Instant verification: in some cases the phone number can be instantly verified **without needing to send or enter a verification code**.
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                if(e instanceof FirebaseAuthInvalidCredentialsException){
                    Toast.makeText(LoginActivity.this, "Invalid credential: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }else if(e instanceof FirebaseTooManyRequestsException){
                    Toast.makeText(LoginActivity.this, "SMS Quota exceeded", Toast.LENGTH_SHORT).show();
                }

                verify_progress.setVisibility(View.GONE);
                verify_btn.setVisibility(View.VISIBLE);
                phone_view_part.setVisibility(View.VISIBLE);

                verify_btn.setText("Continue");
                code_sent = false;
                layoutVerifyCode.setVisibility(View.GONE);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                mVerificationId = s;
                mResendToken = forceResendingToken;

                phone_view_part.setVisibility(View.GONE);
                code_sent = true;

                phone_refer.setVisibility(View.VISIBLE);
                phone_refer.setText("Message was sent to\n" + phoneNumber);

                verify_btn.setText("Submit");
                verify_btn.setVisibility(View.VISIBLE);
                layoutVerifyCode.setVisibility(View.VISIBLE);

                //set resend code button
                resend_code.setVisibility(View.VISIBLE);
                SpannableString content = new SpannableString("Resend");
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                resend_code.setText(content);
                resend_code.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resendCode(phoneNumber, mResendToken);
                    }
                });

                verify_progress.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Code has been sent, please check.", Toast.LENGTH_SHORT).show();
            }
        };

    }

    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            verify_progress.setVisibility(View.GONE);

                            Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            Toast.makeText(LoginActivity.this, "Error: " + task.getException().toString(), Toast.LENGTH_SHORT).show();

                            verify_progress.setVisibility(View.GONE);
                            verify_btn.setVisibility(View.VISIBLE);

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void resendCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken mResendToken) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                mResendToken);             // ForceResendingToken from callbacks
    }

}