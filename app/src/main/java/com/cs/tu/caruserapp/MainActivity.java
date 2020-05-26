package com.cs.tu.caruserapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.cs.tu.caruserapp.Dialog.NotificationDialog;
import com.cs.tu.caruserapp.Fragments.ChatsFragment;
import com.cs.tu.caruserapp.Model.Car;
import com.cs.tu.caruserapp.Model.Notification;
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
    TextView verify_status;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    TextView noti_show_count;

    private static final String TAG = "MainActivity";

    private List<Car> carsList;

    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    TabLayout tabLayout;

    boolean warn_dialog_addcar = false;
    boolean warn_dialog_verify = false;

    DialogFragment dialogFragment;

    Intent intent;
    boolean dont_show_dialog;
    boolean open_noti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        user_icon = findViewById(R.id.user_icon);
        username = findViewById(R.id.username);
        verify_status = findViewById(R.id.verify_status);

        intent = getIntent();
        dont_show_dialog = intent.getBooleanExtra("dont_show_dialog", false);
        open_noti = intent.getBooleanExtra("open_noti", false);
        if(dont_show_dialog){
            warn_dialog_addcar = true;
            warn_dialog_verify = true;
            dont_show_dialog = false;
        }
        if(open_noti){
            showNotiDialog();
            open_noti = false;
        }

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    User user = dataSnapshot.getValue(User.class);
                    username.setText(user.getFirstname() + " " + user.getLastname());

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

                }else{
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this, StartActivity.class));
                    finish();
                }

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

                    if(car.getVerify_status() == 0 && !warn_dialog_verify){
                        warnDialog(1);
                    }
                }

                viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
                for(int i = 0; i < carsList.size(); i++){
                    String sender_car_id = carsList.get(i).getCar_id();
                    String province = carsList.get(i).getProvince();
                    Bundle bundle = new Bundle();
                    bundle.putString("car_id", sender_car_id);
                    bundle.putString("province", province);
                    ChatsFragment chatsFragment = new ChatsFragment();
                    chatsFragment.setArguments(bundle);

                    viewPagerAdapter.addFragment(chatsFragment, sender_car_id + "\n" + province);

                }
                if(carsList.size() != 0){
                    viewPager.setAdapter(viewPagerAdapter);
                }else{
                    if(!warn_dialog_addcar){
                        warnDialog(0);
                    }
                }

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
        final MenuItem menuItem = menu.findItem(R.id.notification);
        View actionView = menuItem.getActionView();
        noti_show_count = actionView.findViewById(R.id.noti_count_text);
        setupBadge();
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

//        menu.findItem(R.id.notification).setVisible(false);

        return true;
    }

    private void setupBadge(){
        reference = FirebaseDatabase.getInstance().getReference("Notification").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int noti_count = 0;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Notification noti = snapshot.getValue(Notification.class);
                    if(!noti.isIsseen()){
                        noti_count++;
                    }
                }

                if(noti_count == 0){
                    noti_show_count.setVisibility(View.GONE);
                }else{
                    noti_show_count.setText(""+noti_count);
                    noti_show_count.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
                        .setTitle(getString(R.string.confirm))
                        .setMessage(getString(R.string.logout_message))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(MainActivity.this, StartActivity.class));
                                finish();

                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), null)
                        .show();
                return true;

            case R.id.search_button:
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
                return true;

            case R.id.notification:
                showNotiDialog();
                return true;
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
                warn_dialog_addcar = true;
                new FancyGifDialog.Builder(this)
                        .setTitle(getString(R.string.add_first_car_dialog_title))
                        .setMessage(getString(R.string.add_first_car_dialog))
                        .setNegativeBtnText(getString(R.string.later))
                        .setPositiveBtnBackground("#4A46B5")
                        .setPositiveBtnText(getString(R.string.go))
                        .setNegativeBtnBackground("#FFA9A7A8")
                        .setGifResource(R.drawable.driving_gif)   //Pass your Gif here
                        .isCancellable(true)
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
                break;
            case 1:
                warn_dialog_verify = true;
                new FancyGifDialog.Builder(this)
                        .setTitle(getString(R.string.please_verify_dialog_title))
                        .setMessage(getString(R.string.please_verify_dialog))
                        .setNegativeBtnText(getString(R.string.later))
                        .setPositiveBtnBackground("#4A46B5")
                        .setPositiveBtnText(getString(R.string.go))
                        .setNegativeBtnBackground("#FFA9A7A8")
                        .setGifResource(R.drawable.thieft)   //Pass your Gif here
                        .isCancellable(true)
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
                break;
        }
    }

    private void showNotiDialog(){
        dialogFragment = new NotificationDialog();
        dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
        dialogFragment.show(getSupportFragmentManager(), "Noti");

    }
}