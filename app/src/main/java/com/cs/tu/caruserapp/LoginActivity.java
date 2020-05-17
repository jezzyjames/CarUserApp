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
    String raw_phoneNumber = "";
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
        getSupportActionBar().setTitle(getString(R.string.login));

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
                if(verify_btn.getText().toString().equalsIgnoreCase(getString(R.string.submit)) || code_sent){
                    String verificationCode = editText_code.getText().toString();

                    if(verificationCode.equals("")){
                        Toast.makeText(LoginActivity.this, getString(R.string.please_enter_code), Toast.LENGTH_SHORT).show();
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
                        //admin test//
                        if(editText_phone.getText().toString().equals("1212 312 121")) {
                            FirebaseAuth auth = firebaseAuth.getInstance();
                            auth.signInWithEmailAndPassword("jetdokoalah@gmail.com", "1212312121").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }else{
                                        Toast.makeText(LoginActivity.this, "Admin Login Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        //admin Test//
                        layoutPhone.setErrorEnabled(false);
                        phoneNumber = ccp.getFullNumberWithPlus();
                        raw_phoneNumber = ccp.getSelectedCountryCodeWithPlus() + " " + editText_phone.getText().toString();

                        //Check if phone number is exist
                        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phone_number").equalTo(raw_phoneNumber);
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
                                    layoutPhone.setError(getString(R.string.cant_find_phonenumber));
                                    Toast.makeText(LoginActivity.this, getString(R.string.cant_find_phonenumber), Toast.LENGTH_SHORT).show();
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
                        layoutPhone.setError(getString(R.string.please_enter_valid_number));
                        Toast.makeText(LoginActivity.this, getString(R.string.please_enter_valid_number), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(LoginActivity.this, getString(R.string.invalid_credential) + ": " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }else if(e instanceof FirebaseTooManyRequestsException){
                    Toast.makeText(LoginActivity.this, getString(R.string.sms_error), Toast.LENGTH_SHORT).show();
                }

                verify_progress.setVisibility(View.GONE);
                verify_btn.setVisibility(View.VISIBLE);
                phone_view_part.setVisibility(View.VISIBLE);

                verify_btn.setText(getString(R.string.continue_txt));
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

                String phoneArr[] = raw_phoneNumber.split(" ");
                String hidden_number = phoneArr[0];
                for(int i = 1; i < phoneArr.length-1; i++){
                    String phone_part = phoneArr[i];
                    for(int j = 0; j < phone_part.length(); j++){
                        if(j == 0){
                            hidden_number = hidden_number + " ";
                        }
                        hidden_number = hidden_number + "x";
                    }
                }
                hidden_number = hidden_number + " " + phoneArr[phoneArr.length-1];

                phone_refer.setVisibility(View.VISIBLE);
                phone_refer.setText(getString(R.string.code_was_sent) + hidden_number);

                verify_btn.setText(getString(R.string.submit));
                verify_btn.setVisibility(View.VISIBLE);
                layoutVerifyCode.setVisibility(View.VISIBLE);

                //set resend code button
                resend_code.setVisibility(View.VISIBLE);
                SpannableString content = new SpannableString(getString(R.string.resend));
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                resend_code.setText(content);
                resend_code.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resendCode(phoneNumber, mResendToken);
                    }
                });

                verify_progress.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, getString(R.string.code_has_been_sent), Toast.LENGTH_SHORT).show();
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

                            Toast.makeText(LoginActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            Toast.makeText(LoginActivity.this, getString(R.string.error) + ": " + task.getException().toString(), Toast.LENGTH_SHORT).show();

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