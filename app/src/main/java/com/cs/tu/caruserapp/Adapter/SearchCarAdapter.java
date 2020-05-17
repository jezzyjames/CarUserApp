package com.cs.tu.caruserapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cs.tu.caruserapp.MessageActivity;
import com.cs.tu.caruserapp.Model.Car;
import com.cs.tu.caruserapp.R;

import java.util.List;

public class SearchCarAdapter extends RecyclerView.Adapter<SearchCarAdapter.ViewHolder> {
    View view;

    private int selectedPos = 0;

    private Context mContext;
    private List<Car> mCars;
    private String receiver_id;
    private String receiver_car_id;


    public SearchCarAdapter(Context mContext, List<Car> mCars, String receiver_id, String receiver_car_id) {
        this.mContext = mContext;
        this.mCars = mCars;
        this.receiver_id = receiver_id;
        this.receiver_car_id = receiver_car_id;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Store user_item layout into view
        view = LayoutInflater.from(mContext).inflate(R.layout.car_item, parent, false);

        //return "holder" that carrying layout "view" to onBindViewHolder
        return new SearchCarAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Car car = mCars.get(position);
        holder.itemView.setBackgroundColor(selectedPos == position ? Color.parseColor("#3F51B5") : Color.TRANSPARENT);

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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition() == RecyclerView.NO_POSITION) return;

                notifyItemChanged(selectedPos);
                selectedPos = holder.getAdapterPosition();
                notifyItemChanged(selectedPos);

                new AlertDialog.Builder(mContext)
                        .setMessage(mContext.getString(R.string.choose_car_id_number) + " " + car.getCar_id().toUpperCase() + mContext.getString(R.string.to_your_active_car))
                        .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(mContext, MessageActivity.class);
                                intent.putExtra("receiver_id", receiver_id);
                                intent.putExtra("receiver_car_id", receiver_car_id);
                                intent.putExtra("sender_car_id", car.getCar_id());
                                intent.putExtra("from_search", true);
                                mContext.startActivity(intent);

                            }
                        })
                        .setNegativeButton(mContext.getString(R.string.cancel), null)
                        .show();
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
        public ImageView car_image;

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