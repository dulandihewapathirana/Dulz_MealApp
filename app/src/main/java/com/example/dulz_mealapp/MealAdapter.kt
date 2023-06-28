package com.example.dulz_mealapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MealAdapter(diffCallback: DiffUtil.ItemCallback<Meal>) :
    ListAdapter<Meal, MealAdapter.ViewHolder>(diffCallback) {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val categoryTextView: TextView = itemView.findViewById(R.id.categoryTextView)
        val areaTextView: TextView = itemView.findViewById(R.id.areaTextView)
        val instructionsTextView: TextView = itemView.findViewById(R.id.instructionsTextView)
        val ingredientsTextView: TextView = itemView.findViewById(R.id.ingredientsTextView)
        val messuresTextView: TextView = itemView.findViewById(R.id.messuresTextView)
        val mealImageView: ImageView = itemView.findViewById(R.id.mealImageView)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_meal, parent, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val meal = getItem(position)
        holder.nameTextView.text = meal.name
        holder.categoryTextView.text = "Category: ${meal.category}"
        holder.areaTextView.text = "Area: ${meal.area}"
        holder.instructionsTextView.text = "Instructions: ${meal.instructions}"
        holder.ingredientsTextView.text = "Ingredients: ${meal.ingredients}"
        holder.messuresTextView.text = "Messures: ${meal.measures}"

        Glide.with(holder.itemView.context)
            .load(meal.mealThumb)
            .into(holder.mealImageView)
    }

    class MealDiffCallback : DiffUtil.ItemCallback<Meal>() {
        override fun areItemsTheSame(oldItem: Meal, newItem: Meal): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Meal, newItem: Meal): Boolean {
            return oldItem == newItem
        }
    }
}

