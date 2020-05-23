package com.cs.tu.caruserapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cs.tu.caruserapp.Dialog.ChooseCarDialog;
import com.cs.tu.caruserapp.Model.Car;
import com.cs.tu.caruserapp.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchActivity extends AppCompatActivity {
    EditText search_users;
    Spinner province_spinner;
    CardView cardview_result;
    ImageButton btn_clear_text;
    ImageButton btn_search;
    CircleImageView image_profile;
    TextView verify_status;
    TextView car_id;
    TextView province;
    TextView brand;
    TextView model;
    TextView color;
    Button btn_chat;
    TextView cant_chat;
    ProgressBar progressBar;

    Car found_car;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        cardview_result = findViewById(R.id.cardview_result);
        search_users = findViewById(R.id.search_users);
        province_spinner = findViewById(R.id.province_spinner);
        String[] province_array = getResources().getStringArray(R.array.province_arrays);
        final ArrayAdapter<String> province_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, province_array);
        province_spinner.setAdapter(province_adapter);

        btn_clear_text = findViewById(R.id.btn_clear_text);
        btn_search = findViewById(R.id.btn_search);
        image_profile = findViewById(R.id.profile_image);
        verify_status = findViewById(R.id.verify_status);
        car_id = findViewById(R.id.car_id);
        province = findViewById(R.id.province);
        brand = findViewById(R.id.car_brand);
        model = findViewById(R.id.car_model);
        color = findViewById(R.id.car_color);
        btn_chat = findViewById(R.id.btn_chat);
        cant_chat = findViewById(R.id.cant_chat);
        progressBar = findViewById(R.id.progressbar);

        cardview_result.setVisibility(View.INVISIBLE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.search));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0){
                    btn_clear_text.setVisibility(View.VISIBLE);
                    btn_clear_text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            search_users.setText("");
                        }
                    });


                }else{
                    btn_clear_text.setVisibility(View.INVISIBLE);
                }
            }
        });

        search_users.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    searchUsers(search_users.getText().toString().toLowerCase());
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE
                    );
                    imm.hideSoftInputFromWindow(search_users.getWindowToken(), 0);
                    return true;
                }
                return false;

            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!search_users.getText().toString().equals("") && province_spinner.getSelectedItemPosition() != 0){
                    searchUsers(search_users.getText().toString().toLowerCase());
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE
                    );
                    imm.hideSoftInputFromWindow(search_users.getWindowToken(), 0);
                }else{
                    if(search_users.getText().toString().equals("")){
                        Toast.makeText(SearchActivity.this, "Please insert car id", Toast.LENGTH_SHORT).show();
                    }else if(province_spinner.getSelectedItemPosition() == 0){
                        Toast.makeText(SearchActivity.this, "Please select province", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

    }

    private void searchUsers(String s) {
        progressBar.setVisibility(View.VISIBLE);
        found_car = new Car();
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Query query = FirebaseDatabase.getInstance().getReference("Cars").orderByChild("car_id").equalTo(s);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                found_car = null;
                //if dataSnapshot is not null
                if(dataSnapshot.exists()){

                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final Car car = snapshot.getValue(Car.class);
                        assert car != null;
                        assert firebaseUser != null;
                        if(car.getProvince().equals(province_spinner.getSelectedItem().toString())){
                            found_car = car;

                        }

                    }
                    if(found_car != null){
                        progressBar.setVisibility(View.GONE);

                        //check verify status
                        if(found_car.getVerify_status() == 2){
                            verify_status.setVisibility(View.GONE);
                        }else{
                            verify_status.setVisibility(View.VISIBLE);
                        }

                        car_id.setText(found_car.getCar_id().toUpperCase());
                        province.setText(found_car.getProvince());
                        brand.setText(found_car.getBrand());
                        model.setText(found_car.getModel());

                        String[] colors = getResources().getStringArray(R.array.color_arrays);
                        String car_color = colors[found_car.getColor()];
                        color.setText(car_color);

                        //set image
                        if (found_car.getImageURL().equals("default")) {
                            image_profile.setImageResource(R.drawable.ic_light_car);
                        } else {
                            Glide.with(getApplicationContext()).load(found_car.getImageURL()).into(image_profile);
                        }
                        cardview_result.setVisibility(View.VISIBLE);

                        //if search result is your own id
                        if (!found_car.getOwner_id().equals(firebaseUser.getUid())) {
                            btn_chat.setVisibility(View.VISIBLE);
                            cant_chat.setVisibility(View.INVISIBLE);
                            btn_chat.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("receiver_id", found_car.getOwner_id());
                                    bundle.putString("receiver_car_id", found_car.getCar_id());
                                    bundle.putString("receiver_car_province", found_car.getProvince());

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
                    }else{
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(SearchActivity.this, getString(R.string.car_id_not_found), Toast.LENGTH_SHORT).show();
                        cardview_result.setVisibility(View.INVISIBLE);
                    }


                }else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SearchActivity.this, getString(R.string.car_id_not_found), Toast.LENGTH_SHORT).show();
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