package com.instamedia.convertify

import android.app.DownloadManager
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class ConvertTiktokActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private val apiUrl = "${BuildConfig.API_BASE_URL}/tiktok"
    private val apiKey = BuildConfig.API_KEY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_convert_tiktok)

        val mAdView1 = findViewById<AdView>(R.id.ad_view_container)
        val adRequest1 = AdRequest.Builder().build()
        mAdView1.loadAd(adRequest1)

        val mAdView2 = findViewById<AdView>(R.id.ad_view_container2)
        val adRequest2 = AdRequest.Builder().build()
        mAdView2.loadAd(adRequest2)

        val editTextUrl = findViewById<EditText>(R.id.editTextUrl)
        val buttonChangeFormat = findViewById<Button>(R.id.buttonChangeFormat)
        val convertBtn = findViewById<Button>(R.id.buttonConvert)

        buttonChangeFormat.setOnClickListener {
            if (buttonChangeFormat.text == "MP4") {
                buttonChangeFormat.text = "MP3"
            } else {
                buttonChangeFormat.text = "MP4"
            }
        }

        convertBtn.setOnClickListener {
            val urlText = editTextUrl.text.toString()
            if (urlText.equals("")) {
                Toast.makeText(this, "Please, enter a valid URL", Toast.LENGTH_SHORT).show()
            } else {
                enviarUrlAlServidor(urlText)
            }
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        // Botón Home
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
            Toast.makeText(baseContext, "Settings section not available yet", Toast.LENGTH_SHORT).show()
            // val intent = Intent(this, SettingsActivity::class.java)
            // startActivity(intent)
        }
    }

    private fun enviarUrlAlServidor(tiktokUrl: String) {
        val json = JSONObject()
        json.put("url", tiktokUrl)

        val body = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())
        val request = Request.Builder()
            .url(apiUrl)
            .addHeader("x-api-key", apiKey)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@ConvertTiktokActivity, "Connection with server failed, try again", Toast.LENGTH_SHORT).show()
                }
                Log.e("Convertify", "Error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    val jsonResponse = JSONObject(responseBody)
                    val videoUrl = jsonResponse.optString("video_url", "")
                    val title = jsonResponse.optString("title", "")
                    val author = jsonResponse.optString("author", "")
                    val textVideoTitle = findViewById<TextView>(R.id.textVideoTitle)
                    val buttonSelectedFormat = findViewById<Button>(R.id.buttonChangeFormat)

                    if (videoUrl != null) {
                        runOnUiThread {
                            textVideoTitle.text = "Video title: $title"

                            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                            val uri = Uri.parse(videoUrl)

                            val isMp4  = buttonSelectedFormat.text == "MP4"
                            val format = if (isMp4) "mp4" else "mp3"
                            val mimeType = "video/$format"
                            val fileName = if (isMp4) "video_convertify_tiktok_${author}_${System.currentTimeMillis()}.$format"
                                else "audio_convertify_tiktok_${author}_${System.currentTimeMillis()}.$format"

                            val request = DownloadManager.Request(uri)
                                .setTitle("Downloading resource")
                                .setDescription(title)
                                .setMimeType(mimeType)
                                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                                .setAllowedOverMetered(true)
                                .setAllowedOverRoaming(true)

                            val downloadId = downloadManager.enqueue(request)

                            // Mostrar barra de progreso
                            val progressBar = findViewById<ProgressBar>(R.id.progressBar)
                            progressBar.progress = 0
                            progressBar.max = 100
                            progressBar.visibility = View.VISIBLE

                            Thread {
                                var downloading = true
                                while (downloading) {
                                    val query = DownloadManager.Query().setFilterById(downloadId)
                                    val cursor = downloadManager.query(query)

                                    if (cursor != null && cursor.moveToFirst()) {
                                        val bytesDownloaded = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                                        val bytesTotal = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

                                        if (bytesTotal > 0) {
                                            val progress = ((bytesDownloaded * 100L) / bytesTotal).toInt()

                                            runOnUiThread {
                                                progressBar.progress = progress
                                            }
                                        }

                                        val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                                        if (status == DownloadManager.STATUS_SUCCESSFUL || status == DownloadManager.STATUS_FAILED) {
                                            downloading = false
                                            runOnUiThread {
                                                progressBar.visibility = View.GONE
                                                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                                    Toast.makeText(baseContext, "Download complete", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    Toast.makeText(baseContext, "Download error", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    }
                                    cursor?.close()
                                    Thread.sleep(100)
                                }
                            }.start()

                            runOnUiThread {
                                Toast.makeText(this@ConvertTiktokActivity, "Download started", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@ConvertTiktokActivity, "No video found", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@ConvertTiktokActivity, "Invalid server response, try again", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}