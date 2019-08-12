package com.example.maxwa.friendlywager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeScreenActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }
    private void openFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId())
        {
            case R.id.action_home:
                //do stuff
                Toast.makeText(HomeScreenActivity.this, "Home clicked", Toast.LENGTH_SHORT).show();
                Fragment homeFragment = HomeFragment.newInstance();
                openFragment(homeFragment);
                break;
            case R.id.action_group:
                //do stuff
                Toast.makeText(HomeScreenActivity.this, "Edit clicked", Toast.LENGTH_SHORT).show();
                Fragment groupsFragment = GroupsFragment.newInstance();
                openFragment(groupsFragment);
                break;
            case R.id.action_friends:
                //do stuff
                Toast.makeText(HomeScreenActivity.this, "Remove clicked", Toast.LENGTH_SHORT).show();
                Fragment friendsFragment = FriendsFragment.newInstance();
                openFragment(friendsFragment);
                break;
        }
        return true;
    }
}
