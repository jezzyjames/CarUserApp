package com.cs.tu.caruserapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class VerifyActivity extends AppCompatActivity {
    Intent intent;

    String my_car_id;
    String my_car_province;
    boolean verify_for_report;
    String exist_car_owner_id;

    TextView txt_car_id;
    CardView card_regist_book;
    Button btn_choose_book;

    LinearLayout regist_book_layout;
    ImageView image_car_book;
    Button btn_take_photo;
    Button btn_take_gallery;
    Button btn_submit;
    ProgressBar upload_progress;
    TextView txt_wait;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    private static final int REQUEST_IMAGE_CAPTURE = 0;
    private static final int GALLERY_REQUEST = 1;
    Uri VerifyImageUri;
    String currentPhotoPath;
    StorageReference storageReference;
    private StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        txt_car_id = findViewById(R.id.txt_car_id);
        card_regist_book = findViewById(R.id.card_regist_book);
        btn_choose_book = findViewById(R.id.btn_choose_book);

        regist_book_layout = findViewById(R.id.regist_book_verify_layout);
        image_car_book = findViewById(R.id.image_car_book);
        btn_take_photo = findViewById(R.id.btn_take_photo);
        btn_take_gallery = findViewById(R.id.btn_take_gallery);
        btn_submit = findViewById(R.id.btn_submit);
        upload_progress = findViewById(R.id.upload_progress);
        txt_wait = findViewById(R.id.txt_wait);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        intent = getIntent();
        verify_for_report = intent.getBooleanExtra("verify_for_report", false);
        my_car_id = intent.getStringExtra("my_car_id");
        my_car_province = intent.getStringExtra("my_car_province");
        exist_car_owner_id = intent.getStringExtra("exist_car_owner_id");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.car_verify));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txt_car_id.setText(getString(R.string.car_id_in_verify) + "\n" + my_car_id.toUpperCase() + " " + my_car_province);

        btn_choose_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card_regist_book.setVisibility(View.GONE);
                regist_book_layout.setVisibility(View.VISIBLE);
            }
        });


        btn_take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(VerifyActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(VerifyActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);

                } else {
                    openImage(REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        btn_take_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage(GALLERY_REQUEST);
            }
        });
    }

    private void updateStatus(){
        reference = FirebaseDatabase.getInstance().getReference("Cars").child(my_car_id.toLowerCase() + "_" + my_car_province);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reference.child("verify_status").setValue(1).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        storeNotiToDatabase(getString(R.string.send_verify_complete) + " " + my_car_id.toUpperCase() + " " + my_car_province + " " +getString(R.string.send_verify_complete2));
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

            VerifyImageUri = convertBitmapToUri(this, rotatedBitmap);
            sendVerifyImage();

        }

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            VerifyImageUri = data.getData();
            sendVerifyImage();

        }
    }

    private void sendVerifyImage(){
        if(VerifyImageUri != null){
            image_car_book.setImageURI(VerifyImageUri);
            btn_submit.setVisibility(View.VISIBLE);
            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btn_submit.setVisibility(View.GONE);
                    upload_progress.setVisibility(View.VISIBLE);
                    txt_wait.setVisibility(View.VISIBLE);
                    uploadImage(VerifyImageUri);
                }
            });
        }else{
            Toast.makeText(this, getString(R.string.no_image_select), Toast.LENGTH_SHORT).show();
        }
    }

    private void setDefualtUI(){
        btn_submit.setVisibility(View.VISIBLE);
        upload_progress.setVisibility(View.GONE);
        txt_wait.setVisibility(View.GONE);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(final Uri VerifyImageUri){
        if(VerifyImageUri != null){
            final StorageReference fileReference = storageReference.child("verify_photo/vehicle_regist_book/" + firebaseUser.getUid() + "/" + my_car_id + "_" + my_car_province
                    + "/" + my_car_id + "_" + my_car_province + "_" + System.currentTimeMillis() + "." + getFileExtension(VerifyImageUri));

            uploadTask = fileReference.putFile(VerifyImageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        setDefualtUI();
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){

                        if(!verify_for_report){
                            updateStatus();
                        }else{
                            reportUser();

                        }


                    }else{
                        setDefualtUI();
                        Toast.makeText(VerifyActivity.this, getString(R.string.upload_image_failed), Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    setDefualtUI();
                    Toast.makeText(VerifyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            setDefualtUI();
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

    private void reportUser(){
        //get current date
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        String formattedDate = df.format(date);
        //get current time
        String pattern = "HH:mm a";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String currentTimeString = simpleDateFormat.format(new Date());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("report_type", "Car exist");
        hashMap.put("report_message", "My car is exist in system!");
        hashMap.put("id", exist_car_owner_id);
        hashMap.put("car_id", my_car_id);
        hashMap.put("province", my_car_province);
        hashMap.put("date", formattedDate);
        hashMap.put("time", currentTimeString);
        hashMap.put("reporter_id", firebaseUser.getUid());
        hashMap.put("reporter_car_id", my_car_id);
        hashMap.put("reporter_car_province", my_car_province);

        reference.child("Report").child(firebaseUser.getUid()).push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(VerifyActivity.this, getString(R.string.exist_car_report_message) + my_car_id.toUpperCase() + " " + my_car_province + getString(R.string.exist_car_report_message2), Toast.LENGTH_LONG).show();
                storeNotiToDatabase(getString(R.string.exist_car_report_message) + my_car_id.toUpperCase() + " " + my_car_province + getString(R.string.exist_car_report_message2));
            }
        });
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
                sendNotification(getString(R.string.noti_from_system), messageBody);
                Toast.makeText(VerifyActivity.this, getString(R.string.send_verify_complete) + " " + my_car_id + " " + my_car_province + " " + getString(R.string.send_verify_complete2), Toast.LENGTH_LONG).show();
                startActivity(new Intent(VerifyActivity.this, ProfileActivity.class));
                finish();
            }
        });
    }

    private void sendNotification(String messageTitle, String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("dont_show_dialog", true);
        intent.putExtra("open_noti", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_car_launcher)
                        .setContentTitle(messageTitle)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}