package com.example.adoptareapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ejemplo.adoptareapp.Pet
import com.example.scoreboard.R

class PetsAdapter(
    private var petsList: List<Pet>,
    private val onItemClicked: (String) -> Unit
) : RecyclerView.Adapter<PetsAdapter.PetViewHolder>() {

    fun updatePets(newPets: List<Pet>) {
        petsList = newPets
        notifyDataSetChanged()
    }

    class PetViewHolder(itemView: View, private val onItemClicked: (String) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val petImage: ImageView = itemView.findViewById(R.id.image_pet)
        private val petName: TextView = itemView.findViewById(R.id.text_pet_name)
        private val petAge: TextView = itemView.findViewById(R.id.text_pet_age)

        fun bind(pet: Pet) {
            petName.text = pet.name
            petAge.text = pet.age
            Glide.with(itemView.context)
                .load(pet.imageUrl)
                .into(petImage)

            itemView.setOnClickListener {
                onItemClicked(pet.telefono)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pet, parent, false)
        return PetViewHolder(view, onItemClicked)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        val pet = petsList[position]
        holder.bind(pet)
    }

    override fun getItemCount() = petsList.size
}
