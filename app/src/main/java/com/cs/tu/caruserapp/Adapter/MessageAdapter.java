package com.cs.tu.caruserapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cs.tu.caruserapp.Model.Chat;
import com.cs.tu.caruserapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChat;
    private String imageurl;

    FirebaseUser firebaseUser;

    public MessageAdapter(Context mContext, List<Chat> mChat, String imageurl){
        this.mContext = mContext;
        this.mChat = mChat;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Chat chat = mChat.get(position);

        //show date
        if(position != 0 && chat.getDate().equals(mChat.get(position-1).getDate())){
            holder.show_time.setVisibility(View.GONE);
        }else{
            Date date = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
            String formattedDate = df.format(date);
            String currentDateArr[] = formattedDate.split(" ");
            String currentYear = currentDateArr[2];

            String dateArr[] = chat.getDate().split(" ");
            String day = dateArr[0];
            String month = dateArr[1];
            String year = dateArr[2];

            if(chat.getDate().equalsIgnoreCase(formattedDate)){
                holder.show_time.setText(mContext.getString(R.string.today));
            }else{
                holder.show_time.setText(chat.getDate());
                if(year.equalsIgnoreCase(currentYear)){
                    holder.show_time.setText(day + " " + month);
                }else{
                    holder.show_time.setText(day + " " + month + " " + year);
                }

            }

            holder.show_time.setVisibility(View.VISIBLE);
        }

        //show message
        if(chat.getMessage_type().equals("text")){
            holder.chat_image.setVisibility(View.GONE);
            holder.show_message.setVisibility(View.VISIBLE);
            holder.show_message.setText(chat.getMessage());


        }else if(chat.getMessage_type().equals("image")){
            holder.show_message.setVisibility(View.GONE);
            holder.chat_image.setVisibility(View.VISIBLE);
            holder.chat_image.layout(0,0,0,0);
            Glide.with(mContext).load(chat.getMessage()).into(holder.chat_image);
        }

        if(imageurl.equals("default")){
            holder.profile_image.setImageResource(R.drawable.ic_light_car);
        }else{
            Glide.with(mContext).load(imageurl).into(holder.profile_image);
        }

        holder.ststamp.setText(chat.getTime());

        //if position == last recent chat
        //text seen is show only last chat
        if(position == mChat.size()-1 && mChat.get(position).getSender().equals(firebaseUser.getUid())){
            if(chat.isIsseen()){
                holder.txt_seen.setText(mContext.getString(R.string.seen));
                holder.txt_seen.setVisibility(View.VISIBLE);
            }else{
                holder.txt_seen.setText(mContext.getString(R.string.sent));
                holder.txt_seen.setVisibility(View.VISIBLE);
            }
        }else{
            holder.txt_seen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show_message;
        public ImageView chat_image;
        public ImageView profile_image;
        public TextView txt_seen;
        public TextView ststamp;
        public TextView show_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            chat_image = itemView.findViewById(R.id.chat_image);
            show_time = itemView.findViewById(R.id.show_time);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            ststamp = itemView.findViewById(R.id.send_time_stamp);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }
}