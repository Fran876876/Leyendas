package com.summercompany.leyendas.activities
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.leagueofheroes.R
import com.example.leagueofheroes.adapters.SuperheroAdapter
import com.example.leagueofheroes.data.Superhero
import com.example.leagueofheroes.data.SuperheroService
import com.summercompany.leyendas.R
import com.summercompany.leyendas.adapters.SuperheroAdapter
import com.summercompany.leyendas.data.Superhero
import com.summercompany.leyendas.data.SuperheroService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    lateinit var adapter: SuperheroAdapter
    lateinit var binding: ActivityMainBinding

    var superheroList: List<Superhero> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
                //Instanciamos AMBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
                // Este mét0do establece el contenido de la
                //  actividad a la vista raíz del binding que acabas de inflar.


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = SuperheroAdapter(superheroList) { position -> //parametro
            val superhero = superheroList[position]


            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("SUPERHERO_ID", superhero.id)
            startActivity(intent)
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)

        searchSuperheroesByName("a")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)

        val menuItem = menu?.findItem(R.id.action_search)
        val searchView = menuItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchSuperheroesByName(query)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                return false
            }
        })

        return true
    }

    fun getRetrofit(): SuperheroService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.superheroapi.com/api.php/7252591128153666/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(SuperheroService::class.java)
    }

    fun searchSuperheroesByName(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = getRetrofit()
                val result = service.findSuperheroesByName(query)

                superheroList = result.results

                CoroutineScope(Dispatchers.Main).launch {
                    adapter.items = superheroList
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}