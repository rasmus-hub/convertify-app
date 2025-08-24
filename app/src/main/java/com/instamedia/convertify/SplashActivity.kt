package com.instamedia.convertify

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash) // Crea un layout simple con tu logo

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("convertify_prefs", MODE_PRIVATE)

        // Initialize MobileAds
        MobileAds.initialize(this)

        // Verificar estado de autenticación después de un breve delay
        Handler(Looper.getMainLooper()).postDelayed({
            checkUserAuthentication()
        }, 2000) // 2 segundos de splash screen
    }

    private fun checkUserAuthentication() {
        val currentUser = auth.currentUser
        val isUserLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

        if (currentUser != null && isUserLoggedIn) {
            // Usuario ya está logueado, ir directo al Home
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // Usuario no está logueado, ir al registro/login
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}