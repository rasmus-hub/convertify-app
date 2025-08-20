package com.instamedia.convertify

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        MobileAds.initialize(this)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

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
            //val intent = Intent(this, ConvertYoutubeActivity::class.java)
            //startActivity(intent)
        }

        val buttonTiktokConvert = findViewById<LinearLayout>(R.id.btn_convert_tiktok)

        buttonTiktokConvert.setOnClickListener {
            val intent = Intent(this, ConvertTiktokActivity::class.java)
            startActivity(intent)
        }

        val buttonInstagramConvert = findViewById<LinearLayout>(R.id.btn_convert_instagram)

        buttonInstagramConvert.setOnClickListener {
            Toast.makeText(baseContext, "Platform not available", Toast.LENGTH_SHORT).show()
            //val intent = Intent(this, ConvertInstagramActivity::class.java)
            //startActivity(intent)
        }

        val buttonFacebookConvert = findViewById<LinearLayout>(R.id.btn_convert_facebook)

        buttonFacebookConvert.setOnClickListener {
            Toast.makeText(baseContext, "Platform not available", Toast.LENGTH_SHORT).show()
            //val intent = Intent(this, ConvertFacebookActivity::class.java)
            //startActivity(intent)
        }
    }
}