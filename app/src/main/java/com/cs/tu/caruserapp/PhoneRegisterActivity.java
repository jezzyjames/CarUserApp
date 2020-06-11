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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    String raw_phoneNumber = "";
    LinearLayout phone_view_part;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    FirebaseAuth firebaseAuth;
    DatabaseReference reference;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;

    boolean need_regist = false;

    private static final String TAG = "PhoneRegister";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.login));

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
                if(verify_btn.getText().toString().equalsIgnoreCase(getString(R.string.verify)) || code_sent){
                    String verificationCode = editText_code.getText().toString();

                    if(verificationCode.equals("")){
                        Toast.makeText(PhoneRegisterActivity.this, getString(R.string.please_enter_code), Toast.LENGTH_SHORT).show();
                        layoutVerifyCode.setError(getString(R.string.please_enter_code));
                    }else{
                        verify_btn.setVisibility(View.GONE);
                        verify_progress.setVisibility(View.VISIBLE);
                        layoutVerifyCode.setErrorEnabled(false);

                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                        signInWithPhoneAuthCredential(credential);
                    }

                //*******regist new user*******
                }else if(verify_btn.getText().toString().equalsIgnoreCase(getString(R.string.submit)) || need_regist){
                    layoutFirstname.setVisibility(View.VISIBLE);
                    layoutLastname.setVisibility(View.VISIBLE);
                    if(!edt_firstname.getText().toString().equals("") && !edt_lastname.getText().toString().equals("")) {
                        layoutFirstname.setErrorEnabled(false);
                        layoutLastname.setErrorEnabled(false);
                        firstname = edt_firstname.getText().toString();
                        lastname = edt_lastname.getText().toString();

                        registDatabase();
                    }else{
                        if(edt_firstname.getText().toString().equals("")){
                            layoutFirstname.setError(getString(R.string.please_enter_firstname));
                            Toast.makeText(PhoneRegisterActivity.this, getString(R.string.please_enter_firstname), Toast.LENGTH_SHORT).show();
                        }else{
                            layoutFirstname.setErrorEnabled(false);
                        }

                        if(edt_lastname.getText().toString().equals("")){
                            layoutLastname.setError(getString(R.string.please_enter_lastname));
                            Toast.makeText(PhoneRegisterActivity.this, getString(R.string.please_enter_lastname), Toast.LENGTH_SHORT).show();
                        }else{
                            layoutLastname.setErrorEnabled(false);
                        }

                    }

                //*******verify phone number*******
                } else{
                        if (!editText_phone.getText().toString().equals("")) {
                            //admin test//
                            if(editText_phone.getText().toString().equals("121231212")) {
                                FirebaseAuth auth = firebaseAuth.getInstance();
                                auth.signInWithEmailAndPassword("jetdokoalah@gmail.com", "1212312121").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            Intent intent = new Intent(PhoneRegisterActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }else{
                                            Toast.makeText(PhoneRegisterActivity.this, "Admin Login Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            if(editText_phone.getText().toString().equals("12345678")) {
                                FirebaseAuth auth = firebaseAuth.getInstance();
                                auth.signInWithEmailAndPassword("jetzjamez@hotmail.com", "1212312121").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            Intent intent = new Intent(PhoneRegisterActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }else{
                                            Toast.makeText(PhoneRegisterActivity.this, "Admin Login Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            //admin Test//

                            verify_btn.setVisibility(View.GONE);
                            verify_progress.setVisibility(View.VISIBLE);
                            layoutPhone.setErrorEnabled(false);
                            raw_phoneNumber = ccp.getFormattedFullNumber();
                            phoneNumber = ccp.getFormattedFullNumber();

                            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                    phoneNumber,                                          //phone number to verify
                                    60,                                                //timeout duration
                                    TimeUnit.SECONDS,                                     //unit of timeout
                                    PhoneRegisterActivity.this,                   //activity for callback
                                    mCallbacks);                                          //onVerificationStateChangedCallbacks

                        } else {
                            layoutPhone.setError(getString(R.string.please_enter_valid_number));
                            Toast.makeText(PhoneRegisterActivity.this, getString(R.string.please_enter_valid_number), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(PhoneRegisterActivity.this, getString(R.string.verify_number_complete), Toast.LENGTH_SHORT).show();
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                if(e instanceof FirebaseAuthInvalidCredentialsException){
                    Toast.makeText(PhoneRegisterActivity.this, getString(R.string.invalid_credential) + ": " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }else if(e instanceof FirebaseTooManyRequestsException){
                    Toast.makeText(PhoneRegisterActivity.this, getString(R.string.sms_error), Toast.LENGTH_SHORT).show();
                }

                verify_progress.setVisibility(View.GONE);
                verify_btn.setVisibility(View.VISIBLE);
                phone_view_part.setVisibility(View.VISIBLE);

                verify_btn.setText(getString(R.string.continue_txt));
                code_sent = false;
                phone_refer.setVisibility(View.GONE);
                layoutVerifyCode.setVisibility(View.GONE);
                resend_code.setVisibility(View.GONE);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                mVerificationId = s;
                mResendToken = forceResendingToken;

                phone_view_part.setVisibility(View.GONE);
                code_sent = true;

                phone_refer.setVisibility(View.VISIBLE);
                phone_refer.setText(getString(R.string.code_was_sent) + hiddenPhone(raw_phoneNumber));

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

                verify_btn.setText(getString(R.string.verify));
                verify_btn.setVisibility(View.VISIBLE);
                verify_progress.setVisibility(View.GONE);

                Toast.makeText(PhoneRegisterActivity.this, getString(R.string.code_has_been_sent), Toast.LENGTH_SHORT).show();
            }
        };

    }

    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            code_sent = false;
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        need_regist = false;
                                        login();

                                    }else{
                                        need_regist = true;
                                        phone_view_part.setVisibility(View.GONE);
                                        verify_progress.setVisibility(View.GONE);

                                        layoutVerifyCode.setVisibility(View.GONE);
                                        resend_code.setVisibility(View.GONE);
                                        phone_refer.setVisibility(View.GONE);

                                        layoutName.setVisibility(View.VISIBLE);
                                        verify_btn.setVisibility(View.VISIBLE);
                                        verify_btn.setText(getString(R.string.submit));

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            Toast.makeText(PhoneRegisterActivity.this, getString(R.string.error) + ": " + task.getException().toString(), Toast.LENGTH_SHORT).show();

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
        verify_btn.setVisibility(View.GONE);
        verify_progress.setVisibility(View.VISIBLE);
        layoutPhone.setErrorEnabled(false);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                mResendToken);             // ForceResendingToken from callbacks
    }

    private void registDatabase(){
        verify_btn.setVisibility(View.GONE);
        verify_progress.setVisibility(View.VISIBLE);

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //register new user to database at "Users"
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", firebaseUser.getUid());
        hashMap.put("firstname", firstname.substring(0, 1).toUpperCase() + firstname.substring(1));
        hashMap.put("lastname", lastname.substring(0, 1).toUpperCase() + lastname.substring(1));
        hashMap.put("phone_number", raw_phoneNumber);
        hashMap.put("role", "user");
        reference.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //regist phone number
                DatabaseReference phone_ref = FirebaseDatabase.getInstance().getReference("Phone").child(firebaseUser.getUid());
                HashMap<String, Object> PhoneHashMap = new HashMap<>();
                PhoneHashMap.put("phone_number", raw_phoneNumber);
                phone_ref.setValue(PhoneHashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        login();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        DatabaseFailure();
                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                DatabaseFailure();
            }
        });
    }

    private void DatabaseFailure(){
        need_regist = false;
        verify_btn.setVisibility(View.VISIBLE);
        verify_progress.setVisibility(View.GONE);
        Toast.makeText(PhoneRegisterActivity.this, getString(R.string.regist_failed), Toast.LENGTH_SHORT).show();
    }

    private void login(){
        Toast.makeText(PhoneRegisterActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(PhoneRegisterActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private String hiddenPhone(String phone_num){
        phone_num.replaceAll("-"," ");
        String phoneArr[] = phone_num.split(" ");
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

        return hidden_number;
    }

}