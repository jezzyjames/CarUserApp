package com.cs.tu.caruserapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.cs.tu.caruserapp.Adapter.MessageAdapter;
import com.cs.tu.caruserapp.Fragments.APIService;
import com.cs.tu.caruserapp.Model.Car;
import com.cs.tu.caruserapp.Model.Chat;
import com.cs.tu.caruserapp.Model.Sender;
import com.cs.tu.caruserapp.Model.User;
import com.cs.tu.caruserapp.Notification.Client;
import com.cs.tu.caruserapp.Notification.Data;
import com.cs.tu.caruserapp.Notification.MyResponse;
import com.cs.tu.caruserapp.Notification.Token;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;
    TextView province;
    TextView verify_status;
    TextView phone_number;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    ImageButton btn_camera;
    ImageButton btn_gallery;
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
    String receiver_car_province;
    String sender_car_id;
    String sender_car_province;

    private static final int REQUEST_IMAGE_CAPTURE = 0;
    private static final int GALLERY_REQUEST = 1;
    Uri SendImageUri;
    String currentPhotoPath;
    StorageReference storageReference;
    private StorageTask uploadTask;

    boolean warn_hidden_phone = false;
    boolean warn_unverified = false;


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
                    intent.putExtra("dont_show_dialog", true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
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
        province = findViewById(R.id.province);
        verify_status = findViewById(R.id.verify_status);
        phone_number = findViewById(R.id.phonenumber);
        btn_camera = findViewById(R.id.btn_camera);
        btn_gallery = findViewById(R.id.btn_gallery);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        //get opposite user id data in intent from UserAdapter (Reciever id)
        intent = getIntent();
        receiver_id = intent.getStringExtra("receiver_id");
        receiver_car_id = intent.getStringExtra("receiver_car_id");
        receiver_car_province = intent.getStringExtra("receiver_car_province");
        sender_car_id = intent.getStringExtra("sender_car_id");
        sender_car_province = intent.getStringExtra("sender_car_province");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("uploads");


        //********* CAMERA BUTTON ***********//
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MessageActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);

                } else {
                    openImage(REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        //********* GALLERY BUTTON ***********//
        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage(GALLERY_REQUEST);
            }
        });

        //********* SEND MESSAGE BUTTON ***********//
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String msg = text_send.getText().toString();
                if(!msg.equals("")){
                    sendMessage(firebaseUser.getUid(), receiver_id, sender_car_id, receiver_car_id, msg, "text", sender_car_province, receiver_car_province);
                }else{
                    Toast.makeText(MessageActivity.this, getString(R.string.cant_send_empty_message), Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");

            }
        });

        //********* SHOW USER NAME, PROFILE PIC AND READ MESSAGE ***********//
        reference = FirebaseDatabase.getInstance().getReference("Cars").child(receiver_car_id + "_" + receiver_car_province);
        //Read user data in database
        reference.addValueEventListener(new ValueEventListener() {
            //found user
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    //set receiver data on Action bar
                    Car car = dataSnapshot.getValue(Car.class);
                    username.setText(car.getCar_id().toUpperCase());
                    province.setText(car.getProvince());

                    //check verify status
                    if(car.getVerify_status() == 2){
                        verify_status.setVisibility(View.GONE);
                    }else if(car.getVerify_status() == 0 || car.getVerify_status() == 1){
                        if(!warn_unverified){
                            warnDialog(0);
                        }
                        verify_status.setVisibility(View.VISIBLE);
                    }

                    //set Circle ImageView
                    if(car.getImageURL().equals("default")){
                        profile_image.setImageResource(R.drawable.ic_light_car);
                    }else{
                        Glide.with(getApplicationContext()).load(car.getImageURL()).into(profile_image);
                    }

                    //read and show all chat message on screen
                    readMessage(sender_car_id, receiver_car_id, car.getImageURL(), sender_car_province, receiver_car_province);
                }



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
                    if((chat.getReceiver_car_id().equals(sender_car_id) && chat.getReceiver_car_province().equals(sender_car_province))
                            && (chat.getSender_car_id().equals(receiver_car_id) && chat.getSender_car_province().equals(receiver_car_province))){
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


    public String filterBadWords(String message) {
        String[] bad_words = getResources().getStringArray(R.array.bad_words);;
        final List<String> words = Arrays.asList(bad_words);;

        for(String word : words){
            Pattern rx = Pattern.compile("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE);
            message = rx.matcher(message).replaceAll(new String(new char[word.length()]).replace('\0', '*'));
        }

//        String censor_word = message;
//        for (String word : words) {
//            Pattern rx = Pattern.compile(word, Pattern.CASE_INSENSITIVE);
//            message = rx.matcher(message).replaceAll(new String(new char[word.length()]).replace('\0', '*'));
//
//            for(int i=0;i<censor_word.length();i++){
//                if(censor_word.charAt(i) != message.charAt(i)){
//                    censor_word = censor_word.replace(censor_word.charAt(i),'*');
//                }
//            }
//        }

        return message;
    }

    private void sendMessage(String sender, final String receiver, final String sender_car_id, final String receiver_car_id
            , String message, String message_type, final String sender_car_province, final String receiver_car_province){
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
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("sender_car_id", sender_car_id);
        hashMap.put("receiver_car_id", receiver_car_id);
        hashMap.put("sender_car_province", sender_car_province);
        hashMap.put("receiver_car_province", receiver_car_province);
        hashMap.put("message", message);
        hashMap.put("message_type", message_type);
        hashMap.put("isseen", false);
        hashMap.put("date",formattedDate);
        hashMap.put("time", currentTimeString);

        reference.child("Chats").push().setValue(hashMap);

        //Add chatlist to sender ID
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(firebaseUser.getUid())
                .child(sender_car_id + "_" + sender_car_province)
                .child(receiver_car_id + "_" + receiver_car_province);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    chatRef.child("id").setValue(receiver);
                    chatRef.child("sender_car_id").setValue(sender_car_id);
                    chatRef.child("receiver_car_id").setValue(receiver_car_id);
                    chatRef.child("sender_car_province").setValue(sender_car_province);
                    chatRef.child("receiver_car_province").setValue(receiver_car_province);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Add chatlist to receiver ID
        final DatabaseReference receiver_chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(receiver)
                .child(receiver_car_id + "_" + receiver_car_province)
                .child(sender_car_id + "_" + sender_car_province);

        receiver_chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    receiver_chatRef.child("id").setValue(firebaseUser.getUid());
                    receiver_chatRef.child("sender_car_id").setValue(receiver_car_id);
                    receiver_chatRef.child("receiver_car_id").setValue(sender_car_id);
                    receiver_chatRef.child("sender_car_province").setValue(receiver_car_province);
                    receiver_chatRef.child("receiver_car_province").setValue(sender_car_province);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //NOTIFICATION Part
        String msg = "";
        if(message_type.equals("text")){
            msg = message;
        }else if(message_type.equals("image")){
            msg = "Sent a photo.";
        }

        if(notify){
            //send noti msg to receiver with sender name
            sendNotification(receiver, sender_car_id, receiver_car_id, sender_car_province, receiver_car_province, msg);
        }
        notify = false;

    }

    private void sendNotification(final String receiver_id, final String sender_car_id, final String receiver_car_id
            , final String sender_car_province, final String receiver_car_province, final String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver_id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), sender_car_id, sender_car_province, R.mipmap.ic_car_launcher, message, sender_car_id.toUpperCase() + " :"
                            , receiver_id, receiver_car_id, receiver_car_province);

                    //pack data and receiver token into sender
                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code() == 200){
                                        if(response.body().success != 1){
                                            Toast.makeText(MessageActivity.this, getString(R.string.send_noti_fail), Toast.LENGTH_SHORT).show();
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
    private void readMessage(final String sender_car_id, final String receiver_car_id, final String imageurl, final String sender_car_province, final String receiver_car_province){
        mchat = new ArrayList<>();

        //Read database from Chats
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                int sender_chat = 0;
                int receiver_chat = 0;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    //add all chat that include sender and receiver id to ArrayList
                    if((chat.getReceiver_car_id().equals(sender_car_id) && chat.getReceiver_car_province().equals(sender_car_province))
                            && (chat.getSender_car_id().equals(receiver_car_id) && chat.getSender_car_province().equals(receiver_car_province))
                            || (chat.getReceiver_car_id().equals(receiver_car_id) && chat.getReceiver_car_province().equals(receiver_car_province))
                            && (chat.getSender_car_id().equals(sender_car_id) && chat.getSender_car_province().equals(sender_car_province))){

                            //filter bad words
                            chat.setMessage(filterBadWords(chat.getMessage()));
                            mchat.add(chat);
                            if(chat.getSender().equals(firebaseUser.getUid())){
                               sender_chat++;
                            }
                            else{
                               receiver_chat++;
                            }
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, mchat, imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
                //show phone number if start some chat message
                final int sender_count = sender_chat;
                final int receiver_count = receiver_chat;
                    //Get user phone number and show
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(receiver_id);
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            String receiver_raw_phonenum = user.getPhone_number();
                            String phoneArr[] = receiver_raw_phonenum.split(" ");
                            String hidden_number = phoneArr[0];
                            for(int i = 1; i < phoneArr.length-1; i++){
                                String phone_part = phoneArr[i];
                                for(int j = 0; j < phone_part.length(); j++){
                                    if(j == 0){
                                        hidden_number = hidden_number + " ";
                                    }
                                    hidden_number = hidden_number + "x";
                                }
                            }
                            hidden_number = hidden_number + " " + phoneArr[phoneArr.length-1];

                            if(sender_count > 0 && receiver_count > 0){
                                phone_number.setText(receiver_raw_phonenum);
                            }else {
                                phone_number.setText(hidden_number);
                                if(!warn_hidden_phone){
                                    warnDialog(1);
                                }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("", databaseError.getMessage());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            File file = new File(currentPhotoPath);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bitmap != null) {

            }

            ExifInterface ei = null;
            try {
                ei = new ExifInterface(currentPhotoPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            Bitmap rotatedBitmap = null;
            switch(orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }

            SendImageUri = convertBitmapToUri(this, rotatedBitmap);
            uploadImage(SendImageUri);

        }

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            SendImageUri = data.getData();
            uploadImage(SendImageUri);

        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(final Uri SendImageUri){
        if(SendImageUri != null){
            final StorageReference fileReference = storageReference.child("image_message/" + firebaseUser.getUid() + "/"
                    + sender_car_id + "_" + sender_car_province + "/" + receiver_car_id + "_" + receiver_car_province + "/" + System.currentTimeMillis() + "." + getFileExtension(SendImageUri));

            uploadTask = fileReference.putFile(SendImageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();
                        sendMessage(firebaseUser.getUid(), receiver_id, sender_car_id, receiver_car_id, mUri, "image", sender_car_province, receiver_car_province);

                    }else{
                        Toast.makeText(MessageActivity.this, getString(R.string.upload_image_failed), Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MessageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(this, getString(R.string.no_image_select), Toast.LENGTH_SHORT).show();
        }
    }

    private void openImage(int request_code) {
                switch (request_code) {
                    case REQUEST_IMAGE_CAPTURE:
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            // Create the File where the photo should go
                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                            } catch (IOException ex) {

                            }
                            // Continue only if the File was successfully created
                            if (photoFile != null) {
                                Uri photoURI = FileProvider.getUriForFile(this, "com.cs.tu.caruserapp", photoFile);
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                            }

                        }
                        break;

                    case GALLERY_REQUEST:
                        Intent pictureActionIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        if (pictureActionIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(pictureActionIntent, GALLERY_REQUEST);
                        }
                        break;
                }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public Uri convertBitmapToUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "JPEG_" + timeStamp, null);
        return Uri.parse(path);
    }

    //Check camera permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImage(REQUEST_IMAGE_CAPTURE);

                } else {
                    Toast.makeText(this, getString(R.string.camera_permission_denied), Toast.LENGTH_SHORT).show();
                    new AlertDialog.Builder(this)
                            .setMessage(getString(R.string.please_give_camera_permission))
                            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

                                }
                            })
                            .setNegativeButton(getString(R.string.cancel), null)
                            .show();

                }
                return;
            }
        }
    }

    public void warnDialog(int dialog){
        switch (dialog){
            case 0:
                warn_unverified = true;
                new FancyGifDialog.Builder(this)
                        .setTitle(getString(R.string.please_be_careful))
                        .setMessage(getString(R.string.this_user_is_not_verify))
                        .setPositiveBtnBackground("#4A46B5")
                        .setPositiveBtnText(getString(R.string.yes))
                        .setGifResource(R.drawable.bad_emoji_resize)   //Pass your Gif here
                        .isCancellable(true)
                        .build();
                break;
            case 1:
                warn_hidden_phone = true;
                new FancyGifDialog.Builder(this)
                        .setTitle(getString(R.string.phone_number_hide))
                        .setMessage(getString(R.string.phone_number_show_after))
                        .setPositiveBtnBackground("#4A46B5")
                        .setPositiveBtnText(getString(R.string.yes))
                        .setGifResource(R.drawable.phone)   //Pass your Gif here
                        .isCancellable(true)
                        .build();
                break;
        }
    }

    //--- Dont send noti while chatting ---
    private void currentUser(String receiver_car_id, String receiver_car_province){
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", receiver_car_id + "_" + receiver_car_province);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //--- Dont send noti while chatting --- set current chatting receiver ID to SharedPreference
        currentUser(receiver_car_id, receiver_car_province);
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        currentUser("none", "none");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        boolean from_search = intent.getBooleanExtra("from_search", false);
        if(from_search) {
            Intent intent = new Intent(MessageActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        finish();
    }
}