package com.cs.tu.caruserapp.Dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;


import com.cs.tu.caruserapp.MainActivity;
import com.cs.tu.caruserapp.Model.Car;
import com.cs.tu.caruserapp.R;

import com.cs.tu.caruserapp.StartActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class AddCarDialog extends DialogFragment {

    //interface for send input to ProfileActivity
    public interface OnInputListener{
        void sendInput(Uri imageUri, String input_carid, String input_province, String input_brand, String input_model, int input_color);
    }
    public OnInputListener mOnInputListener;

    CircleImageView car_photo;
    TextView btn_cancel;
    TextView btn_add;
    Button btn_add_car_photo;
    EditText edt_carid;
    Spinner province_spinner;
    Spinner brand_spinner;
    EditText edt_model;
    Spinner color_spinner;

    private static final int REQUEST_IMAGE_CAPTURE = 0;
    private static final int GALLERY_REQUEST = 1;
    private Uri imageUri;
    String currentPhotoPath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_car_dialog, container,false);

        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        car_photo = view.findViewById(R.id.car_photo);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_add = view.findViewById(R.id.btn_add);
        edt_carid = view.findViewById(R.id.edt_car_id);
        btn_add_car_photo = view.findViewById(R.id.btn_add_car_photo);

        province_spinner = view.findViewById(R.id.province_spinner);
        String[] province_array = getResources().getStringArray(R.array.province_arrays);
        final ArrayAdapter<String> province_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, province_array);
        province_spinner.setAdapter(province_adapter);

        brand_spinner = view.findViewById(R.id.brand_spinner);
        String[] cars_array = getResources().getStringArray(R.array.cars_array);
        final ArrayAdapter<String> cars_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, cars_array);
        brand_spinner.setAdapter(cars_adapter);

        edt_model = view.findViewById(R.id.edt_model);

        color_spinner = view.findViewById(R.id.color_spinner);
        String[] color_array = getResources().getStringArray(R.array.color_arrays);
        final ArrayAdapter<String> color_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, color_array);
        color_spinner.setAdapter(color_adapter);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        btn_add_car_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();

            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String input_carid = edt_carid.getText().toString();
                final String input_province = province_spinner.getSelectedItem().toString();
                final String input_brand = brand_spinner.getSelectedItem().toString();
                final String input_model = edt_model.getText().toString();
                final int input_color = color_spinner.getSelectedItemPosition();

                if(imageUri != null && !input_carid.equals("") && province_spinner.getSelectedItemPosition() != 0 && brand_spinner.getSelectedItemPosition() != 0
                        && !input_model.equals("") && color_spinner.getSelectedItemPosition() != 0) {
                    Query query = FirebaseDatabase.getInstance().getReference("Cars").orderByChild("car_id").equalTo(input_carid);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean exist_car = false;
                            //check if car id is not exist
                            if(!dataSnapshot.exists()){
                                mOnInputListener.sendInput(imageUri, input_carid, input_province, input_brand, input_model, input_color);
                                getDialog().dismiss();


                            }else{
                                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    final Car car = snapshot.getValue(Car.class);
                                    assert car != null;
                                    if(car.getProvince().equals(input_province)){
                                        exist_car = true;

                                    }
                                }
                                if(!exist_car){
                                    mOnInputListener.sendInput(imageUri, input_carid, input_province, input_brand, input_model, input_color);
                                    getDialog().dismiss();
                                }else{
                                    Toast.makeText(getActivity(), getString(R.string.this_car_already_regist), Toast.LENGTH_SHORT).show();
                                    new android.app.AlertDialog.Builder(getActivity())
                                            .setTitle(getString(R.string.this_car_already_regist))
                                            .setMessage(getString(R.string.report_if_your_car))
                                            .setPositiveButton(getString(R.string.report), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Toast.makeText(getActivity(), "Report", Toast.LENGTH_SHORT).show();

                                                }
                                            })
                                            .setNegativeButton(getString(R.string.cancel), null)
                                            .show();
                                }

                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("", databaseError.getMessage());
                        }
                    });


                }else{
                    if(imageUri == null){
                        Toast.makeText(getActivity(), getString(R.string.no_image_select), Toast.LENGTH_SHORT).show();
                    } else if(input_carid.equals("") || input_brand.equals("") || input_model.equals("")){
                        Toast.makeText(getActivity(), getString(R.string.please_fill_up_detail), Toast.LENGTH_SHORT).show();

                    } else if(province_spinner.getSelectedItemPosition() == 0) {
                        Toast.makeText(getActivity(), getString(R.string.please_select_province), Toast.LENGTH_SHORT).show();
                    } else if(brand_spinner.getSelectedItemPosition() == 0){
                        Toast.makeText(getActivity(), getString(R.string.please_select_brand), Toast.LENGTH_SHORT).show();
                    } else if(color_spinner.getSelectedItemPosition() == 0){
                        Toast.makeText(getActivity(), getString(R.string.please_select_color), Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            mOnInputListener = (OnInputListener) getActivity();
        }catch (ClassCastException e){
            Log.e("", "onAttach: ClassCastException: " + e.getMessage());
        }
    }

    private void openImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.add_car_photo));

        String[] choices = getResources().getStringArray(R.array.take_photo_choice);
        builder.setItems(choices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                            // Create the File where the photo should go
                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                            } catch (IOException ex) {

                            }
                            // Continue only if the File was successfully created
                            if (photoFile != null) {
                                Uri photoURI = FileProvider.getUriForFile(getActivity(), "com.cs.tu.caruserapp", photoFile);
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                            }

                        }
                        break;

                    case 1:
                        Intent pictureActionIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        if (pictureActionIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivityForResult(pictureActionIntent, GALLERY_REQUEST);
                        }
                        break;
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            File file = new File(currentPhotoPath);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.fromFile(file));
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


            imageUri = convertBitmapToUri(getActivity(), rotatedBitmap);
            car_photo.setImageURI(imageUri);

        }

        if(requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            car_photo.setImageURI(imageUri);

        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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

}