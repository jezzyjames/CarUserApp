package com.cs.tu.caruserapp.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cs.tu.caruserapp.Model.Car;
import com.cs.tu.caruserapp.R;

import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder> {
    View view;

    private int selectedPos = 0;

    private Context mContext;
    private List<Car> mCars;

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
        holder.itemView.setBackgroundColor(selectedPos == position ? Color.parseColor("#3F51B5") : Color.TRANSPARENT);
        holder.is_use.setVisibility(selectedPos == position ? View.VISIBLE : View.GONE);

        holder.txt_car_id.setText(car.getCar_id());
        holder.txt_province.setText(car.getProvince());
        holder.txt_car_brand.setText(car.getBrand());
        holder.txt_car_model.setText(car.getModel());
        holder.txt_car_color.setText(car.getColor());

        if(car.getImageURL().equals("default")){
            holder.car_image.setImageResource(R.mipmap.ic_launcher);
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
        public TextView is_use;
        public RelativeLayout layout_border;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_car_id = itemView.findViewById(R.id.car_id);
            txt_province = itemView.findViewById(R.id.province);
            txt_car_brand = itemView.findViewById(R.id.brand);
            txt_car_model = itemView.findViewById(R.id.model);
            txt_car_color = itemView.findViewById(R.id.color);
            car_image = itemView.findViewById(R.id.car_image);
            is_use = itemView.findViewById(R.id.is_use);
            layout_border = itemView.findViewById(R.id.layout_border);

        }
    }

}