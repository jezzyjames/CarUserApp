package com.cs.tu.caruserapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cs.tu.caruserapp.Adapter.CarAdapter;
import com.cs.tu.caruserapp.Adapter.UserAdapter;
import com.cs.tu.caruserapp.Dialog.AddCarDialog;
import com.cs.tu.caruserapp.Model.Car;
import com.cs.tu.caruserapp.Model.Chatlist;
import com.cs.tu.caruserapp.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements AddCarDialog.OnInputListener {
    //Receive input from AddCarDialog
    @Override
    public void sendInput(String input_carid, String input_province, String input_brand, String input_model, String input_color) {
        addCar(input_carid, input_province, input_brand, input_model, input_color);
    }

    private RecyclerView recyclerView;

    private CarAdapter carAdapter;
    private List<Car> mCars;
    private List<Car> carsList;

    CircleImageView image_profile;
    TextView txt_add_car;

    DatabaseReference reference;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        image_profile = findViewById(R.id.profile_image);
        txt_add_car = findViewById(R.id.txt_add_car);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user.getImageURL().equals("default")){
                    image_profile.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(image_profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("", databaseError.getMessage());
            }
        });

        //get your owner cars from database and show on recyclerView
        carsList = new ArrayList<>();

        Query query = FirebaseDatabase.getInstance().getReference("Cars").orderByChild("owner_id").equalTo(firebaseUser.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                carsList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Car car = snapshot.getValue(Car.class);
                    carsList.add(car);
                }
                carAdapter = new CarAdapter(getApplicationContext(), carsList);
                recyclerView.setAdapter(carAdapter);

                //can't add more than 3 cars
                if(carsList.size() >= 3){
                    txt_add_car.setVisibility(View.GONE);
                }else{
                    txt_add_car.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DialogFragment dialogFragment = new AddCarDialog();
                            dialogFragment.show(getSupportFragmentManager(), "addcar");

                        }
                    });
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

//
//    private void showCarList() {
//        mCars = new ArrayList<>();
//        reference = FirebaseDatabase.getInstance().getReference("Cars");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                mCars.clear();
//                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    User user = snapshot.getValue(User.class);
//                    for(Carlist carlist : carsList){
//                        //if users id in database equal to users id in userList (users that you are chatting with)
//                        if(user.getId().equals(chatlist.getId())){
//                            //add that user to mUsers to show on Chat fragment page view
//                            mCars.add(user);
//                        }
//                    }
//                }
//                carAdapter = new CarAdapter(getContext(), mCars);
//                recyclerView.setAdapter(carAdapter);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }

    private void addCar(final String car_id, final String province, final String car_brand, final String car_model, final String car_color){

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Cars").child(car_id);

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("car_id", car_id);
        hashMap.put("province", province);
        hashMap.put("brand", car_brand);
        hashMap.put("model", car_model);
        hashMap.put("color", car_color);
        hashMap.put("imageURL", "default");
        hashMap.put("owner_id", firebaseUser.getUid());

        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ProfileActivity.this, "Added car complete!", Toast.LENGTH_SHORT).show();

            }
        });

    }

}