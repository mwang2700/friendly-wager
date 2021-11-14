package com.maxwa.friendlywager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NewGroupActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ArrayList<String> existingGroups;
    EditText groupNameEditText;
    String username;
    String email;
    String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        groupNameEditText = findViewById(R.id.editText);
        existingGroups = getIntent().getStringArrayListExtra("existingGroups");
    }

    public void addGroup(View view) {
        boolean doesNotExist = true;
        groupName = groupNameEditText.getText().toString();
        for (int a = 0; a < existingGroups.size(); a++) {
            if (groupName.equals(existingGroups.get(a))) {
                Toast.makeText(this, "A group under that name already exists.", Toast.LENGTH_SHORT).show();
                doesNotExist = false;
            }
        }
        if (doesNotExist && !groupName.equals("friends") && !groupName.equals("groups") && !groupName.equals("requests") && !groupName.equals("users") && !groupName.equals("odds") || !groupName.equals("bets")) {
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid());
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        String val = (String) data.getValue();
                        if (!val.contains("@")) {
                            username = val;
                        } else {
                            email = val;
                        }
                    }
                    if (username != null) {
                        FirebaseDatabase.getInstance().getReference()
                                .child("groups")
                                .child(groupName)
                                .child(mAuth.getUid())
                                .child("email").setValue(email);
                        FirebaseDatabase.getInstance().getReference()
                                .child("groups")
                                .child(groupName)
                                .child(mAuth.getUid())
                                .child("username").setValue(username);
                        FirebaseDatabase.getInstance().getReference()
                                .child(groupName)
                                .child(username)
                                .child("points").setValue(100);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(NewGroupActivity.this, "Error getting Friends List", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (doesNotExist) {
            Toast.makeText(this, "Please use a different group name.", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelGroup(View view) {
        finish();
    }
}
