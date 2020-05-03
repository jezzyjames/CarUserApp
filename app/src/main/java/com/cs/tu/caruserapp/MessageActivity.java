package com.cs.tu.caruserapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.cs.tu.caruserapp.Adapter.MessageAdapter;
import com.cs.tu.caruserapp.Fragments.APIService;
import com.cs.tu.caruserapp.Model.Car;
import com.cs.tu.caruserapp.Model.Chat;
import com.cs.tu.caruserapp.Model.Sender;;
import com.cs.tu.caruserapp.Notification.Client;
import com.cs.tu.caruserapp.Notification.Data;
import com.cs.tu.caruserapp.Notification.MyResponse;
import com.cs.tu.caruserapp.Notification.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    ImageButton btn_send;
    EditText text_send;
    MessageAdapter messageAdapter;
    List<Chat> mchat;

    RecyclerView recyclerView;

    Intent intent;

    ValueEventListener seenListener;

    APIService apiService;

    boolean notify = false;
    String receiver_id;
    String receiver_car_id;
    String sender_car_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean from_search = intent.getBooleanExtra("from_search", false);
                if(from_search) {
                    Intent intent = new Intent(MessageActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                finish();

            }
        });

        //api for using firebase cloud message (for notification)
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        //setup recyclerView for showing message
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        //get opposite user id data in intent from UserAdapter (Reciever id)
        intent = getIntent();
        receiver_id = intent.getStringExtra("receiver_id");
        receiver_car_id = intent.getStringExtra("receiver_car_id");
        sender_car_id = intent.getStringExtra("sender_car_id");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //********* SEND MESSAGE BUTTON ***********//
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String msg = text_send.getText().toString();
                if(!msg.equals("")){
                    sendMessage(firebaseUser.getUid(), receiver_id, sender_car_id, receiver_car_id, msg);
                }else{
                    Toast.makeText(MessageActivity.this, "Can't send empty message", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");

            }
        });

        //********* SHOW USER NAME, PROFILE PIC AND READ MESSAGE ***********//
        reference = FirebaseDatabase.getInstance().getReference("Cars").child(receiver_car_id);
        //Read user data in database
        reference.addValueEventListener(new ValueEventListener() {
            //found user
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //set receiver data on Action bar
                Car car = dataSnapshot.getValue(Car.class);
                username.setText(car.getCar_id());

                if(car.getImageURL().equals("default")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(getApplicationContext()).load(car.getImageURL()).into(profile_image);
                }

                //read and show all chat message on screen
                readMessage(sender_car_id, receiver_car_id, car.getImageURL());
            }
            //didn't found
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMessage(receiver_car_id);
    }


    private void seenMessage(final String receiver_car_id){
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver_car_id().equals(sender_car_id) && chat.getSender_car_id().equals(receiver_car_id)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String sender, final String receiver, final String sender_car_id, final String receiver_car_id, String message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        String currentDateString = DateFormat.getDateInstance().format(new Date());
        String pattern = "HH:mm a";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String currentTimeString = simpleDateFormat.format(new Date());
        //String currentTimeString = DateFormat.getTimeInstance().format(new Date());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("sender_car_id", sender_car_id);
        hashMap.put("receiver_car_id", receiver_car_id);
        hashMap.put("message", message);
        hashMap.put("isseen", false);
        hashMap.put("date",currentDateString);
        hashMap.put("time", currentTimeString);

        reference.child("Chats").push().setValue(hashMap);

        //Add chatlist to sender ID
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(firebaseUser.getUid())
                .child(sender_car_id)
                .child(receiver_car_id);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    chatRef.child("id").setValue(receiver);
                    chatRef.child("sender_car_id").setValue(sender_car_id);
                    chatRef.child("receiver_car_id").setValue(receiver_car_id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Add chatlist to receiver ID
        final DatabaseReference receiver_chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(receiver)
                .child(receiver_car_id)
                .child(sender_car_id);

        receiver_chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    receiver_chatRef.child("id").setValue(firebaseUser.getUid());
                    receiver_chatRef.child("sender_car_id").setValue(receiver_car_id);
                    receiver_chatRef.child("receiver_car_id").setValue(sender_car_id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //NOTIFICATION Part
        final String msg = message;
        if(notify){
            //send noti msg to receiver with sender name
            sendNotification(receiver, sender_car_id, receiver_car_id, msg);
        }
        notify = false;

    }

    private void sendNotification(final String receiver_id, final String sender_car_id, final String receiver_car_id, final String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver_id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), sender_car_id, R.mipmap.ic_launcher, sender_car_id+": "+message, "New Message", receiver_id, receiver_car_id);

                    //pack data and receiver token into sender
                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code() == 200){
                                        if(response.body().success != 1){
                                            Toast.makeText(MessageActivity.this, "Failed to send notification message!", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //userid mean opposite id (reciever)***
    private void readMessage(final String sender_car_id, final String receiver_car_id, final String imageurl){
        mchat = new ArrayList<>();

        //Read database from Chats
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    //add all chat that include sender and receiver id to ArrayList
                    if(chat.getReceiver_car_id().equals(sender_car_id) && chat.getSender_car_id().equals(receiver_car_id)
                            || chat.getReceiver_car_id().equals(receiver_car_id) && chat.getSender_car_id().equals(sender_car_id)){

                            mchat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, mchat, imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("", databaseError.getMessage());
            }
        });
    }

    //--- Dont send noti while chatting ---
    private void currentUser(String receiver_car_id){
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", receiver_car_id);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //--- Dont send noti while chatting --- set current chatting receiver ID to SharedPreference
        currentUser(receiver_car_id);
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        currentUser("none");
    }
}