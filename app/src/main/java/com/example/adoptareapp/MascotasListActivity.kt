package com.example.adoptareapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.scoreboard.R

class MascotasListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MascotasAdapter
    private var mascotasList = mutableListOf<Mascota>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mascotas_list)

        recyclerView = findViewById(R.id.recycler_view_mascotas)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MascotasAdapter(mascotasList)
        recyclerView.adapter = adapter

        cargarMascotas()
    }

    private fun cargarMascotas() {
        val sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val idRefugio = sharedPref.getInt("idusuario", -1) // Obtiene el idusuario de las preferencias

        if (idRefugio != -1) {
            val url = "http://10.0.2.2/API/petList.php?idRefugio=$idRefugio"

            val jsonArrayRequest = JsonArrayRequest(
                Request.Method.GET, url, null,
                Response.Listener { response ->
                    for (i in 0 until response.length()) {
                        val mascotaJson = response.getJSONObject(i)
                        val mascota = Mascota(
                            idmascotas = mascotaJson.getInt("idmascotas"),
                            nombre = mascotaJson.getString("nombre"),
                            edad = mascotaJson.getString("edad"),
                            imagenUrl = mascotaJson.getString("imagen_url")
                        )
                        mascotasList.add(mascota)
                    }
                    adapter.notifyDataSetChanged()
                },
                Response.ErrorListener { error ->
                    Log.e("MascotasListActivity", "Error al cargar mascotas: ${error.message}")
                }
            )

            Volley.newRequestQueue(this).add(jsonArrayRequest)
        } else {
            Log.e("MascotasListActivity", "ID de refugio no encontrado")
        }
    }
}