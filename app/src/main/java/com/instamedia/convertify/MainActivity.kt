package com.instamedia.convertify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.MobileAdsInitProvider
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        setupButtons()
    }

    fun setupButtons() {
        // Boton para registro
        val buttonSignIn = findViewById<Button>(R.id.buttonSignIn)

        buttonSignIn.setOnClickListener {
            registerAccount()
        }

        // Boton para ir a login
        val buttonGoLogin = findViewById<Button>(R.id.buttonGoLogin)

        buttonGoLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    fun registerAccount() {
        val email = findViewById<EditText>(R.id.input_email)
        val password = findViewById<EditText>(R.id.input_password)

        if (email.text.length > 0 && password.text.length > 0) {
            auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("USER CREATE SUCCESS", "createUserWithEmail:success")
                        Toast.makeText(
                            baseContext,
                            "¡Welcome to Convertify!",
                            Toast.LENGTH_SHORT,
                        ).show()
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("USER CREATE FAILED", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        } else {
            Toast.makeText(
                baseContext,
                "Please, fill all the fields.",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }
}