package com.cs.tu.caruserapp.Dialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.cs.tu.caruserapp.Adapter.SearchCarAdapter;
import com.cs.tu.caruserapp.Model.Car;
import com.cs.tu.caruserapp.ProfileActivity;
import com.cs.tu.caruserapp.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;



import java.util.ArrayList;
import java.util.List;


public class ChooseCarDialog extends DialogFragment {
    private RecyclerView recyclerView;
    private List<Car> carsList;
    private SearchCarAdapter searchCarAdapter;
    private TextView txt_addcar;
    private Button btn_addcar;

    FirebaseUser firebaseUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_car_dialog, container,false);

        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        txt_addcar = view.findViewById(R.id.txt_add_car);
        btn_addcar = view.findViewById(R.id.btn_addcar);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //***get your owner cars from database and show on recyclerView***
        carsList = new ArrayList<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Query query = FirebaseDatabase.getInstance().getReference("Cars").orderByChild("owner_id").equalTo(firebaseUser.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                carsList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Car car = snapshot.getValue(Car.class);
                    carsList.add(car);
                }

                if(carsList.size() == 0){
                    recyclerView.setVisibility(View.GONE);
                    txt_addcar.setVisibility(View.VISIBLE);
                    btn_addcar.setVisibility(View.VISIBLE);
                    btn_addcar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent profile_intent = new Intent(getActivity(), ProfileActivity.class);
                            startActivity(profile_intent);
                        }
                    });
                }else{
                    txt_addcar.setVisibility(View.GONE);
                    btn_addcar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    Bundle bundle = getArguments();
                    String receiver_id = bundle.getString("receiver_id", "");
                    String receiver_car_id = bundle.getString("receiver_car_id", "");
                    String receiver_car_province = bundle.getString("receiver_car_province", "");

                    searchCarAdapter = new SearchCarAdapter(getContext(), carsList, receiver_id, receiver_car_id, receiver_car_province);
                    recyclerView.setAdapter(searchCarAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

}