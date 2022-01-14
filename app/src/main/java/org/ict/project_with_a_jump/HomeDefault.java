package org.ict.project_with_a_jump;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeDefault extends AppCompatActivity {
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private final HomeScreen homeFragment = new HomeScreen();
    private final History historyFragment = new History();
    private final PersonalInfo infoFragment = new PersonalInfo();
    private final ChangeOfficeHour workFragment = new ChangeOfficeHour();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startActivity(new Intent(getApplicationContext(), Loading.class));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_default);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, homeFragment).commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelected());
    }


    class ItemSelected implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch (item.getItemId()) {
                case R.id.home:
                    transaction.replace(R.id.frameLayout, homeFragment).commit();
                    return true;
                case R.id.history:
                    transaction.replace(R.id.frameLayout, historyFragment).commit();
                    return true;
                case R.id.info:
                    transaction.replace(R.id.frameLayout, infoFragment).commit();
                    return true;
                case R.id.work:
                    transaction.replace(R.id.frameLayout, workFragment).commit();
                    return true;
            }
            return false;
        }
    }
}