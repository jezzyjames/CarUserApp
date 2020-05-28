package com.cs.tu.caruserapp.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cs.tu.caruserapp.MessageActivity;
import com.cs.tu.caruserapp.Model.Car;
import com.cs.tu.caruserapp.Model.Chat;
import com.cs.tu.caruserapp.Model.Chatlist;
import com.cs.tu.caruserapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    View view;

    private Context mContext;
    private List<Car> mCars;
    private List<Chatlist> userList;

    String theLastMessage;
    String the_time;
    String the_date;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    String chat_room_key;

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

        holder.car_id.setText(car.getCar_id().toUpperCase());
        holder.province.setText(car.getProvince());
        if(car.getImageURL().equals("default")){
            holder.profile_image.setImageResource(R.drawable.ic_light_car);
        }else{
            Glide.with(mContext).load(car.getImageURL()).into(holder.profile_image);
        }

        //check verify status
        if(car.getVerify_status() == 2){
            holder.verify_status.setVisibility(View.GONE);
        }else{
            holder.verify_status.setVisibility(View.VISIBLE);
        }


        //Show last message on user list
        lastMessage(chatlist.getSender_car_id(), car.getOwner_id(), car.getCar_id(), car.getProvince(), holder.last_msg, holder.date_time);

        //show notify sign on unread message
        countNewMessage(chatlist.getSender_car_id(), car.getOwner_id(), car.getCar_id(), holder.unread_num);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("receiver_id", car.getOwner_id());
                intent.putExtra("receiver_car_id", car.getCar_id());
                intent.putExtra("receiver_car_province", car.getProvince());
                intent.putExtra("sender_car_id", chatlist.getSender_car_id());
                intent.putExtra("sender_car_province", chatlist.getSender_car_province());
                mContext.startActivity(intent);

            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(car.getCar_id().toUpperCase() + " " + car.getProvince());

                String[] choices = mContext.getResources().getStringArray(R.array.user_interact_choice);
                builder.setItems(choices, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                builder.setTitle(mContext.getString(R.string.report) + " " + car.getCar_id().toUpperCase() + " " + car.getProvince());

                                //set layout
                                LinearLayout layout = new LinearLayout(mContext);
                                layout.setOrientation(LinearLayout.VERTICAL);
                                layout.setPadding(45,30,30,30);

                                //set spinner
                                String[] spinner_list = mContext.getResources().getStringArray(R.array.report_list);
                                final ArrayAdapter<String> adp = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, spinner_list);
                                final Spinner spinner = new Spinner(mContext);
                                spinner.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                spinner.setAdapter(adp);

                                //set edit text
                                final EditText report_input = new EditText(mContext);
                                report_input.setInputType(InputType.TYPE_CLASS_TEXT);

                                //connect together
                                layout.addView(spinner);
                                layout.addView(report_input);
                                builder.setView(layout);

                                builder.setPositiveButton(mContext.getString(R.string.report), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String report_type = spinner.getSelectedItem().toString();
                                        String report_message = report_input.getText().toString();

                                        //get current date
                                        Date date = Calendar.getInstance().getTime();
                                        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
                                        String formattedDate = df.format(date);
                                        //get current time
                                        String pattern = "HH:mm a";
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                                        String currentTimeString = simpleDateFormat.format(new Date());

                                        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                        reference = FirebaseDatabase.getInstance().getReference();
                                        HashMap<String, String> hashMap = new HashMap<>();
                                        hashMap.put("report_type", report_type);
                                        hashMap.put("report_message", report_message);
                                        hashMap.put("id", car.getOwner_id());
                                        hashMap.put("car_id", car.getCar_id());
                                        hashMap.put("province", car.getProvince());
                                        hashMap.put("date", formattedDate);
                                        hashMap.put("time", currentTimeString);
                                        hashMap.put("reporter_id", firebaseUser.getUid());
                                        hashMap.put("reporter_car_id", chatlist.getSender_car_id());
                                        hashMap.put("reporter_car_province", chatlist.getSender_car_province());

                                        reference.child("Report").child(firebaseUser.getUid()).push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                storeNotiToDatabase(mContext.getString(R.string.car_reported) + " " + car.getCar_id().toUpperCase() + " " + car.getProvince()
                                                        + " " + mContext.getString(R.string.car_reported2));
                                            }
                                        });

                                    }
                                });
                                builder.setNegativeButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                builder.show();
                                break;
                            case 1:
                                new android.app.AlertDialog.Builder(mContext)
                                        .setTitle(mContext.getString(R.string.confirm))
                                        .setMessage(mContext.getString(R.string.delete_chat_message) + " " + car.getCar_id().toUpperCase() + " " + car.getProvince() + " " + mContext.getString(R.string.delete_chat_message_end))
                                        .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                                reference = FirebaseDatabase.getInstance().getReference("Chatlist")
                                                        .child(firebaseUser.getUid())
                                                        .child(chatlist.getSender_car_id() + "_" + chatlist.getSender_car_province())
                                                        .child(car.getCar_id() + "_" + chatlist.getReceiver_car_province());
                                                reference.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(mContext, mContext.getString(R.string.delete_chat_complete), Toast.LENGTH_SHORT).show();
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

    @Override
    public int getItemCount() {
        return mCars.size();
    }

    //check for last message
    private void lastMessage(final String sender_car_id, final String receiver, final String receiver_car_id, final String receiver_car_province
            , final TextView last_msg, final TextView date_time){

        theLastMessage = "default";
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        createChatRoomKey(firebaseUser.getUid(), sender_car_id, receiver, receiver_car_id);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(chat_room_key).child("Message");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                        if(chat.getMessage_type().equals("text")){
                            theLastMessage = filterBadWords(chat.getMessage());
                        }else if(chat.getMessage_type().equals("image")){
                            if(chat.getSender().equals(firebaseUser.getUid())){
                                theLastMessage = mContext.getString(R.string.you_sent_photo);
                            }else{
                                theLastMessage =  receiver_car_id.toUpperCase() + " " + receiver_car_province + " " + mContext.getString(R.string.sent_photo);
                            }

                        }
                        the_date = chat.getDate();
                        the_time = chat.getTime();

                }

                switch (theLastMessage){
                    case "default":
                        last_msg.setText("");
                        date_time.setText("");
                        break;

                    default:
                        last_msg.setText(theLastMessage);

                        Date date = Calendar.getInstance().getTime();
                        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
                        String formattedDate = df.format(date);

                        String currentDateArr[] = formattedDate.split(" ");
                        String currentYear = currentDateArr[2];

                        String dateArr[] = the_date.split(" ");
                        String day = dateArr[0];
                        String month = dateArr[1];
                        String year = dateArr[2];

                        if(the_date.equalsIgnoreCase(formattedDate)){
                            date_time.setText(the_time);
                        } else{
                            if(currentYear.equalsIgnoreCase(year)){
                                date_time.setText(day + " " + month);
                            }else{
                                date_time.setText(day + " " + month + " " + year);
                            }

                        }

                        break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void countNewMessage(final String sender_car_id, final String receiver, final String receiver_car_id, final TextView unread_num){

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        createChatRoomKey(firebaseUser.getUid(), sender_car_id, receiver, receiver_car_id);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(chat_room_key).child("Message");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int unread = 0;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if((chat.getReceiver().equals(firebaseUser.getUid()) || chat.getSender().equals(receiver)) && !chat.isIsseen()){
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

    public String filterBadWords(String message) {
        String[] bad_words = mContext.getResources().getStringArray(R.array.bad_words);;
        final List<String> words = Arrays.asList(bad_words);;

        for(String word : words){
            Pattern rx = Pattern.compile("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE);
            message = rx.matcher(message).replaceAll(new String(new char[word.length()]).replace('\0', '*'));
        }

        return message;
    }

    private void storeNotiToDatabase(final String messageBody){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        //get current date
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        String formattedDate = df.format(date);
        //get current time
        String pattern = "HH:mm a";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
        String currentTimeString = simpleDateFormat.format(new Date());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("imageURL", "default");
        hashMap.put("message", messageBody);
        hashMap.put("isseen", false);
        hashMap.put("date", formattedDate);
        hashMap.put("time", currentTimeString);

        reference.child("Notification").child(firebaseUser.getUid()).push().setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(mContext, messageBody, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createChatRoomKey(final String sender, final String sender_car_id, final String receiver, final String receiver_car_id){
        //Create new chat room
        String sender_key = sender.replaceAll("[\\D]", "");
        String receiver_key = receiver.replaceAll("[\\D]", "");
        int sender_car_id_key = 0;
        int receiver_car_id_key = 0;

        for(int i = 0; i < sender_car_id.length(); i++){
            int num = sender_car_id.charAt(i);
            sender_car_id_key = sender_car_id_key + num;
        }
        for(int i = 0; i < receiver_car_id.length(); i++){
            int num = receiver_car_id.charAt(i);
            receiver_car_id_key = receiver_car_id_key + num;
        }

        chat_room_key = String.valueOf((Integer.parseInt(sender_key)*sender_car_id_key) + (Integer.parseInt(receiver_key)*receiver_car_id_key));
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