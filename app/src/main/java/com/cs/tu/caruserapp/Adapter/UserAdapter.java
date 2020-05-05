package com.cs.tu.caruserapp.Adapter;

import android.content.Context;
import android.content.Intent;
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
import com.cs.tu.caruserapp.Model.Chat;
import com.cs.tu.caruserapp.Model.Chatlist;
import com.cs.tu.caruserapp.Model.User;
import com.cs.tu.caruserapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.w3c.dom.Text;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    View view;

    private Context mContext;
    private List<Car> mCars;
    private List<Chatlist> userList;

    String theLastMessage;
    String the_date_time;

    public UserAdapter(Context mContext, List<Car> mCars, List<Chatlist> userList) {
        this.mContext = mContext;
        this.mCars = mCars;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Store user_item layout into view
        view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);

        //return "holder" that carrying layout "view" to onBindViewHolder
        return new UserAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Car car = mCars.get(position);
        final Chatlist chatlist = userList.get(position);

        holder.car_id.setText(car.getCar_id());
        holder.province.setText(car.getProvince());
        if(car.getImageURL().equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(mContext).load(car.getImageURL()).into(holder.profile_image);
        }

        //Show last message on user list
        lastMessage(chatlist.getSender_car_id(), car.getCar_id(), holder.last_msg, holder.date_time);

        //show notify sign on unread message
        countNewMessage(chatlist.getSender_car_id(), car.getCar_id(), holder.unread_num);

        //check verify status
        checkVerifyStatus(car.getOwner_id(), holder.verify_status);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("receiver_id", car.getOwner_id());
                intent.putExtra("receiver_car_id", car.getCar_id());
                intent.putExtra("sender_car_id", chatlist.getSender_car_id());
                mContext.startActivity(intent);

            }
        });

    }

    private void checkVerifyStatus(final String receiver_id, final TextView verify_status) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(receiver_id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(!user.isVerify_status()){
                    verify_status.setVisibility(View.VISIBLE);
                }else{
                    verify_status.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mCars.size();
    }

    //check for last message
    private void lastMessage(final String sender_car_id, final String receiver_car_id, final TextView last_msg, final TextView date_time){
        theLastMessage = "default";
        the_date_time = "default";
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver_car_id().equals(sender_car_id) && chat.getSender_car_id().equals(receiver_car_id) || chat.getReceiver_car_id().equals(receiver_car_id) && chat.getSender_car_id().equals(sender_car_id)){
                        theLastMessage = chat.getMessage();
                        the_date_time = chat.getTime();
                    }
                }

                switch (theLastMessage){
                    case "default":
                        last_msg.setText("No Message");
                        break;

                    default:
                        last_msg.setText(theLastMessage);
                        date_time.setText(the_date_time);
                        break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void countNewMessage(final String sender_car_id, final String receiver_car_id, final TextView unread_num){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int unread = 0;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver_car_id().equals(sender_car_id) && chat.getSender_car_id().equals(receiver_car_id) && !chat.isIsseen()){
                        unread++;
                    }
                }

                if(unread == 0){
                    unread_num.setVisibility(View.INVISIBLE);
                }else{
                    unread_num.setText("" + unread);
                    unread_num.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView car_id;
        public TextView province;
        public ImageView profile_image;
        TextView last_msg;
        TextView unread_num;
        TextView verify_status;
        TextView date_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            car_id = itemView.findViewById(R.id.username);
            province = itemView.findViewById(R.id.province);
            profile_image = itemView.findViewById(R.id.profile_image);
            last_msg = itemView.findViewById(R.id.last_msg);
            unread_num = itemView.findViewById(R.id.unread_num);
            verify_status = itemView.findViewById(R.id.verify_status);
            date_time = itemView.findViewById(R.id.date_time);

        }
    }

}