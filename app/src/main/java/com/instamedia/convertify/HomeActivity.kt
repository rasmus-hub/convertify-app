package com.instamedia.convertify

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        MobileAds.initialize(this)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("convertify_prefs", MODE_PRIVATE)

        // Configuración de anuncios
        val mAdView = findViewById<AdView>(R.id.ad_view_container)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        setupButtons()
    }

    fun setupButtons() {
        // Configuración de botones para convertir videos
        val buttonYoutubeConvert = findViewById<LinearLayout>(R.id.btn_convert_youtube)
        buttonYoutubeConvert.setOnClickListener {
            Toast.makeText(baseContext, "Platform not available", Toast.LENGTH_SHORT).show()
        }

        val buttonTiktokConvert = findViewById<LinearLayout>(R.id.btn_convert_tiktok)
        buttonTiktokConvert.setOnClickListener {
            val intent = Intent(this, ConvertTiktokActivity::class.java)
            startActivity(intent)
        }

        val buttonInstagramConvert = findViewById<LinearLayout>(R.id.btn_convert_instagram)
        buttonInstagramConvert.setOnClickListener {
            Toast.makeText(baseContext, "Platform not available", Toast.LENGTH_SHORT).show()
        }

        val buttonFacebookConvert = findViewById<LinearLayout>(R.id.btn_convert_facebook)
        buttonFacebookConvert.setOnClickListener {
            Toast.makeText(baseContext, "Platform not available", Toast.LENGTH_SHORT).show()
        }

        // Configurar navegación inferior
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        // Botón Home (ya estamos en Home, no hacer nada o refrescar)
        val btnGoHome = findViewById<ImageView>(R.id.btn_go_home)
        btnGoHome.setOnClickListener {
            // Ya estamos en Home
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
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Prevenir que el usuario regrese al login usando el botón atrás
        // En su lugar, minimizar la app
        moveTaskToBack(true)
    }
}