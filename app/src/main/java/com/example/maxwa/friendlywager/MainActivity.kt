package com.example.maxwa.friendlywager

import androidx.appcompat.app.AppCompatActivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    internal lateinit var emailEditText: EditText
    internal lateinit var passwordEditText: EditText
    internal lateinit var logInButton: Button
    internal lateinit var signUpButton: Button
    internal lateinit var forgotPasswordButton: Button
    internal var mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        emailEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        logInButton = findViewById(R.id.logInButton)
        signUpButton = findViewById(R.id.signUpButton)
        forgotPasswordButton = findViewById(R.id.forgotPasswordButton)

        if (mAuth.currentUser != null) {
            Log.i("Entered here", "in if")
            logIn()
        }
    }

    fun logInPressed(view: View) {
        if (emailEditText.text.toString() == "" || passwordEditText.text.toString() == "") {
            Toast.makeText(this, "Email and Password fields cannot be blank.", Toast.LENGTH_SHORT).show()
        } else if (passwordEditText.toString().length < 8) {
            Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
        } else if (emailEditText.text.toString().indexOf('@') == -1) {
            Toast.makeText(this, "Please enter an Email Address", Toast.LENGTH_SHORT).show()
        } else {
            mAuth.signInWithEmailAndPassword(emailEditText.text.toString(), passwordEditText.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            logIn()
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(this@MainActivity, "Invalid Email Address or Incorrect Password",
                                    Toast.LENGTH_SHORT).show()

                        }

                        // ...
                    }
        }

    }

    fun logIn() {
        val intent = Intent(this@MainActivity, HomeActivity::class.java)
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent)
    }

    fun toSignUp(view: View) {
        val intent = Intent(this@MainActivity, SignUpActivity::class.java)
        startActivity(intent)
    }
}
