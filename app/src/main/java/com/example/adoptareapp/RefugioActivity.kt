package com.example.adoptareapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.scoreboard.R
import android.Manifest
import android.content.pm.PackageManager


class RefugioActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_refugio)

        imageView = findViewById(R.id.ivImagenMascota) // Reemplaza con tu ID real del ImageView
        val btnSeleccionarImagen = findViewById<Button>(R.id.btnSeleccionarImagen)

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
    }

    private fun seleccionarImagen() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            // Verificar si el resultado contiene una imagen
            if (data == null || data.data == null) {
                return
            }

            // Obtener la uri de la imagen
            val imageUri: Uri? = data.data
            // Mostrar la imagen en el ImageView
            imageView.setImageURI(imageUri)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            IMAGE_PICK_CODE -> {
                // Si la solicitud es cancelada, el array resultante está vacío
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permiso concedido, abrir selector de imágenes
                    seleccionarImagen()
                } else {
                    // Permiso denegado, mostrar mensaje
                    Toast.makeText(this, "Permiso denegado para leer el almacenamiento", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }
}
