package com.cs.tu.caruserapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cs.tu.caruserapp.Adapter.UserAdapter;
import com.cs.tu.caruserapp.Model.Car;
import com.cs.tu.caruserapp.Model.Chatlist;
import com.cs.tu.caruserapp.Notification.Token;
import com.cs.tu.caruserapp.ProfileActivity;
import com.cs.tu.caruserapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;


import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {
    private RelativeLayout warn_verify_layout;
    private RecyclerView recyclerView;

    ImageView info;
    Animation anim;

    private UserAdapter userAdapter;
    private List<Car> mCars;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    private List<Chatlist> usersList;

    private static final String TAG = "ChatFragment";

    Bundle bundle;
    String car_id;
    String province;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        info = view.findViewById(R.id.info);
        anim = AnimationUtils.loadAnimation(getActivity(), R.anim.zoom_in);
        info.setAnimation(anim);

        warn_verify_layout = view.findViewById(R.id.warn_verify_layout);
        warn_verify_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                warnDialog();
            }
        });

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //get current user auth state
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        bundle = getArguments();
        car_id = bundle.getString("car_id", "");
        province = bundle.getString("province", "");

        usersList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid()).child(car_id + "_" + province);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chatlist chatlist = snapshot.getValue(Chatlist.class);
                    //add all users that you are chatting with from Chatlist(Database) in userList
                    usersList.add(chatlist);

                }
                chatList();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateToken();
        return view;
    }

    private void chatList() {
        mCars = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Cars");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mCars.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Car car = snapshot.getValue(Car.class);

                    //just check your car verify status
                    if(car.getCar_id().equals(car_id) && car.getProvince().equals(province)){
                        if(car.getVerify_status() == 2){
                            warn_verify_layout.setVisibility(View.GONE);
                        }else{
                            warn_verify_layout.setVisibility(View.VISIBLE);
                        }
                    }

                    for(Chatlist chatlist : usersList){
                        //if users id in database equal to users id in userList (users that you are chatting with)
                        if(car.getCar_id().equals(chatlist.getReceiver_car_id()) && car.getProvince().equals(chatlist.getReceiver_car_province())){
                            //add that user to mUsers to show on Chat fragment page view
                            mCars.add(car);
                        }
                    }
                }
                userAdapter = new UserAdapter(getContext(), mCars, usersList);
                recyclerView.setAdapter(userAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void updateToken(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
                        Token tokenl = new Token(token);
                        reference.child(firebaseUser.getUid()).setValue(tokenl);

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                    }
                });
    }

    public void warnDialog(){
        new FancyGifDialog.Builder(getActivity())
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
                        Intent profile_intent = new Intent(getActivity(), ProfileActivity.class);
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