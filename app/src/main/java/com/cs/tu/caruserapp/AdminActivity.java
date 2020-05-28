package com.cs.tu.caruserapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cs.tu.caruserapp.Fragments.ChatsFragment;
import com.cs.tu.caruserapp.Fragments.VerifyListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView btmView;
    VerifyListFragment verifyListFragment = new VerifyListFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        btmView = findViewById(R.id.bottom_navigation);
        btmView.setOnNavigationItemSelectedListener(this);
        btmView.setSelectedItemId(R.id.page_1);

    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.page_1:
                Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.page_2:
                Toast.makeText(this, "2", Toast.LENGTH_SHORT).show();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, verifyListFragment).commit();
                return true;
        }
        return false;
    }

}
