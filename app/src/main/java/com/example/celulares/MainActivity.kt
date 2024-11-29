package com.example.celulares

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var celularAdapter: CelularAdapter
    private lateinit var databaseHelper: CelularesDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerViewCelulares)
        recyclerView.layoutManager = LinearLayoutManager(this)

        databaseHelper = CelularesDatabaseHelper(this)

        cargarCelulares()

        val btnAgregarCelular: FloatingActionButton = findViewById(R.id.fabAgregarCelular)
        btnAgregarCelular.setOnClickListener {
            val intent = Intent(this, FormularioCelularActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        cargarCelulares()
    }

    private fun cargarCelulares() {
        val db = databaseHelper.readableDatabase
        val celulares = obtenerCelulares(db)

        celularAdapter = CelularAdapter(celulares, { celular ->
            val intent = Intent(this, FormularioCelularActivity::class.java)
            intent.putExtra("idCelular", celular.id)
            startActivity(intent)
        }, { celular ->
            eliminarCelular(celular)
        })

        recyclerView.adapter = celularAdapter
    }

    private fun eliminarCelular(celular: Celular) {
        val db = databaseHelper.writableDatabase
        databaseHelper.eliminarCelular(db, celular.id)
        cargarCelulares()
    }

    private fun obtenerCelulares(db: SQLiteDatabase): List<Celular> {
        val lista = mutableListOf<Celular>()
        val cursor = db.rawQuery("SELECT * FROM Celular", null)
        with(cursor) {
            while (moveToNext()) {
                val idCelular = getInt(getColumnIndexOrThrow("id_celular"))
                val nombre = getString(getColumnIndexOrThrow("nombre"))
                val modelo = getString(getColumnIndexOrThrow("modelo"))
                val precio = getDouble(getColumnIndexOrThrow("precio"))
                val marcaId = getInt(getColumnIndexOrThrow("id_marca"))

                val marcaCursor = db.rawQuery("SELECT nombre FROM Marca WHERE id_marca = ?", arrayOf(marcaId.toString()))
                val marcaNombre = if (marcaCursor.moveToFirst()) marcaCursor.getString(marcaCursor.getColumnIndexOrThrow("nombre")) else "Desconocida"
                marcaCursor.close()

                lista.add(Celular(idCelular, nombre, modelo, precio, marcaId, marcaNombre))
            }
            close()
        }
        return lista
    }
}