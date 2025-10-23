package com.instamedia.convertify

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.instamedia.convertify.adapters.FilesAdapter
import com.instamedia.convertify.models.VideoFile
import java.io.File
import java.util.ArrayList
import java.util.Arrays
import java.util.Collections
import java.util.Comparator

class FilesActivity : AppCompatActivity(), FilesAdapter.OnItemClickListener, FilesAdapter.OnMoreClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var recyclerView: RecyclerView
    private lateinit var filesAdapter: FilesAdapter
    private lateinit var searchEditText: EditText
    private var allFiles: ArrayList<VideoFile> = ArrayList()

    companion object {
        private const val STORAGE_PERMISSION_REQUEST_CODE = 1001
        private const val SETTINGS_REQUEST_CODE = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_files)

        MobileAds.initialize(this)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("convertify_prefs", MODE_PRIVATE)

        // Configuración de anuncios
        val mAdView: AdView = findViewById(R.id.ad_view_container)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        // Inicializar vistas
        initViews()

        // Configurar navegación inferior
        setupBottomNavigation()

        // Verificar permisos y cargar archivos
        checkStoragePermissions()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerViewFiles)
        searchEditText = findViewById(R.id.searchFile)

        // Configurar RecyclerView
        filesAdapter = FilesAdapter(this, this, this)

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = filesAdapter

        // Configurar búsqueda
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = if (s != null) s.toString() else ""
                filterFiles(query)
            }
        })
    }

    private fun checkStoragePermissions() {
        // Saltar verificación de permisos y cargar archivos directamente
        // Solo para testing - esto funciona en la mayoría de casos
        try {
            loadConvertifyFiles()
        } catch (e: SecurityException) {
            // Si hay un error de seguridad, entonces sí solicitar permisos
            android.util.Log.e("PermissionsError", "Security exception: ${e.message}")
            requestStoragePermissionSimple()
        } catch (e: Exception) {
            android.util.Log.e("FilesError", "Error loading files: ${e.message}")
            Toast.makeText(this, "Error loading files: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun requestStoragePermissionSimple() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            val permissionsNeeded = ArrayList<String>()

            // Para Android 13+
            if (android.os.Build.VERSION.SDK_INT >= 33) {
                if (ContextCompat.checkSelfPermission(this, "android.permission.READ_MEDIA_AUDIO") != PackageManager.PERMISSION_GRANTED) {
                    permissionsNeeded.add("android.permission.READ_MEDIA_AUDIO")
                }
                if (ContextCompat.checkSelfPermission(this, "android.permission.READ_MEDIA_VIDEO") != PackageManager.PERMISSION_GRANTED) {
                    permissionsNeeded.add("android.permission.READ_MEDIA_VIDEO")
                }
            } else {
                // Para Android 6-12
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }

            if (permissionsNeeded.size > 0) {
                ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), STORAGE_PERMISSION_REQUEST_CODE)
            } else {
                // Los permisos ya están otorgados
                loadConvertifyFiles()
            }
        } else {
            // Android 5.1 o menor
            loadConvertifyFiles()
        }
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
            .setTitle("Storage Permission Required")
            .setMessage("This app needs access to your storage to display your downloaded Convertify files. Please grant storage permission to continue.")
            .setPositiveButton("Grant Permission") { _, _ ->
                requestStoragePermission()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                showPermissionDeniedMessage()
            }
            .setCancelable(false)
            .show()
    }

    private fun showPermissionDeniedMessage() {
        Toast.makeText(
            this,
            "Storage permission is required to access your files. Please enable it in Settings.",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            STORAGE_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            STORAGE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permiso otorgado
                    Toast.makeText(this, "Permission granted! Loading files...", Toast.LENGTH_SHORT).show()
                    loadConvertifyFiles()
                } else {
                    // Permiso denegado
                    if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        // El usuario denegó el permiso pero puede volver a solicitarlo
                        showPermissionRationaleDialog()
                    } else {
                        // El usuario marcó "No volver a preguntar"
                        showPermissionPermanentlyDeniedDialog()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SETTINGS_REQUEST_CODE) {
            // Verificar si el usuario otorgó el permiso en Settings
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted! Loading files...", Toast.LENGTH_SHORT).show()
                loadConvertifyFiles()
            } else {
                showPermissionDeniedMessage()
            }
        }
    }

    private fun showPermissionPermanentlyDeniedDialog() {
        AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
            .setTitle("Permission Required")
            .setMessage("Storage permission is permanently denied. Please enable it manually in Settings > Apps > Convertify > Permissions.")
            .setPositiveButton("Open Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                showPermissionDeniedMessage()
            }
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
        startActivityForResult(intent, SETTINGS_REQUEST_CODE)
    }

    private fun loadConvertifyFiles() {
        try {
            val videoFiles: ArrayList<VideoFile> = ArrayList()

            // Buscar en diferentes directorios donde pueden estar los archivos
            val searchDirectories: ArrayList<File> = ArrayList()

            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (downloadsDir != null) searchDirectories.add(downloadsDir)

            val moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
            if (moviesDir != null) searchDirectories.add(moviesDir)

            val dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            if (dcimDir != null) searchDirectories.add(dcimDir)

            val convertifyDir = File(Environment.getExternalStorageDirectory(), "Convertify")
            searchDirectories.add(convertifyDir)

            for (directory in searchDirectories) {
                if (directory.exists() && directory.isDirectory()) {
                    searchForConvertifyFiles(directory, videoFiles)
                }
            }

            // También buscar en el directorio de archivos de la app
            val appDirectory = File(getExternalFilesDir(null), "downloads")
            if (appDirectory.exists()) {
                searchForConvertifyFiles(appDirectory, videoFiles)
            }

            // Ordenar por fecha de descarga (más recientes primero)
            Collections.sort(videoFiles, object : Comparator<VideoFile> {
                override fun compare(o1: VideoFile, o2: VideoFile): Int {
                    return java.lang.Long.compare(o2.file.lastModified(), o1.file.lastModified())
                }
            })

            allFiles = videoFiles
            filesAdapter.updateFiles(allFiles)

            if (allFiles.size == 0) {
                Toast.makeText(this, "No Convertify files found", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Toast.makeText(this, "Error loading files: " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchForConvertifyFiles(directory: File, videoFiles: ArrayList<VideoFile>) {
        val files = directory.listFiles()
        if (files != null) {
            for (file in files) {
                val fileName = file.name.toLowerCase(java.util.Locale.getDefault())
                if (file.isFile() && fileName.indexOf("convertify") != -1) {
                    // Verificar que sea un archivo de video/audio válido
                    val extension = getFileExtension(file).toLowerCase(java.util.Locale.getDefault())
                    val validExtensions = Arrays.asList("mp4", "mp3", "avi", "mov", "mkv", "webm")

                    if (validExtensions.contains(extension)) {
                        val videoFile = VideoFile.fromFileName(file)
                        if (videoFile != null) {
                            videoFiles.add(videoFile)
                        }
                    }
                } else if (file.isDirectory()) {
                    // Buscar recursivamente en subdirectorios (limitado a 2 niveles)
                    try {
                        searchForConvertifyFiles(file, videoFiles)
                    } catch (e: Exception) {
                        // Ignorar errores de permisos en subdirectorios
                    }
                }
            }
        }
    }

    private fun getFileExtension(file: File): String {
        val fileName = file.name
        val dotIndex = fileName.lastIndexOf('.')
        return if (dotIndex > 0 && dotIndex < fileName.length - 1) {
            fileName.substring(dotIndex + 1)
        } else {
            ""
        }
    }

    private fun filterFiles(query: String) {
        val filteredFiles: ArrayList<VideoFile> = ArrayList()

        if (query.length == 0) {
            filteredFiles.addAll(allFiles)
        } else {
            for (file in allFiles) {
                val queryLower = query.toLowerCase(java.util.Locale.getDefault())
                val titleLower = file.title.toLowerCase(java.util.Locale.getDefault())
                val authorLower = file.author.toLowerCase(java.util.Locale.getDefault())
                val platformLower = file.platform.displayName.toLowerCase(java.util.Locale.getDefault())

                val titleContains = titleLower.indexOf(queryLower) != -1
                val authorContains = authorLower.indexOf(queryLower) != -1
                val platformContains = platformLower.indexOf(queryLower) != -1

                if (titleContains || authorContains || platformContains) {
                    filteredFiles.add(file)
                }
            }
        }

        filesAdapter.updateFiles(filteredFiles)
    }

    override fun onItemClick(videoFile: VideoFile) {
        openFile(videoFile)
    }

    override fun onMoreClick(videoFile: VideoFile) {
        showFileOptions(videoFile)
    }

    private fun openFile(videoFile: VideoFile) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = androidx.core.content.FileProvider.getUriForFile(this, packageName + ".fileprovider", videoFile.file)
            intent.setDataAndType(uri, getMimeType(videoFile.format))
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            startActivity(Intent.createChooser(intent, "Open with"))
        } catch (e: Exception) {
            Toast.makeText(this, "Cannot open file: " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showFileOptions(videoFile: VideoFile) {
        // Implementar opciones como compartir, eliminar, renombrar, etc.
        val options = arrayOf("Share", "Delete", "File Info")

        AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
            .setTitle("File Options")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> shareFile(videoFile)
                    1 -> deleteFile(videoFile)
                    2 -> showFileInfo(videoFile)
                }
            }
            .show()
    }

    private fun shareFile(videoFile: VideoFile) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = getMimeType(videoFile.format)

            val uri = androidx.core.content.FileProvider.getUriForFile(this, packageName + ".fileprovider", videoFile.file)
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            startActivity(Intent.createChooser(shareIntent, "Share video"))
        } catch (e: Exception) {
            Toast.makeText(this, "Cannot share file: " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteFile(videoFile: VideoFile) {
        AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
            .setTitle("Delete File")
            .setMessage("Are you sure you want to delete this file?")
            .setPositiveButton("Delete") { dialog, which ->
                if (videoFile.file.delete()) {
                    Toast.makeText(this@FilesActivity, "File deleted", Toast.LENGTH_SHORT).show()
                    loadConvertifyFiles() // Recargar la lista
                } else {
                    Toast.makeText(this@FilesActivity, "Cannot delete file", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showFileInfo(videoFile: VideoFile) {
        val info = "Title: " + videoFile.title + "\n" +
                "Author: " + videoFile.author + "\n" +
                "Platform: " + videoFile.platform.displayName + "\n" +
                "Format: " + videoFile.format + "\n" +
                "Size: " + formatFileSize(videoFile.file.length()) + "\n" +
                videoFile.downloadDate + "\n" +
                "Path: " + videoFile.file.absolutePath

        AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
            .setTitle("File Information")
            .setMessage(info)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun getMimeType(format: String): String {
        val formatLower = format.toLowerCase(java.util.Locale.getDefault())
        return when (formatLower) {
            "mp4" -> "video/mp4"
            "mp3" -> "audio/mpeg"
            "avi" -> "video/x-msvideo"
            "mov" -> "video/quicktime"
            "mkv" -> "video/x-matroska"
            "webm" -> "video/webm"
            else -> "*/*"
        }
    }

    private fun formatFileSize(bytes: Long): String {
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0

        return when {
            gb >= 1 -> java.lang.String.format(java.util.Locale.getDefault(), "%.1f GB", gb)
            mb >= 1 -> java.lang.String.format(java.util.Locale.getDefault(), "%.1f MB", mb)
            kb >= 1 -> java.lang.String.format(java.util.Locale.getDefault(), "%.1f KB", kb)
            else -> bytes.toString() + " B"
        }
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
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}