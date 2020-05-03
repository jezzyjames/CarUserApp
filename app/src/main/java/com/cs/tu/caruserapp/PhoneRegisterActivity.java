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
import android.widget.RelativeLayout;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class PhoneRegisterActivity extends AppCompatActivity {
    LinearLayout layoutName;
    TextInputLayout layoutFirstname;
    TextInputEditText edt_firstname;
    TextInputLayout layoutLastname;
    TextInputEditText edt_lastname;
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
    String firstname = "";
    String lastname = "";
    String phoneNumber = "";
    LinearLayout phone_view_part;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    FirebaseAuth firebaseAuth;
    DatabaseReference reference;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;

    private static final String TAG = "PhoneRegister";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");

        layoutName = findViewById(R.id.layoutName);
        layoutFirstname = findViewById(R.id.layoutFirstname);
        edt_firstname = findViewById(R.id.edt_firstname);
        layoutLastname = findViewById(R.id.layoutLastname);
        edt_lastname = findViewById(R.id.edt_lastname);
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
                        Toast.makeText(PhoneRegisterActivity.this, "Please enter verification code", Toast.LENGTH_SHORT).show();
                    }else{
                        verify_progress.setVisibility(View.VISIBLE);

                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                        signInWithPhoneAuthCredential(credential);
                    }

                    //*******verify phone number*******
                }else{
                    verify_btn.setVisibility(View.GONE);
                    verify_progress.setVisibility(View.VISIBLE);
                    if(!edt_firstname.getText().toString().equals("") && !edt_lastname.getText().toString().equals("")) {
                        layoutFirstname.setErrorEnabled(false);
                        layoutLastname.setErrorEnabled(false);
                        firstname = edt_firstname.getText().toString();
                        lastname = edt_lastname.getText().toString();

                        if (!editText_phone.getText().toString().equals("")) {
                            layoutPhone.setErrorEnabled(false);
                            phoneNumber = ccp.getFullNumberWithPlus();

                            //Check if phone number is exist
                            Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phone_number").equalTo(phoneNumber);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()) {
                                        layoutPhone.setErrorEnabled(false);
                                        verify_btn.setVisibility(View.GONE);
                                        verify_progress.setVisibility(View.VISIBLE);

                                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                                phoneNumber,                                          //phone number to verify
                                                60,                                                //timeout duration
                                                TimeUnit.SECONDS,                                     //unit of timeout
                                                PhoneRegisterActivity.this,                   //activity for callback
                                                mCallbacks);                                          //onVerificationStateChangedCallbacks
                                    } else {
                                        layoutPhone.setError("This phone number is used, please use other phone number.");
                                        Toast.makeText(PhoneRegisterActivity.this, "This phone number is used, please use other phone number.", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(PhoneRegisterActivity.this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                            verify_btn.setVisibility(View.VISIBLE);
                            verify_progress.setVisibility(View.GONE);
                        }
                    }else{
                        if(edt_firstname.getText().toString().equals("")){
                            layoutFirstname.setError("Please enter display name");
                            Toast.makeText(PhoneRegisterActivity.this, "Please enter first name", Toast.LENGTH_SHORT).show();
                        }else{
                            layoutFirstname.setErrorEnabled(false);
                        }

                        if(edt_lastname.getText().toString().equals("")){
                            layoutLastname.setError("Please enter display name");
                            Toast.makeText(PhoneRegisterActivity.this, "Please enter last name", Toast.LENGTH_SHORT).show();
                        }else{
                            layoutLastname.setErrorEnabled(false);
                        }
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
                Toast.makeText(PhoneRegisterActivity.this, "Verify phone number complete!", Toast.LENGTH_SHORT).show();
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                if(e instanceof FirebaseAuthInvalidCredentialsException){
                    Toast.makeText(PhoneRegisterActivity.this, "Invalid credential: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }else if(e instanceof FirebaseTooManyRequestsException){
                    Toast.makeText(PhoneRegisterActivity.this, "SMS Quota exceeded", Toast.LENGTH_SHORT).show();
                }

                verify_progress.setVisibility(View.GONE);
                verify_btn.setVisibility(View.VISIBLE);
                layoutName.setVisibility(View.VISIBLE);
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

                layoutName.setVisibility(View.GONE);
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
                Toast.makeText(PhoneRegisterActivity.this, "Code has been sent, please check.", Toast.LENGTH_SHORT).show();
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

                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                            //register new user to database at "Users"
                            reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", firebaseUser.getUid());
                            hashMap.put("firstname", firstname.substring(0, 1).toUpperCase() + firstname.substring(1));
                            hashMap.put("lastname", lastname.substring(0, 1).toUpperCase() + lastname.substring(1));
                            hashMap.put("phone_number", phoneNumber);
                            hashMap.put("verify_status", "unverified");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(PhoneRegisterActivity.this, "Register successful!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(PhoneRegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            Toast.makeText(PhoneRegisterActivity.this, "Error: " + task.getException().toString(), Toast.LENGTH_SHORT).show();

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