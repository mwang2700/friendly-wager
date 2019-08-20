package com.maxwa.friendlywager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    internal lateinit var emailEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        emailEditText = findViewById(R.id.emailResetEditText)
    }

    fun sendReset(view: View) {
        val auth = FirebaseAuth.getInstance()
        val emailAddress = emailEditText.text.toString();

        if (emailAddress.length == 0 || emailEditText.text.toString().indexOf('@') == -1) {
            Toast.makeText(this, "Please enter an Email Address", Toast.LENGTH_SHORT).show()
        } else {
            auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Reset E-mail sent. Please check your email address.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Did not recognize that email address.", Toast.LENGTH_SHORT).show()
                        }
                    }
        }
    }
}
