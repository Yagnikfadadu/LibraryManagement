package com.yagnikfadadu.librarymanagement;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.yagnikfadadu.librarymanagement.Fragments.MyBooksFragment;
import com.yagnikfadadu.librarymanagement.Fragments.HistoryFragment;
import com.yagnikfadadu.librarymanagement.Fragments.ProfileFragment;
import com.yagnikfadadu.librarymanagement.Fragments.SearchFragment;
import com.yagnikfadadu.librarymanagement.Fragments.WishListFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    SearchFragment searchFragment = new SearchFragment();
    WishListFragment wishListFragment = new WishListFragment();
    MyBooksFragment myBooksFragment = new MyBooksFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    HistoryFragment historyFragment = new HistoryFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).hide();

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
        bottomNavigationView.setSelectedItemId(R.id.search);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.search:
                getSupportFragmentManager().beginTransaction().replace(R.id.container,searchFragment).commit();
                return true;
            case R.id.wishlist:
                getSupportFragmentManager().beginTransaction().replace(R.id.container,wishListFragment).commit();
                return true;
            case R.id.profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.container,profileFragment).commit();
                return true;
            case R.id.mybooks:
                getSupportFragmentManager().beginTransaction().replace(R.id.container,myBooksFragment).commit();
                return true;
            case R.id.history:
                getSupportFragmentManager().beginTransaction().replace(R.id.container,historyFragment).commit();
                return true;
        }
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}