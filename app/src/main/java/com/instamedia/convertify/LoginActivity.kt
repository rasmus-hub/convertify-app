package com.instamedia.convertify

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("convertify_prefs", MODE_PRIVATE)

        // Cargar credenciales guardadas si existen
        loadSavedCredentials()

        setupButtons()
    }

    private fun loadSavedCredentials() {
        val savedEmail = sharedPreferences.getString("saved_email", "")
        val savedPassword = sharedPreferences.getString("saved_password", "")
        val rememberMe = sharedPreferences.getBoolean("remember_me", false)

        if (rememberMe && savedEmail === null || savedEmail == "") {
            findViewById<EditText>(R.id.input_email).setText(savedEmail)
            findViewById<EditText>(R.id.input_password).setText(savedPassword)
            findViewById<CheckBox>(R.id.checkbox_remember_me).isChecked = true
        }
    }

    fun setupButtons() {
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)

        buttonLogin.setOnClickListener {
            loginAccount()
        }

        val buttonGoSignIn = findViewById<Button>(R.id.buttonGoSignIn)

        buttonGoSignIn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    fun loginAccount() {
        val emailInput = findViewById<EditText>(R.id.input_email)
        val passwordInput = findViewById<EditText>(R.id.input_password)
        val rememberMeCheckbox = findViewById<CheckBox>(R.id.checkbox_remember_me)

        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()

        if (email.length > 0 && password.length > 0) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("USER LOGIN SUCCESS", "signInWithEmail:success")

                        // Guardar estado de login
                        val editor = sharedPreferences.edit()
                        editor.putBoolean("is_logged_in", true)

                        // Guardar credenciales si el usuario marcó "Recordarme"
                        if (rememberMeCheckbox.isChecked) {
                            editor.putString("saved_email", email)
                            editor.putString("saved_password", password)
                            editor.putBoolean("remember_me", true)
                        } else {
                            // Limpiar credenciales guardadas si no quiere recordarlas
                            editor.remove("saved_email")
                            editor.remove("saved_password")
                            editor.putBoolean("remember_me", false)
                        }

                        editor.apply()

                        Toast.makeText(baseContext, "¡Welcome to Convertify!", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finishAffinity() // Cierra todas las actividades anteriores

                    } else {
                        Log.w("USER LOGIN FAILED", "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(baseContext, "Please, fill all the fields.", Toast.LENGTH_SHORT).show()
        }
    }
}