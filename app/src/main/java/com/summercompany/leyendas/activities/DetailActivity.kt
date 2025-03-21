package com.summercompany.leyendas.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.squareup.picasso.Picasso
import com.summercompany.leyendas.R
import com.summercompany.leyendas.data.Superhero
import com.summercompany.leyendas.data.SuperheroService
import com.summercompany.leyendas.databinding.ActivityDetailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class DetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailBinding

    lateinit var superhero: Superhero

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val id = intent.getStringExtra("SUPERHERO_ID")!!
        getSuperheroById(id)

        binding.navigationBar.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_biography -> {
                    binding.appearanceContent.root.visibility = View.GONE
                    binding.statsContent.root.visibility = View.GONE
                    binding.biographyContent.root.visibility = View.VISIBLE
                }
                R.id.action_appearance -> {
                    binding.statsContent.root.visibility = View.GONE
                    binding.biographyContent.root.visibility = View.GONE
                    binding.appearanceContent.root.visibility = View.VISIBLE
                }
                R.id.action_stats -> {
                    binding.biographyContent.root.visibility = View.GONE
                    binding.appearanceContent.root.visibility = View.GONE
                    binding.statsContent.root.visibility = View.VISIBLE
                }
            }
            true
        }

        binding.navigationBar.selectedItemId = R.id.action_biography
    }

    fun loadData() {
        Picasso.get().load(superhero.image.url).into(binding.pictureImageView)

        supportActionBar?.title = superhero.name
        supportActionBar?.subtitle = superhero.biography.realName

        // Biography
        binding.biographyContent.publisherTextView.text = superhero.biography.publisher
        binding.biographyContent.placeOfBirthTextView.text = superhero.biography.placeOfBirth
        binding.biographyContent.alignmentTextView.text = superhero.biography.alignment
        binding.biographyContent.alignmentTextView.setTextColor(getColor(superhero.getAlignmentColor()))

        binding.biographyContent.occupationTextView.text = superhero.work.occupation
        binding.biographyContent.baseTextView.text = superhero.work.base

        // Appearance
        with(superhero.appearance){
        binding.appearanceContent.raceTextView.text = race
        binding.appearanceContent.genderTextView.text = gender
        binding.appearanceContent.eyeColorTextView.text = eyeColor
        binding.appearanceContent.hairColorTextView.text = hairColor
        binding.appearanceContent.weightTextView.text = getWeightKg()
        binding.appearanceContent.heightTextView.text = getHeightCm()
        }
        // Stats
        with(superhero.powerstats) {
            binding.statsContent.intelligenceTextView.text = "${intelligence.toIntOrNull() ?: 0}"
            binding.statsContent.strengthTextView.text = "${intelligence.toIntOrNull() ?: 0}"
            binding.statsContent.speedTextView.text = "${intelligence.toIntOrNull() ?: 0}"
            binding.statsContent.durabilityTextView.text = "${intelligence.toIntOrNull() ?: 0}"
            binding.statsContent.powerTextView.text = "${intelligence.toIntOrNull() ?: 0}"
            binding.statsContent.combatTextView.text = "${intelligence.toIntOrNull() ?: 0}"

            binding.statsContent.intelligenceProgressBar.progress = intelligence.toIntOrNull() ?: 0
            binding.statsContent.strenghtProgressBar.progress = intelligence.toIntOrNull() ?: 0
            binding.statsContent.speedProgressBar.progress = intelligence.toIntOrNull() ?: 0
            binding.statsContent.durabilityProgressBar.progress = intelligence.toIntOrNull() ?: 0
            binding.statsContent.powerProgressBar.progress = intelligence.toIntOrNull() ?: 0
            binding.statsContent.combatProgressBar.progress = intelligence.toIntOrNull() ?: 0
        }
    }

    fun getRetrofit(): SuperheroService {
        val retrofit = Retrofit.Builder() // CONSTRUCCION DE OBJETO MEDIANTE PATRON BUILDER
            .baseUrl("https://www.superheroapi.com/api.php/7252591128153666/")
            .addConverterFactory(GsonConverterFactory.create()) // Este mét0do añade un convertidor que permite convertir las respuestas JSON de la API en objetos de Kotlin.
            .build() // METODO Q CREA EL OBJETO

        return retrofit.create(SuperheroService::class.java)
    }

    fun getSuperheroById(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = getRetrofit()
                superhero = service.findSuperheroById(id)

                CoroutineScope(Dispatchers.Main).launch {
                    loadData()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}