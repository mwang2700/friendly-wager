package com.maxwa.friendlywager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.maxwa.friendlywager.models.Friends;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {

    EditText emailEditText;
    EditText passwordEditText;
    EditText usernameEditText;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        emailEditText = findViewById(R.id.emailSignUpEditText);
        passwordEditText = findViewById(R.id.passwordSignUpEditText);
        usernameEditText = findViewById(R.id.usernameSignUpEditText);
    }

    public void signUp(View view) {
        if (emailEditText.getText().toString().equals("") || passwordEditText.getText().toString().equals("") || usernameEditText.getText().toString().equals("")) {
            Toast.makeText(this, "All fields must be entered.", Toast.LENGTH_SHORT).show();
        } else if (passwordEditText.getText().toString().length() < 8) {
            Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
        } else if (!emailEditText.getText().toString().contains("@")) {
            Toast.makeText(this, "Please enter a valid Email Address", Toast.LENGTH_SHORT).show();
        } else {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean exists = false;
                    for (DataSnapshot data: dataSnapshot.getChildren()) {
                        Friends friend = data.getValue(Friends.class);
                        if (friend.username.equals(usernameEditText.getText().toString())) {
                            exists = true;
                            Toast.makeText(SignUpActivity.this, "Username is already in use", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (!exists) {
                        mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success
                                            Toast.makeText(SignUpActivity.this, "Signup Success!", Toast.LENGTH_SHORT).show();
                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("users")
                                                    .child(task.getResult().getUser().getUid())
                                                    .child("email").setValue(emailEditText.getText().toString());
                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("users")
                                                    .child(task.getResult().getUser().getUid())
                                                    .child("username").setValue(usernameEditText.getText().toString());
                                            Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Toast.makeText(SignUpActivity.this, "Signup Failed. Either an account already exists under this email or something went wrong", Toast.LENGTH_LONG).show();
                                        }

                                        // ...
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(SignUpActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}
