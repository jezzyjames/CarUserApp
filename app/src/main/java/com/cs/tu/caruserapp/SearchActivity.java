package com.cs.tu.caruserapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cs.tu.caruserapp.Dialog.ChooseCarDialog;
import com.cs.tu.caruserapp.Model.Car;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import de.hdodenhof.circleimageview.CircleImageView;

public class SearchActivity extends AppCompatActivity {
    EditText search_users;
    CardView cardview_result;
    ImageButton btn_search;
    CircleImageView image_profile;
    TextView txt_username;
    Button btn_chat;
    TextView cant_chat;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        cardview_result = findViewById(R.id.cardview_result);
        search_users = findViewById(R.id.search_users);
        btn_search = findViewById(R.id.btn_search);
        image_profile = findViewById(R.id.profile_image);
        txt_username = findViewById(R.id.txt_username);
        btn_chat = findViewById(R.id.btn_chat);
        cant_chat = findViewById(R.id.cant_chat);
        progressBar = findViewById(R.id.progressbar);

        cardview_result.setVisibility(View.INVISIBLE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        search_users.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    searchUsers(search_users.getText().toString().toLowerCase());
                    return true;
                }
                return false;
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchUsers(search_users.getText().toString().toLowerCase());
            }
        });

    }

    private void searchUsers(String s) {
        progressBar.setVisibility(View.VISIBLE);

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Cars").orderByChild("car_id").equalTo(s);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //if dataSnapshot is not null
                if(dataSnapshot.exists()){
                    progressBar.setVisibility(View.GONE);
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final Car car = snapshot.getValue(Car.class);
                        assert car != null;
                        assert firebaseUser != null;

                        txt_username.setText(car.getCar_id());
                        if (car.getImageURL().equals("default")) {
                            image_profile.setImageResource(R.mipmap.ic_launcher);
                        } else {
                            Glide.with(getApplicationContext()).load(car.getImageURL()).into(image_profile);
                        }
                        cardview_result.setVisibility(View.VISIBLE);

                        //if search result is your own id
                        if (!car.getOwner_id().equals(firebaseUser.getUid())) {
                            btn_chat.setVisibility(View.VISIBLE);
                            cant_chat.setVisibility(View.INVISIBLE);
                            btn_chat.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("receiver_id", car.getOwner_id());
                                    bundle.putString("receiver_car_id", car.getCar_id());

                                    DialogFragment dialogFragment = new ChooseCarDialog();
                                    dialogFragment.setArguments(bundle);
                                    dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogFragmentTheme);
                                    dialogFragment.show(getSupportFragmentManager(), "choosecar");

                                }
                            });

                        } else {
                            btn_chat.setVisibility(View.INVISIBLE);
                            cant_chat.setVisibility(View.VISIBLE);
                        }

                    }

                }else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SearchActivity.this, "ID not found", Toast.LENGTH_SHORT).show();
                    cardview_result.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("", databaseError.getMessage());
            }
        });

    }

}