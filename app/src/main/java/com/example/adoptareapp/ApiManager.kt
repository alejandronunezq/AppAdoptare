package com.example.adoptareapp
import android.os.AsyncTask
import android.util.Log
import java.io.BufferedWriter
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class ApiManager {
    companion object {
        const val BASE_URL = "http://10.0.2.2/API"
    }

    inner class SignUpTask(private val email: String, private val password: String, private val isAdopter: Boolean) : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean {
            try {
                val url = URL("$BASE_URL/addUser.php")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true

                val postData = "email=$email&password=$password&tipoCuenta=${if (isAdopter) "normal" else "refugio"}"
                val outputStream: OutputStream = connection.outputStream
                val writer = BufferedWriter(OutputStreamWriter(outputStream, "UTF-8"))
                writer.write(postData)
                writer.flush()
                writer.close()
                outputStream.close()

                val responseCode = connection.responseCode
                Log.d("ApiManager", "Código de respuesta: $responseCode")
                return responseCode == HttpURLConnection.HTTP_OK
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("ApiManager", "Error en signUp: ${e.message}")
                return false
            }
        }
    }

    fun signUp(email: String, password: String, isAdopter: Boolean): Boolean {
        val signUpTask = SignUpTask(email, password, isAdopter)
        return try {
            signUpTask.execute().get() // Espera hasta que la tarea esté completa
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ApiManager", "Error en signUp: ${e.message}")
            false
        }
    }
}
