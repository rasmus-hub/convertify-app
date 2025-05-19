package com.instamedia.convertify

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Configuración de anuncios
        val mAdView = findViewById<AdView>(R.id.ad_view_container)

        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        // Configuración de botones para convertir videos
        val buttonYoutubeConvert = findViewById<LinearLayout>(R.id.btn_convert_youtube)

        buttonYoutubeConvert.setOnClickListener {
            val intent = Intent(this, ConvertYoutubeActivity::class.java)
            startActivity(intent)
        }

        val buttonTiktokConvert = findViewById<LinearLayout>(R.id.btn_convert_tiktok)

        buttonTiktokConvert.setOnClickListener {
            val intent = Intent(this, ConvertTiktokActivity::class.java)
            startActivity(intent)
        }

        val buttonInstagramConvert = findViewById<LinearLayout>(R.id.btn_convert_instagram)

        buttonInstagramConvert.setOnClickListener {
            val intent = Intent(this, ConvertInstagramActivity::class.java)
            startActivity(intent)
        }

        val buttonFacebookConvert = findViewById<LinearLayout>(R.id.btn_convert_facebook)

        buttonFacebookConvert.setOnClickListener {
            val intent = Intent(this, ConvertFacebookActivity::class.java)
            startActivity(intent)
        }
    }
}