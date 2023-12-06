package com.example.adoptareapp

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.ejemplo.adoptareapp.Pet
import com.example.scoreboard.R

class GateActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PetsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gate)

        recyclerView = findViewById(R.id.recycler_view_pets)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PetsAdapter(emptyList()) { telefono ->
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse("tel:$telefono")
                startActivity(callIntent)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), REQUEST_PHONE_CALL)
            }
        }
        recyclerView.adapter = adapter

        getPets()

        val logoutButton: ImageView = findViewById(R.id.logout_button)
        logoutButton.setOnClickListener {
            confirmarCerrarSesion()
        }
    }

    private fun getPets() {
        val url = "http://10.0.2.2/API/get_mascotas.php"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                val petsList = ArrayList<Pet>()
                for (i in 0 until response.length()) {
                    val pet = response.getJSONObject(i)
                    petsList.add(
                        Pet(
                            name = pet.getString("nombre"),
                            age = pet.getString("edad"),
                            imageUrl = pet.getString("imagen_url"),
                            telefono = pet.getString("telefono")
                        )
                    )
                }
                adapter.updatePets(petsList)
            },
            Response.ErrorListener { error ->
                Log.e("GateActivity", "Error: ${error.message}")
            }
        )

        Volley.newRequestQueue(this).add(jsonArrayRequest)
    }

    private fun cerrarSesion() {
        val sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun confirmarCerrarSesion() {
        AlertDialog.Builder(this)
            .setMessage("¿Estás seguro de que quieres cerrar sesión?")
            .setPositiveButton("Sí") { _, _ -> cerrarSesion() }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    companion object {
        private const val REQUEST_PHONE_CALL = 1
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PHONE_CALL && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
           
        }
    }
}
