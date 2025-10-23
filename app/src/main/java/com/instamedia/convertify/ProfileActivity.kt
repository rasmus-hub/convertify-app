package com.instamedia.convertify

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("convertify_prefs", MODE_PRIVATE)

        // Verificar si el usuario está logueado
        currentUser = auth.currentUser
        if (currentUser == null) {
            redirectToLogin()
            return
        }

        // Configurar anuncios
        setupAds()

        // Configurar la interfaz
        setupUI()

        // Configurar botones
        setupButtons()
    }

    private fun setupAds() {
        val mAdView = findViewById<AdView>(R.id.ad_view_container)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        val mAdView2 = findViewById<AdView>(R.id.ad_view_container2)
        val adRequest2 = AdRequest.Builder().build()
        mAdView2.loadAd(adRequest2)
    }

    private fun setupUI() {
        // Mostrar información del usuario
        val textUserEmail = findViewById<TextView>(R.id.textUserEmail)
        val textUserInfo = findViewById<TextView>(R.id.textUserInfo)

        textUserEmail.text = currentUser?.email ?: "No email available"

        val creationTime = currentUser?.metadata?.creationTimestamp
        val lastSignIn = currentUser?.metadata?.lastSignInTimestamp

        if (creationTime != null) {
            val creationDate = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                .format(java.util.Date(creationTime))
            textUserInfo.text = "Account created: $creationDate"
        }
    }

    private fun setupButtons() {
        // Botón de regresar
        val btnGoHome = findViewById<ImageView>(R.id.btn_go_home)
        btnGoHome.setOnClickListener {
            finish() // Regresar a la actividad anterior (Home)
        }

        // Botón de cambiar contraseña
        val btnChangePassword = findViewById<Button>(R.id.buttonChangePassword)
        btnChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }

        // Botón de cerrar sesión
        val btnLogout = findViewById<Button>(R.id.buttonLogout)
        btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }

        // Configurar navegación inferior (opcional)
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
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val editCurrentPassword = dialogView.findViewById<EditText>(R.id.editCurrentPassword)
        val editNewPassword = dialogView.findViewById<EditText>(R.id.editNewPassword)
        val editConfirmPassword = dialogView.findViewById<EditText>(R.id.editConfirmPassword)

        AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
            .setTitle("Change Password")
            .setView(dialogView)
            .setPositiveButton("Change") { _, _ ->
                val currentPassword = editCurrentPassword.text.toString()
                val newPassword = editNewPassword.text.toString()
                val confirmPassword = editConfirmPassword.text.toString()

                changePassword(currentPassword, newPassword, confirmPassword)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun changePassword(currentPassword: String, newPassword: String, confirmPassword: String) {
        // Validaciones
        if (currentPassword == "" || newPassword == "" || confirmPassword == "") {
            Toast.makeText(this, "Please, fill all the fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword != confirmPassword) {
            Toast.makeText(this, "Passwords need to be the same", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword.length < 6) {
            Toast.makeText(this, "The password must be at least 6 characters long.", Toast.LENGTH_SHORT).show()
            return
        }

        // Re-autenticar al usuario
        val credential = EmailAuthProvider.getCredential(currentUser?.email!!, currentPassword)
        currentUser?.reauthenticate(credential)
            ?.addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    // Cambiar la contraseña
                    currentUser?.updatePassword(newPassword)
                        ?.addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                Log.d("PASSWORD_UPDATE", "Password updated successfully")
                                Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()

                                // Actualizar contraseña guardada si el usuario tenía "recordarme" activado
                                val rememberMe = sharedPreferences.getBoolean("remember_me", false)
                                if (rememberMe) {
                                    val editor = sharedPreferences.edit()
                                    editor.putString("saved_password", newPassword)
                                    editor.apply()
                                }
                            } else {
                                Log.w("PASSWORD_UPDATE", "Password update failed", updateTask.exception)
                                Toast.makeText(this, "Error updating password: ${updateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Log.w("REAUTHENTICATION", "Re-authentication failed", reauthTask.exception)
                    Toast.makeText(this, "Actual password incorrect", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
            .setTitle("Logout")
            .setMessage("¿Are you sure you want to logout?")
            .setPositiveButton("Yes, logout") { _, _ ->
                logout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun logout() {
        // Cerrar sesión en Firebase
        auth.signOut()

        // Limpiar SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_logged_in", false)
        editor.remove("saved_email")
        editor.remove("saved_password")
        editor.putBoolean("remember_me", false)
        editor.apply()

        Toast.makeText(this, "Session closed", Toast.LENGTH_SHORT).show()

        redirectToLogin()
    }

    private fun redirectToLogin() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity() // Cerrar todas las actividades anteriores
    }
}