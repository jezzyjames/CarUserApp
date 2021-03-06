package com.cs.tu.caruserapp.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cs.tu.caruserapp.MessageActivity;
import com.cs.tu.caruserapp.Model.Car;
import com.cs.tu.caruserapp.R;
import com.cs.tu.caruserapp.VerifyActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder> {
    View view;

    private Context mContext;
    private List<Car> mCars;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    Animation anim;

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

        String[] colors = mContext.getResources().getStringArray(R.array.color_arrays);
        String car_color = colors[car.getColor()];
        holder.txt_car_color.setText(car_color);

        if(car.getImageURL().equals("default")){
            holder.car_image.setImageResource(R.drawable.ic_light_car);
        }else{
            Glide.with(mContext).load(car.getImageURL()).into(holder.car_image);
        }

        if(car.getVerify_status() == 2){
            holder.car_verify_status.setText(mContext.getString(R.string.verified));
            holder.car_verify_status.setTextColor(ContextCompat.getColor(mContext, R.color.green));
            holder.verify_btn.setVisibility(View.GONE);
        } else if(car.getVerify_status() == 1){
            holder.car_verify_status.setText(mContext.getString(R.string.verifying));
            holder.car_verify_status.setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
            holder.verify_btn.setVisibility(View.GONE);

        } else if(car.getVerify_status() == 0){
            holder.car_verify_status.setText(mContext.getString(R.string.unverified));
            holder.car_verify_status.setTextColor(ContextCompat.getColor(mContext, R.color.red));
            anim = AnimationUtils.loadAnimation(mContext, R.anim.zoom_in_button);
            holder.verify_btn.setAnimation(anim);
            holder.verify_btn.setVisibility(View.VISIBLE);
            holder.verify_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(mContext, VerifyActivity.class);
                    intent.putExtra("my_car_id", car.getCar_id());
                    intent.putExtra("my_car_province", car.getProvince());
                    mContext.startActivity(intent);
                }
            });
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(car.getCar_id().toUpperCase() + " " + car.getProvince());

                String[] choices = {mContext.getString(R.string.delete_this_car)};
                builder.setItems(choices, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                new android.app.AlertDialog.Builder(mContext)
                                        .setTitle(mContext.getString(R.string.confirm))
                                        .setMessage(mContext.getString(R.string.are_you_sure_to_delete) + "\n" + car.getCar_id().toUpperCase() + " " + car.getProvince())
                                        .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                                reference = FirebaseDatabase.getInstance().getReference("Cars")
                                                        .child(car.getCar_id() + "_" + car.getProvince());
                                                reference.setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        listAllStorageFilesAndDelete(car.getCar_id(), car.getProvince());
                                                    }
                                                });

                                            }
                                        })
                                        .setNegativeButton(mContext.getString(R.string.cancel), null)
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

    public void listAllStorageFilesAndDelete(final String car_id, final String province) {
        final String car_photo_path = "uploads/car_photo/" + firebaseUser.getUid() + "/" + car_id + "_" + province;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference listRef = storage.getReference(car_photo_path);

        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference();
                        String filenameToDelete = "";
                        for (StorageReference prefix : listResult.getPrefixes()) {
                            // All the prefixes under listRef.
                            // You may call listAll() recursively on them.'
                        }

                        for (StorageReference item : listResult.getItems()) {
                            filenameToDelete = item.getName();
                        }
                        StorageReference desertRef = storageRef.child(car_photo_path + "/" + filenameToDelete);
                        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(mContext, mContext.getString(R.string.delete_car_success), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(mContext, mContext.getString(R.string.fail_to_delete_car_photo), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
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
        public TextView car_verify_status;
        public Button verify_btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_car_id = itemView.findViewById(R.id.car_id);
            txt_province = itemView.findViewById(R.id.province);
            txt_car_brand = itemView.findViewById(R.id.brand);
            txt_car_model = itemView.findViewById(R.id.model);
            txt_car_color = itemView.findViewById(R.id.color);
            car_image = itemView.findViewById(R.id.car_image);
            car_verify_status = itemView.findViewById(R.id.car_verify_status);
            verify_btn = itemView.findViewById(R.id.verify_btn);

        }
    }

}