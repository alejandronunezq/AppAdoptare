package com.example.adoptareapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.scoreboard.R
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private val apiBaseUrl = "http://192.168.1.4/API/getUser.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginButton: Button = findViewById(R.id.login_button)
        loginButton.setOnClickListener {
            val emailEditText: EditText = findViewById(R.id.email)
            val passwordEditText: EditText = findViewById(R.id.password)

            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {
        val url = apiBaseUrl

        val params = HashMap<String, String>()
        params["email"] = email
        params["password"] = password

        val jsonObject = JSONObject(params as Map<*, *>?)

        val request = JsonObjectRequest(
            Request.Method.POST,
            url,
            jsonObject,
            { response ->
                try {
                    val tipoCuenta = response.getString("tipoCuenta")

                    when (tipoCuenta) {
                        "normal" -> {
                            val intent = Intent(this@LoginActivity, GateActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        "refugio" -> {
                            val intent = Intent(this@LoginActivity, RefugioActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else -> {
                            Toast.makeText(this, "Tipo de cuenta no soportado: $tipoCuenta", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al procesar la respuesta del servidor.", Toast.LENGTH_LONG).show()
                    Log.e("LoginActivity", "Exception: ", e)
                }
            },
            { error ->
                val statusCode = error.networkResponse?.statusCode ?: "Código no disponible"
                val responseBody = error.networkResponse?.data?.let { String(it) } ?: "Respuesta no disponible"
                Toast.makeText(this, "Error en la solicitud: $statusCode", Toast.LENGTH_LONG).show()
                Log.d("LoginActivity", "Solicitud enviada: $url")
                Log.d("LoginActivity", "Código de estado HTTP: $statusCode")
                Log.e("LoginActivity", "Error en la respuesta: $responseBody")
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}
