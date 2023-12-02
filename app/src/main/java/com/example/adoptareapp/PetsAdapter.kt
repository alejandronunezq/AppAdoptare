package com.example.adoptareapp;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ejemplo.adoptareapp.Pet
import com.example.scoreboard.R

class PetsAdapter(private var petsList: List<Pet>) : RecyclerView.Adapter<PetsAdapter.PetViewHolder>() {

    fun updatePets(newPets: List<Pet>) {
        petsList = newPets
        notifyDataSetChanged()
    }

    class PetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val petImage: ImageView = itemView.findViewById(R.id.image_pet)
        val petName: TextView = itemView.findViewById(R.id.text_pet_name)
        val petAge: TextView = itemView.findViewById(R.id.text_pet_age)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pet, parent, false)
        return PetViewHolder(view)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        val pet = petsList[position]
        holder.petName.text = pet.name
        holder.petAge.text = pet.age

        Glide.with(holder.itemView.context)
            .load(pet.imageUrl)
            .into(holder.petImage)
    }

    override fun getItemCount() = petsList.size
}
