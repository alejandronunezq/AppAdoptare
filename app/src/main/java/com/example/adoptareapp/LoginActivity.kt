package com.example.adoptareapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
                    if (response.getBoolean("success")) {
                        val idUsuario = response.getInt("idusuario")
                        val tipoCuenta = response.getString("tipoCuenta")

                        // Guardar idUsuario y tipoCuenta en SharedPreferences
                        val sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putInt("idusuario", idUsuario)
                            putString("tipoCuenta", tipoCuenta)
                            apply()
                        }

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
                    } else {
                        Toast.makeText(this, response.getString("message"), Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al procesar la respuesta del servidor.", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error en la solicitud: ${error.toString()}", Toast.LENGTH_LONG).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}
