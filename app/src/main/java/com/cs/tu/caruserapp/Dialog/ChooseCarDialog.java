package com.cs.tu.caruserapp.Dialog;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.cs.tu.caruserapp.Adapter.CarAdapter;
import com.cs.tu.caruserapp.Adapter.SearchCarAdapter;
import com.cs.tu.caruserapp.Model.Car;
import com.cs.tu.caruserapp.ProfileActivity;
import com.cs.tu.caruserapp.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class ChooseCarDialog extends DialogFragment {

    //interface for send input to ProfileActivity
    public interface OnInputListener{
        void sendInput(String input_carid, String input_province, String input_brand, String input_model, String input_color);
    }
    public OnInputListener mOnInputListener;

    private RecyclerView recyclerView;
    private List<Car> carsList;
    private SearchCarAdapter searchCarAdapter;

    FirebaseUser firebaseUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_car_dialog, container,false);

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

                Bundle bundle = getArguments();
                String receiver_id = bundle.getString("receiver_id", "");
                String receiver_car_id = bundle.getString("receiver_car_id", "");

                searchCarAdapter = new SearchCarAdapter(getContext(), carsList, receiver_id, receiver_car_id);
                recyclerView.setAdapter(searchCarAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            mOnInputListener = (OnInputListener) getActivity();
        }catch (ClassCastException e){
            Log.e("", "onAttach: ClassCastException: " + e.getMessage());
        }
    }
}