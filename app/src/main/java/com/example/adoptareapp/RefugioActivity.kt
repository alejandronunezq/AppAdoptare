package com.example.adoptareapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.scoreboard.R
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class RefugioActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var nombreEditText: EditText
    private lateinit var edadEditText: EditText
    private var idRefugio: Int = 11 // Suponiendo que este es el ID de refugio que tienes
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_refugio)

        imageView = findViewById(R.id.ivImagenMascota)
        nombreEditText = findViewById(R.id.etNombreMascota)
        edadEditText = findViewById(R.id.etEdadMascota)
        val btnSeleccionarImagen = findViewById<Button>(R.id.btnSeleccionarImagen)
        val btnGuardarMascota = findViewById<Button>(R.id.btnGuardarMascota)

        btnSeleccionarImagen.setOnClickListener {
            // Verificar si el permiso de lectura está concedido
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                // Si el permiso no está concedido, solicitarlo
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    IMAGE_PICK_CODE)
            } else {
                // Permiso concedido, abrir selector de imágenes
                seleccionarImagen()
            }
        }

        btnGuardarMascota.setOnClickListener {
            if (imageUri == null || nombreEditText.text.isEmpty() || edadEditText.text.isEmpty()) {
                Toast.makeText(this, "Por favor, selecciona una imagen y completa los campos de nombre y edad.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val imageName = "img_${System.currentTimeMillis()}.jpg"
            val imageBase64 = convertImageToBase64(imageUri)

            enviarDatosAlServidor(imageName, imageBase64, nombreEditText.text.toString(), edadEditText.text.toString(), idRefugio)
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
            imageUri = data?.data // Guardar la Uri de la imagen seleccionada
            imageUri?.let { uri ->
                imageView.setImageURI(uri)
            }
        }
    }

    private fun convertImageToBase64(imageUri: Uri?): String {
        if (imageUri == null) {
            Toast.makeText(this, "Error al convertir la imagen.", Toast.LENGTH_SHORT).show()
            return ""
        }

        contentResolver.openInputStream(imageUri).use { inputStream ->
            val bitmap = BitmapFactory.decodeStream(inputStream)
            ByteArrayOutputStream().use { byteArrayOutputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()
                return Base64.encodeToString(byteArray, Base64.DEFAULT)
            }
        }
    }

    private fun enviarDatosAlServidor(imageName: String, imageBase64: String, nombre: String, edad: String, idRefugio: Int) {
        val url = "http://10.0.2.2/API/put_mascotas.php"

        val params = HashMap<String, String>().apply {
            put("nombre", nombre)
            put("edad", edad)
            put("imagen", imageBase64)
            put("nombreImagen", imageName)
            put("idRefugio", idRefugio.toString())
        }

        val jsonObject = JSONObject(params as Map<*, *>?)

        val request = JsonObjectRequest(Request.Method.POST, url, jsonObject,
            { response ->
                Toast.makeText(this, "Mascota añadida exitosamente", Toast.LENGTH_SHORT).show()
            },
            { error ->
                Toast.makeText(this, "Error al añadir mascota: ${error.message}", Toast.LENGTH_SHORT).show()
            })

        Volley.newRequestQueue(this).add(request)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            IMAGE_PICK_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    seleccionarImagen()
                } else {
                    Toast.makeText(this, "Permiso denegado para leer el almacenamiento", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }
}
