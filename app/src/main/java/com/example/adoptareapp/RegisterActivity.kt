package com.example.adoptareapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.example.scoreboard.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var apiManager: ApiManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        apiManager = ApiManager()

        val emailEditText: EditText = findViewById(R.id.email)
        val passwordEditText: EditText = findViewById(R.id.password)
        val phoneEditText: EditText = findViewById(R.id.phone)
        val isAdopterRadioButton: RadioButton = findViewById(R.id.radio_adopter)
        val signUpButton: Button = findViewById(R.id.login_button)

        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val phone = phoneEditText.text.toString()
            val isAdopter = isAdopterRadioButton.isChecked

            signUp(email, password, isAdopter, phone)
        }
    }

    private fun signUp(email: String, password: String, isAdopter: Boolean, phone: String) {
        val success = apiManager.signUp(email, password, isAdopter, phone)

        if (success) {
            Log.d("RegisterActivity", "Registro exitoso")
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(mainActivityIntent)
            finish()
        } else {
            Log.e("RegisterActivity", "Error en el registro")
        }
    }
}
