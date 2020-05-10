package com.cs.tu.caruserapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


import com.cs.tu.caruserapp.Fragments.ChatsFragment;
import com.cs.tu.caruserapp.Model.Car;
import com.cs.tu.caruserapp.Model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    ImageView user_icon;
    TextView username;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    private static final String TAG = "MainActivity";

    private List<Car> carsList;

    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    TabLayout tabLayout;

    ImageView info;
    Animation anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        info = findViewById(R.id.info);
        info.setVisibility(View.GONE);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                warnDialog(1);
            }
        });
        anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        user_icon = findViewById(R.id.user_icon);
        username = findViewById(R.id.username);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getFirstname());
                username.setTextColor(Color.WHITE);
                user_icon.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white));
                info.setVisibility(View.GONE);
                info.clearAnimation();


                if(!user.isVerify_status()){
                    username.setText(user.getFirstname() + " : unverified");
                    username.setTextColor(Color.RED);
                    user_icon.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.red));

                    info.setVisibility(View.VISIBLE);
                    info.startAnimation(anim);

                    warnDialog(1);

                }

                user_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    }
                });
                username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

                viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
                for(int i = 0; i < carsList.size(); i++){
                    String sender_car_id = carsList.get(i).getCar_id();
                    Bundle bundle = new Bundle();
                    bundle.putString("car_id", sender_car_id);
                    ChatsFragment chatsFragment = new ChatsFragment();
                    chatsFragment.setArguments(bundle);

                    //count unread message
//                    int unread = 0;
//                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                        Chat chat = snapshot.getValue(Chat.class);
//                        if(chat.getReceiver_car_id().equals(sender_car_id) && !chat.isIsseen()){
//                            unread++;
//                        }
//                    }

//                    String unreadnum = "";
//                    if(unread != 0){
//                        unreadnum = " (" + unread + ")";
//                    }
                    //add fragment each car id
                    viewPagerAdapter.addFragment(chatsFragment, sender_car_id);

                }
                if(carsList.size() != 0){
                    viewPager.setAdapter(viewPagerAdapter);
                }else{
                    warnDialog(0);
                }


//                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
//                reference.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        tabLayout.setupWithViewPager(viewPager);

    }

    //show menu on top
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //Signout if logout has been selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.profile:
                Intent profile_intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(profile_intent);
                return true;

            case R.id.logout:
                new AlertDialog.Builder(this)
                        .setTitle("Confirm")
                        .setMessage("Are you sure to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(MainActivity.this, StartActivity.class));
                                finish();

                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;

            case R.id.search_button:
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);

        }

        return false;
    }

    //Setup ViewPageAdapter properties
    class ViewPagerAdapter extends FragmentPagerAdapter{

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm){
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    public void warnDialog(int dialog){
        switch (dialog){
            case 0:
                new FancyGifDialog.Builder(MainActivity.this)
                        .setTitle("Add your first car")
                        .setMessage("To chat with other people, please add car in your profile.")
                        .setNegativeBtnText("Later")
                        .setPositiveBtnBackground("#4A46B5")
                        .setPositiveBtnText("Go")
                        .setNegativeBtnBackground("#FFA9A7A8")
                        .setGifResource(R.drawable.driving_gif)   //Pass your Gif here
                        .isCancellable(false)
                        .OnPositiveClicked(new FancyGifDialogListener() {
                            @Override
                            public void OnClick() {
                                Intent profile_intent = new Intent(MainActivity.this, ProfileActivity.class);
                                startActivity(profile_intent);
                            }
                        })
                        .OnNegativeClicked(new FancyGifDialogListener() {
                            @Override
                            public void OnClick() {
                            }
                        })
                        .build();
            case 1:
                new FancyGifDialog.Builder(MainActivity.this)
                        .setTitle("Your account is not verified")
                        .setMessage("For the safety of everyone please verify yourself. if you don't, you still can use the app but your unverified status will show to other")
                        .setNegativeBtnText("Later")
                        .setPositiveBtnBackground("#4A46B5")
                        .setPositiveBtnText("Go")
                        .setNegativeBtnBackground("#FFA9A7A8")
                        .setGifResource(R.drawable.thieft)   //Pass your Gif here
                        .isCancellable(false)
                        .OnPositiveClicked(new FancyGifDialogListener() {
                            @Override
                            public void OnClick() {
                                Intent profile_intent = new Intent(MainActivity.this, ProfileActivity.class);
                                startActivity(profile_intent);
                            }
                        })
                        .OnNegativeClicked(new FancyGifDialogListener() {
                            @Override
                            public void OnClick() {
                            }
                        })
                        .build();
        }


    }
}