package com.instamedia.convertify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

class ConvertYoutubeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_convert_youtube)

        // Configuración de vistas de anuncios
        val mAdView1 = findViewById<AdView>(R.id.ad_view_container)

        val adRequest1 = AdRequest.Builder().build()
        mAdView1.loadAd(adRequest1)

        val mAdView2 = findViewById<AdView>(R.id.ad_view_container2)

        val adRequest2 = AdRequest.Builder().build()
        mAdView2.loadAd(adRequest2)

        // Configuración de conversión

        // Botones - Conversión
        val buttonConvertFormat = findViewById<Button>(R.id.btn_convert_format)

        buttonConvertFormat.setOnClickListener {
            val text = buttonConvertFormat.text
            if (text == "MP3") buttonConvertFormat.setText("MP4") else buttonConvertFormat.setText("MP3")
        }

        // Configuración de botones footer
        val goToHome = findViewById<ImageView>(R.id.btn_go_home)

        goToHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}