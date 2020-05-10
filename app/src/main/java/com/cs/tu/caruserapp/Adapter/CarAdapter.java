package com.cs.tu.caruserapp.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cs.tu.caruserapp.Model.Car;
import com.cs.tu.caruserapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder> {
    View view;

    private Context mContext;
    private List<Car> mCars;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    public CarAdapter(Context mContext, List<Car> mCars) {
        this.mContext = mContext;
        this.mCars = mCars;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Store user_item layout into view
        view = LayoutInflater.from(mContext).inflate(R.layout.car_item, parent, false);

        //return "holder" that carrying layout "view" to onBindViewHolder
        return new CarAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Car car = mCars.get(position);

        holder.txt_car_id.setText(car.getCar_id().toUpperCase());
        holder.txt_province.setText(car.getProvince());
        holder.txt_car_brand.setText(car.getBrand());
        holder.txt_car_model.setText(car.getModel());
        holder.txt_car_color.setText(car.getColor());

        if(car.getImageURL().equals("default")){
            holder.car_image.setImageResource(R.drawable.ic_light_car);
        }else{
            Glide.with(mContext).load(car.getImageURL()).into(holder.car_image);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(car.getCar_id().toUpperCase());

                String[] choices = {"Delete this car"};
                builder.setItems(choices, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                new android.app.AlertDialog.Builder(mContext)
                                        .setTitle("Confirm")
                                        .setMessage("Are you sure to delete " + car.getCar_id().toUpperCase())
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                                reference = FirebaseDatabase.getInstance().getReference("Cars")
                                                        .child(car.getCar_id());
                                                reference.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(mContext, "Delete car complete", Toast.LENGTH_SHORT).show();
                                                    }

                                                });

                                            }
                                        })
                                        .setNegativeButton("No", null)
                                        .show();
                                break;

                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mCars.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txt_car_id;
        public TextView txt_province;
        public TextView txt_car_brand;
        public TextView txt_car_model;
        public TextView txt_car_color;
        public CircleImageView car_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_car_id = itemView.findViewById(R.id.car_id);
            txt_province = itemView.findViewById(R.id.province);
            txt_car_brand = itemView.findViewById(R.id.brand);
            txt_car_model = itemView.findViewById(R.id.model);
            txt_car_color = itemView.findViewById(R.id.color);
            car_image = itemView.findViewById(R.id.car_image);

        }
    }

}