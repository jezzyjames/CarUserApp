package com.cs.tu.caruserapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cs.tu.caruserapp.Adapter.CarAdapter;
import com.cs.tu.caruserapp.Dialog.AddCarDialog;
import com.cs.tu.caruserapp.Model.Car;
import com.cs.tu.caruserapp.Model.User;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileActivity extends AppCompatActivity implements AddCarDialog.OnInputListener {
    //Receive input from AddCarDialog
    @Override
    public void sendInput(Uri imageUri, String input_carid, String input_province, String input_brand, String input_model, String input_color) {
        addCar(imageUri, input_carid, input_province, input_brand, input_model, input_color);
    }

    private RecyclerView recyclerView;

    private CarAdapter carAdapter;
    private List<Car> carsList;

    TextView username;
    TextView phonenumber;
    TextView txt_add_car;
    TextView license_status;
    Button verify_btn;

    DatabaseReference reference;
    FirebaseUser firebaseUser;

    StorageReference storageReference;
    private static final int REQUEST_IMAGE_CAPTURE = 0;
    private StorageTask uploadTask;

    DialogFragment dialogFragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        username = findViewById(R.id.username);
        phonenumber = findViewById(R.id.phonenumber);
        license_status = findViewById(R.id.license_status);
        verify_btn = findViewById(R.id.verify_btn);
        txt_add_car = findViewById(R.id.txt_add_car);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getFirstname() + " " + user.getLastname());
                phonenumber.setText(user.getPhone_number());
                license_status.setText("verified");
                license_status.setTextColor(Color.GREEN);
                verify_btn.setVisibility(View.GONE);

                if(!user.isVerify_status()){
                    license_status.setText("unverified");
                    license_status.setTextColor(Color.RED);
                    verify_btn.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("", databaseError.getMessage());
            }
        });

        //***get your owner cars from database and show on recyclerView***
        carsList = new ArrayList<>();

        Query query = FirebaseDatabase.getInstance().getReference("Cars").orderByChild("owner_id").equalTo(firebaseUser.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                carsList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Car car = snapshot.getValue(Car.class);
                    carsList.add(car);
                }
                carAdapter = new CarAdapter(ProfileActivity.this, carsList);
                recyclerView.setAdapter(carAdapter);

                //can't add more than 3 cars
                if(carsList.size() >= 3){
                    txt_add_car.setVisibility(View.GONE);
                    txt_add_car.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(ProfileActivity.this, "Can't add more than three cars", Toast.LENGTH_SHORT).show();

                        }
                    });
                }else{
                    txt_add_car.setVisibility(View.VISIBLE);
                    txt_add_car.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);

                            } else {
                                showAddCarDialog();
                            }


                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void addCar(final Uri imageUri, final String car_id, final String province, final String car_brand, final String car_model, final String car_color){
        if(imageUri != null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
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

                        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        reference = FirebaseDatabase.getInstance().getReference("Cars").child(car_id.toLowerCase());

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("imageURL", mUri);
                        hashMap.put("car_id", car_id.toLowerCase());
                        hashMap.put("province", province);
                        hashMap.put("brand", car_brand.substring(0, 1).toUpperCase() + car_brand.substring(1));
                        hashMap.put("model", car_model.substring(0, 1).toUpperCase() + car_model.substring(1));
                        hashMap.put("color", car_color);
                        hashMap.put("owner_id", firebaseUser.getUid());
                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialogFragment.dismiss();
                                Toast.makeText(ProfileActivity.this, "Added car complete!", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }else{
                        Toast.makeText(ProfileActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }

    }

    private void showAddCarDialog(){
        dialogFragment = new AddCarDialog();
        dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogFragmentTheme);
        dialogFragment.show(getSupportFragmentManager(), "addcar");

    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = ProfileActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    //Check camera permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showAddCarDialog();

                } else {
                    Toast.makeText(this, "Camera permission denied.", Toast.LENGTH_SHORT).show();
                    new AlertDialog.Builder(this)
                            .setMessage("Please give a permission to add car")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", ProfileActivity.this.getPackageName(), null);
                                    intent.setData(uri);
                                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

                                }
                            })
                            .setNegativeButton("No", null)
                            .show();

                }
                return;
            }
        }
    }


}