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

    private val apiBaseUrl = "http://10.0.2.2/API/getUser.php"

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
            Response.Listener { response ->
                try {
                    val tipoCuenta = response.getString("tipoCuenta")

                    if (tipoCuenta == "normal") {
                        val intent = Intent(this@LoginActivity, GateActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // Manejar otros tipos de cuenta o acciones según sea necesario
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                // Manejar error de la API
                Toast.makeText(this, "Error en la solicitud: ${error.networkResponse?.statusCode}", Toast.LENGTH_SHORT).show()
                error.printStackTrace()

                // Agregar esta línea para imprimir la respuesta en el logcat
                Log.e("LoginActivity", "Error en la respuesta: ${String(error.networkResponse?.data ?: ByteArray(0))}")
            }
        )


        Volley.newRequestQueue(this).add(request)
    }
}
