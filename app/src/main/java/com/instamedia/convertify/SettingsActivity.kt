package com.instamedia.convertify

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        MobileAds.initialize(this)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("convertify_prefs", MODE_PRIVATE)

        // Configuración de anuncios
        val mAdView = findViewById<AdView>(R.id.ad_view_container)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        // Botón Home (ya estamos en Home, no hacer nada o refrescar)
        val btnGoHome = findViewById<ImageView>(R.id.btn_go_home)
        btnGoHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        // Botón Perfil
        val btnGoProfile = findViewById<ImageView>(R.id.btn_go_profile)
        btnGoProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Botón Archivos
        val btnGoFiles = findViewById<ImageView>(R.id.btn_go_files)
        btnGoFiles.setOnClickListener {
            val intent = Intent(this, FilesActivity::class.java)
            startActivity(intent)
        }

        // Botón Configuración
        val btnGoSettings = findViewById<ImageView>(R.id.btn_go_settings)
        btnGoSettings.setOnClickListener {
            // Ya estamos en Settings
        }
    }
}