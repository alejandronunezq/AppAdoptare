package com.example.adoptareapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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
        adapter = PetsAdapter(emptyList())
        recyclerView.adapter = adapter

        getPets()
    }

    private fun getPets() {
        val url = "http://192.168.1.4/API/get_mascotas.php"

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
                            imageUrl = pet.getString("imagen_url") // Aquí debes modificar según cómo manejes las imágenes
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
}
