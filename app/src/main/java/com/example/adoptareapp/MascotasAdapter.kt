package com.example.adoptareapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.scoreboard.R

class MascotasAdapter(private val mascotasList: List<Mascota>) : RecyclerView.Adapter<MascotasAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombreTextView: TextView = view.findViewById(R.id.nombre_mascota)
        val edadTextView: TextView = view.findViewById(R.id.edad_mascota)
        val imagenImageView: ImageView = view.findViewById(R.id.imagen_mascota)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mascota, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mascota = mascotasList[position]
        holder.nombreTextView.text = mascota.nombre
        holder.edadTextView.text = mascota.edad


        Glide.with(holder.itemView.context)
            .load(mascota.imagenUrl)
            .into(holder.imagenImageView)
    }

    override fun getItemCount() = mascotasList.size
}