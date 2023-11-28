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

        val signUpButton: Button = findViewById(R.id.login_button)
        signUpButton.setOnClickListener {
            signUp()
        }
    }

    private fun signUp() {
        val emailEditText: EditText = findViewById(R.id.email)
        val passwordEditText: EditText = findViewById(R.id.password)
        val isAdopterRadioButton: RadioButton = findViewById(R.id.radio_adopter)

        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val isAdopter = isAdopterRadioButton.isChecked

        // Llama a la función signUp en la clase ApiManager
        val success = apiManager.signUp(email, password, isAdopter)

        // Aquí puedes manejar el resultado (por ejemplo, mostrar un mensaje al usuario)
        if (success) {
            Log.d("RegisterActivity", "Registro exitoso")

            // Redirige a la MainActivity después del registro exitoso
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(mainActivityIntent)
            finish()  // Esto evita que el usuario regrese a la pantalla de registro al presionar "Atrás"
        } else {
            Log.e("RegisterActivity", "Error en el registro")
        }
    }
}
