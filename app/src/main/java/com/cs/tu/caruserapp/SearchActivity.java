package com.cs.tu.caruserapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cs.tu.caruserapp.Model.User;
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
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search_name").equalTo(s);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //if dataSnapshot is not null
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final User user = snapshot.getValue(User.class);
                        assert user != null;
                        assert firebaseUser != null;

                        txt_username.setText(user.getUsername());
                        if(user.getImageURL().equals("default")){
                            image_profile.setImageResource(R.mipmap.ic_launcher);
                        }else{
                            Glide.with(getApplicationContext()).load(user.getImageURL()).into(image_profile);
                        }
                        cardview_result.setVisibility(View.VISIBLE);

                        //if search result is your own id
                        if (!user.getId().equals(firebaseUser.getUid())) {
                            btn_chat.setVisibility(View.VISIBLE);
                            cant_chat.setVisibility(View.INVISIBLE);
                            btn_chat.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                                    intent.putExtra("receiver_id", user.getId());
                                    startActivity(intent);

                                }
                            });

                        }else{
                            btn_chat.setVisibility(View.INVISIBLE);
                            cant_chat.setVisibility(View.VISIBLE);
                        }
                }
                }else{
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