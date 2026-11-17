package com.example.flightsystem

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : ComponentActivity() {

    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        val userId = intent.getIntExtra("USER_ID", -1)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val btnSave = findViewById<Button>(R.id.btnSaveProfile)
        val btnBack = findViewById<Button>(R.id.btnBack)
        val tvMessage = findViewById<TextView>(R.id.tvProfileMessage)

        if (userId == -1) {
            tvMessage.text = "User information is missing."
            btnSave.isEnabled = false
            return
        }

        val db = FlightDatabase.getInstance(applicationContext)

        // Load the user's information
        lifecycleScope.launch {

            val user = withContext(Dispatchers.IO) {
                db.userDao().getUserByID(userId)
            }

            if (user != null) {
                currentUser = user

                etUsername.setText(user.username)
                etEmail.setText(user.email)
                etPhone.setText(user.phone)

            } else {
                tvMessage.text = "User not found."
                btnSave.isEnabled = false
            }
        }

        // Save profile changes
        btnSave.setOnClickListener {

            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phone = etPhone.text.toString().trim()

            if (username.isBlank() || email.isBlank() || phone.isBlank()) {
                tvMessage.text = "Please fill in all fields."
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tvMessage.text = "Please enter a valid email."
                return@setOnClickListener
            }

            val oldUser = currentUser

            if (oldUser == null) {
                tvMessage.text = "User information is not loaded yet."
                return@setOnClickListener
            }

            btnSave.isEnabled = false
            tvMessage.text = "Saving..."

            lifecycleScope.launch {

                try {
                    val updatedUser = oldUser.copy(
                        username = username,
                        email = email,
                        phone = phone
                    )

                    withContext(Dispatchers.IO) {
                        db.userDao().updateUser(updatedUser)
                    }

                    currentUser = updatedUser
                    tvMessage.text = "Profile updated successfully!"

                } catch (e: Exception) {
                    tvMessage.text = "Update failed: ${e.message}"
                }

                btnSave.isEnabled = true
            }
        }
        btnBack.setOnClickListener { finish() }
    }
}