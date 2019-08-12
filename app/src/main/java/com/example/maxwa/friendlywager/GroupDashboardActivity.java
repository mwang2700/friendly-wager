package com.example.maxwa.friendlywager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.maxwa.friendlywager.models.ViewWager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GroupDashboardActivity extends AppCompatActivity {
    private TextView mTextMessage;
    String username;
    String groupName;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_addtogroup:
                    Fragment addToGroupFragment = AddMembersFragment.newInstance();
                    openFragment(addToGroupFragment);
                    return true;
                case R.id.navigation_leaderboard:
                    Fragment leaderboardFragment = LeaderboardFragment.newInstance();
                    openFragment(leaderboardFragment);
                    return true;
                case R.id.navigation_addWager:
                    Fragment addWagerFragment = AddWagerFragment.newInstance();
                    openFragment(addWagerFragment);
                    return true;
                case R.id.navigation_viewWagers:
                    Fragment viewWagerFragment = ViewWagerFragment.newInstance();
                    openFragment(viewWagerFragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_dashboard);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        openFragment(HomeFragment.newInstance());
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(getIntent().getStringExtra("groupName"));
        Fragment addToGroupFragment = AddMembersFragment.newInstance();
        openFragment(addToGroupFragment);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void openFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.dashboard_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.dashboardHelp:
                Intent intent = new Intent(this, DashboardHelpActivity.class);
                startActivity(intent);
                break;
            case R.id.dashboardLeaveGroup:
                userLeaveGroup();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void userLeaveGroup() {
        groupName = getIntent().getStringExtra("groupName");
        DatabaseReference usernameRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid());
        usernameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username = (String) dataSnapshot.child("username").getValue();
                DatabaseReference root = FirebaseDatabase.getInstance().getReference();
                root.child(groupName).child(username).removeValue();
                root.child("bets").child("mlb").child(groupName).child(username).removeValue();
                root.child("bets").child("nfl").child(groupName).child(username).removeValue();
                root.child("bets").child("nba").child(groupName).child(username).removeValue();
                root.child("groups").child(groupName).child(mAuth.getUid()).removeValue();
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
