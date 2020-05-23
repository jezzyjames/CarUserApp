package com.cs.tu.caruserapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;


public class VerifyActivity extends AppCompatActivity {
    Intent intent;

    String my_car_id;
    String my_car_province;

    TextView txt_method;
    CardView card_regist_book;
    CardView card_regist_photo;
    Button btn_choose_book;
    Button btn_choose_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        txt_method = findViewById(R.id.txt_method);
        card_regist_book = findViewById(R.id.card_regist_book);
        card_regist_photo = findViewById(R.id.card_regist_photo);
        btn_choose_book = findViewById(R.id.btn_choose_book);
        btn_choose_photo = findViewById(R.id.btn_choose_photo);

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

        intent = getIntent();
        my_car_id = intent.getStringExtra("my_car_id");
        my_car_province = intent.getStringExtra("my_car_province");

        btn_choose_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_method.setText(getString(R.string.by_regist_book));
                card_regist_book.setVisibility(View.GONE);
                card_regist_photo.setVisibility(View.GONE);
            }
        });

        btn_choose_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_method.setText(getString(R.string.by_car_photo));
                card_regist_book.setVisibility(View.GONE);
                card_regist_photo.setVisibility(View.GONE);
            }
        });

    }

}
