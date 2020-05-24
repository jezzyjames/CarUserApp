package com.cs.tu.caruserapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cs.tu.caruserapp.Model.Notification;
import com.cs.tu.caruserapp.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    View view;

    private Context mContext;
    private List<Notification> mNoti;

    public NotificationAdapter(Context mContext, List<Notification> mNoti) {
        this.mContext = mContext;
        this.mNoti = mNoti;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Store user_item layout into view
        view = LayoutInflater.from(mContext).inflate(R.layout.noti_item, parent, false);

        //return "holder" that carrying layout "view" to onBindViewHolder
        return new NotificationAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Notification noti = mNoti.get(position);

        holder.noti_message.setText(noti.getMessage());
        holder.noti_time.setText(noti.getDate() + " " + noti.getTime());

        if(!noti.getImageURL().equals("default")){
            Glide.with(mContext).load(noti.getImageURL()).into(holder.circle_image);
        }

    }


    @Override
    public int getItemCount() {
        return mNoti.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public CircleImageView circle_image;
        public TextView noti_message;
        public TextView noti_time;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            circle_image = itemView.findViewById(R.id.circle_image);
            noti_message = itemView.findViewById(R.id.noti_message);
            noti_time = itemView.findViewById(R.id.noti_time);

        }
    }

}