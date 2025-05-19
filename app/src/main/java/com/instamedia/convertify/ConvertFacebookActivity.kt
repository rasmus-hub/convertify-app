package com.instamedia.convertify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

class ConvertFacebookActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_convert_facebook)

        val mAdView1 = findViewById<AdView>(R.id.ad_view_container)

        val adRequest1 = AdRequest.Builder().build()
        mAdView1.loadAd(adRequest1)

        val mAdView2 = findViewById<AdView>(R.id.ad_view_container2)

        val adRequest2 = AdRequest.Builder().build()
        mAdView2.loadAd(adRequest2)

        val goToHome = findViewById<ImageView>(R.id.btn_go_home)

        goToHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}