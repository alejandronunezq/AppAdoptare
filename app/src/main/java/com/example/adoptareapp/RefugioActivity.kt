package com.example.adoptareapp

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.scoreboard.R
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException

class VolleyMultipartRequest(
    method: Int,
    url: String,
    private val mListener: Response.Listener<NetworkResponse>,
    private val mErrorListener: Response.ErrorListener
) : Request<NetworkResponse>(method, url, mErrorListener) {

    private val twoHyphens = "--"
    private val lineEnd = "\r\n"
    private val boundary = "apiclient-${System.currentTimeMillis()}"

    private var mFilePartData: ByteArray? = null
    private var mFileName: String? = null

    fun addFilePart(filePartName: String, fileName: String, data: ByteArray) {
        mFilePartData = data
        mFileName = fileName
    }

    override fun getHeaders(): MutableMap<String, String> {
        val headers = HashMap<String, String>()
        headers["Content-Type"] = "multipart/form-data;boundary=$boundary"
        return headers
    }

    override fun getBodyContentType(): String {
        return "multipart/form-data;boundary=$boundary"
    }

    @Throws(IOException::class)
    override fun getBody(): ByteArray {
        val bos = ByteArrayOutputStream()
        val dos = DataOutputStream(bos)

        if (mFilePartData != null) {
            dos.writeBytes(twoHyphens + boundary + lineEnd)
            dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" +
                    mFileName + "\"" + lineEnd)
            dos.writeBytes(lineEnd)

            val fileInputStream = ByteArrayInputStream(mFilePartData)
            val buffer = ByteArray(1024)
            var bytesRead: Int

            while (fileInputStream.read(buffer).also { bytesRead = it } != -1) {
                dos.write(buffer, 0, bytesRead)
            }

            dos.writeBytes(lineEnd)
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd)
        }

        return bos.toByteArray()
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<NetworkResponse> {
        return Response.success(response, HttpHeaderParser.parseCacheHeaders(response))
    }

    override fun deliverResponse(response: NetworkResponse) {
        mListener.onResponse(response)
    }
}

class RefugioActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var nombreEditText: EditText
    private lateinit var edadEditText: EditText
    private var imageUri: Uri? = null
    private val IMAGE_PICK_CODE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_refugio)

        imageView = findViewById(R.id.ivImagenMascota)
        nombreEditText = findViewById(R.id.etNombreMascota)
        edadEditText = findViewById(R.id.etEdadMascota)
        val btnSeleccionarImagen = findViewById<Button>(R.id.btnSeleccionarImagen)
        val btnGuardarMascota = findViewById<Button>(R.id.btnGuardarMascota)

        btnSeleccionarImagen.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    IMAGE_PICK_CODE
                )
            } else {
                seleccionarImagen()
            }
        }

        val btnVerMascotas: Button = findViewById(R.id.btnVerMascotas)
        btnVerMascotas.setOnClickListener {
            val intent = Intent(this, MascotasListActivity::class.java)
            startActivity(intent)
        }

        val btnCerrarSesion: Button = findViewById(R.id.btnCerrarSesion)
        btnCerrarSesion.setOnClickListener {
            confirmarCerrarSesion()
        }

        btnGuardarMascota.setOnClickListener {
            if (imageUri == null || nombreEditText.text.isEmpty() || edadEditText.text.isEmpty()) {
                Toast.makeText(
                    this,
                    "Por favor, selecciona una imagen y completa los campos de nombre y edad.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val imageName = "img_${System.currentTimeMillis()}.jpg"
            uploadImage(imageUri, imageName)
        }
    }

    private fun seleccionarImagen() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            imageView.setImageURI(imageUri)
        }
    }

    private fun uploadImage(imageUri: Uri?, imageName: String) {
        val url = "http://192.168.1.4/API/upload_image.php"

        val multipartRequest = VolleyMultipartRequest(
            Request.Method.POST, url,
            Response.Listener<NetworkResponse> { response ->
                val responseString = String(response.data)
                enviarDatosAlServidor(imageName, nombreEditText.text.toString(), edadEditText.text.toString())
                Toast.makeText(this, responseString, Toast.LENGTH_LONG).show()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            })

        imageUri?.let { uri ->
            contentResolver.openInputStream(uri)?.buffered()?.use { inputStream ->
                val imageData = inputStream.readBytes()
                multipartRequest.addFilePart("file", imageName, imageData)
                Volley.newRequestQueue(this).add(multipartRequest)
            }
        }
    }

    private fun enviarDatosAlServidor(imageName: String, nombre: String, edad: String) {
        val sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val idRefugio = sharedPref.getInt("idusuario", -1)
        if (idRefugio == -1) {
            Toast.makeText(this, "Error al obtener ID del refugio.", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "http://192.168.1.4/API/put_mascotas.php"
        val params = HashMap<String, String>().apply {
            put("nombre", nombre)
            put("edad", edad)
            put("imagen", imageName)
            put("idRefugio", idRefugio.toString())
        }

        val jsonObject = JSONObject(params as Map<*, *>)
        val request = JsonObjectRequest(Request.Method.POST, url, jsonObject,
            { response ->
                Toast.makeText(this, "Mascota añadida exitosamente", Toast.LENGTH_SHORT).show()
                resetUI()
            },
            { error ->
                Toast.makeText(this, "Error al añadir mascota: ${error.message}", Toast.LENGTH_SHORT).show()
            })

        Volley.newRequestQueue(this).add(request)
    }

    private fun resetUI() {
        imageView.setImageResource(0)
        nombreEditText.text.clear()
        edadEditText.text.clear()
        imageUri = null
    }

    private fun cerrarSesion() {
        val url = "http://192.168.1.4/API/logout.php"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, null,
            { response ->

                val success = response.getBoolean("success")
                if (success) {

                    val sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
                    sharedPreferences.edit().clear().apply()


                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    val message = response.getString("message")
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            },
            { error ->

                Toast.makeText(this, "Error al cerrar sesión: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }
    private fun confirmarCerrarSesion() {
        AlertDialog.Builder(this)
            .setMessage("¿Estás seguro de que quieres cerrar sesión?")
            .setPositiveButton("Sí") { _, _ -> cerrarSesion() }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .show()
    }


}
