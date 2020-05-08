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


import com.cs.tu.caruserapp.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class AddCarDialog extends DialogFragment {

    //interface for send input to ProfileActivity
    public interface OnInputListener{
        void sendInput(String input_carid, String input_province, String input_brand, String input_model, String input_color);
    }
    public OnInputListener mOnInputListener;

    TextView btn_cancel;
    TextView btn_add;
    Button btn_add_car_photo;
    EditText edt_carid;
    AutoCompleteTextView edt_province;
    EditText edt_brand;
    EditText edt_model;
    Spinner color_spinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_car_dialog, container,false);

        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_add = view.findViewById(R.id.btn_add);
        edt_carid = view.findViewById(R.id.edt_car_id);
        btn_add_car_photo = view.findViewById(R.id.btn_add_car_photo);

        edt_province = view.findViewById(R.id.edt_province);
        String[] province = getResources().getStringArray(R.array.province_arrays);
        ArrayAdapter<String> province_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, province);
        edt_province.setAdapter(province_adapter);

        edt_brand = view.findViewById(R.id.edt_brand);
        edt_model = view.findViewById(R.id.edt_model);

        color_spinner = view.findViewById(R.id.color_spinner);
        ArrayAdapter<CharSequence> color_adapter = ArrayAdapter.createFromResource(getContext(), R.array.color_arrays, android.R.layout.simple_spinner_item);
        color_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        color_spinner.setAdapter(color_adapter);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String input_carid = edt_carid.getText().toString();
                final String input_province = edt_province.getText().toString();
                final String input_brand = edt_brand.getText().toString();
                final String input_model = edt_model.getText().toString();
                final String input_color = color_spinner.getSelectedItem().toString();

                if(!input_carid.equals("") && !input_province.equals("") && !input_brand.equals("") && !input_model.equals("") && !input_color.equals("")) {

                    Query query = FirebaseDatabase.getInstance().getReference("Cars").orderByChild("car_id").equalTo(input_carid);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.exists()){
                                mOnInputListener.sendInput(input_carid, input_province, input_brand, input_model, input_color);
                                getDialog().dismiss();

                            }else{
                                Toast.makeText(getActivity(), "Can't add this car, this car is already registered.", Toast.LENGTH_SHORT).show();

                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("", databaseError.getMessage());
                        }
                    });


                }else{
                    Toast.makeText(getActivity(), "Please fill all blanks!", Toast.LENGTH_SHORT).show();
                }

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