package com.example.dulz_mealapp

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.util.*

class SearchByNameActivity : AppCompatActivity() {
    private lateinit var mealsTextView: TextView
    private lateinit var ingredientEditText: TextView
    private lateinit var retrieveMealsButton: Button
    private lateinit var saveMealsButton : Button

    private lateinit var mealDao: MealDao
    private lateinit var mealDatabase: MealDatabase

    private var meals: List<Meal>? = null


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchmeal_details)

        mealsTextView = findViewById(R.id.meal_details)
        ingredientEditText = findViewById(R.id.ingredientEditText)
        retrieveMealsButton = findViewById(R.id.retrieve_meals_button)

        mealDatabase = MealDatabase.getInstance(applicationContext)
        mealDao = mealDatabase.mealDao()

        val ingredient = intent.getStringExtra("ingredient")

        CoroutineScope(Dispatchers.IO).launch {

            val meals = ingredient?.let { retrieveMealsFromWebService(it) }

            withContext(Dispatchers.Main) {
            }
        }

        retrieveMealsButton.setOnClickListener {
            val searchIngredient = ingredientEditText.text.toString()
            CoroutineScope(Dispatchers.IO).launch {
                val meals = retrieveMealsFromWebService(searchIngredient)
                withContext(Dispatchers.Main) {
                    if (meals.isEmpty()) {
                        // Display an error message if there are no meals for the given ingredient
                        Toast.makeText(
                            this@SearchByNameActivity,
                            "No meals found for $searchIngredient",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val builder = AlertDialog.Builder(this@SearchByNameActivity)
                        builder.setTitle("Meal Details")

                        // Filter meals based on substring entered by user
                        val filteredMeals = meals.filter { meal ->
                            meal.name.toLowerCase(Locale.getDefault()).contains(searchIngredient.toLowerCase(Locale.getDefault()))
                        }

                        if (filteredMeals.isEmpty()) {
                            // Display an error message if there are no meals containing the substring
                            Toast.makeText(
                                this@SearchByNameActivity,
                                "No meals found containing $searchIngredient",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {

                            builder.setMessage(
                                filteredMeals.joinToString(
                                    separator = "\n\n"
                                ) { mealDetailsString(it) }
                            )

                            builder.setPositiveButton("OK", null)

                            val dialog = builder.create()
                            dialog.show()

                            this@SearchByNameActivity.meals = filteredMeals
                        }
                    }
                }
            }
        }


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save any necessary data here
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Handle orientation change here if needed
    }


    private suspend fun retrieveMealsFromWebService(substring: String): List<Meal> {
        val url = "https://www.themealdb.com/api/json/v1/1/search.php?s=$substring"
        val response = URL(url).readText()

        val mealsJson = JSONObject(response).optJSONArray("meals")
        if (mealsJson == null) {
            return emptyList()
        }

        val mealList = mutableListOf<Meal>()
        for (i in 0 until mealsJson.length()) {
            val meal = mealsJson.getJSONObject(i)
            val id = meal.getString("idMeal").toInt()
            val mealName = meal.getString("strMeal")
            val category = meal.getString("strCategory")
            val area = meal.getString("strArea")
            val instructions = meal.getString("strInstructions")
            val tags = meal.optString("strTags", "")
            val youtube = meal.optString("strYoutube", "")
            val mealThumb = meal.optString("strMealThumb", "")
            val ingredients = mutableListOf<String>()
            val measures = mutableListOf<String>()
            for (j in 1..20) {
                val ingredient = meal.optString("strIngredient$j", "")
                if (ingredient.isNotEmpty()) {
                    ingredients.add(ingredient)
                    measures.add(meal.optString("strMeasure$j", ""))
                }
            }
            val mealDetail = Meal(
                id=id,
                name = mealName,
                category = category,
                area = area,
                instructions = instructions,
                tags = tags,
                youtubeLink = youtube,
                ingredients = ingredients,
                measures = measures,
                mealThumb = mealThumb
            )
            mealList.add(mealDetail)
        }
        return mealList
    }



    fun mealDetailsString(meal: Meal): String {
        val sb = StringBuilder()
        sb.append("Meal: ${meal.name}\n")
        sb.append("Category: ${meal.category}\n")
        sb.append("Area: ${meal.area}\n")
        sb.append("Instructions: ${meal.instructions}\n")
        sb.append("Tags: ${meal.tags}\n")
        sb.append("Youtube: ${meal.youtubeLink}\n")
        sb.append("Thumbnail URL: ${meal.mealThumb}\n")

        sb.append("Ingredients:\n")
        for (i in meal.ingredients.indices) {
            sb.append("Ingredient${i + 1}: ${meal.ingredients[i]}\n")
        }

        sb.append("\nMeasures:\n")
        for (i in meal.measures.indices) {
            sb.append("Measure${i + 1}: ${meal.measures[i]}\n")
        }

        return sb.toString()
    }

}