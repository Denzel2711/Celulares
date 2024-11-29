package com.example.celulares

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class FormularioCelularActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etModelo: EditText
    private lateinit var etPrecio: EditText
    private lateinit var spinnerMarca: Spinner
    private lateinit var btnGuardar: Button
    private lateinit var databaseHelper: CelularesDatabaseHelper
    private var idCelular: Int? = null
    private lateinit var btnAgregarMarca: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_celular)

        etNombre = findViewById(R.id.etNombre)
        etModelo = findViewById(R.id.etModelo)
        etPrecio = findViewById(R.id.etPrecio)
        spinnerMarca = findViewById(R.id.spinnerMarca)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnAgregarMarca = findViewById(R.id.btnAgregarMarca)

        databaseHelper = CelularesDatabaseHelper(this)

        idCelular = intent.getIntExtra("idCelular", -1).takeIf { it != -1 }

        if (idCelular != null) {
            cargarDatosCelular(idCelular!!)
        }

        cargarMarcas()

        btnGuardar.setOnClickListener {
            guardarCelular()
        }

        btnAgregarMarca.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Agregar Nueva Marca")

            val input = EditText(this)
            builder.setView(input)

            builder.setPositiveButton("Agregar") { _, _ ->
                val marcaNombre = input.text.toString()
                if (marcaNombre.isNotBlank()) {
                    val db = databaseHelper.writableDatabase
                    val nuevaMarca = Marca(0, marcaNombre)
                    databaseHelper.insertarMarca(db, nuevaMarca)
                    cargarMarcas() // Refresca las marcas en el Spinner
                }
            }

            builder.setNegativeButton("Cancelar", null)

            builder.show()
        }
    }


    private fun cargarDatosCelular(id: Int) {
        val db = databaseHelper.readableDatabase
        val cursor = db.query(
            "Celular", null, "id_celular = ?",
            arrayOf(id.toString()), null, null, null
        )

        if (cursor.moveToFirst()) {
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            val modelo = cursor.getString(cursor.getColumnIndexOrThrow("modelo"))
            val precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
            val marcaId = cursor.getInt(cursor.getColumnIndexOrThrow("id_marca"))

            etNombre.setText(nombre)
            etModelo.setText(modelo)
            etPrecio.setText(precio.toString())

            val marcas = databaseHelper.obtenerMarcas(db)
            val marcaPos = marcas.indexOfFirst { it.id == marcaId }
            spinnerMarca.setSelection(marcaPos)
        }
        cursor.close()
    }

    private fun cargarMarcas() {
        val db = databaseHelper.readableDatabase
        val marcas = databaseHelper.obtenerMarcas(db)
        val marcaAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, marcas.map { it.nombre })
        marcaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMarca.adapter = marcaAdapter
    }

    private fun guardarCelular() {
        val nombre = etNombre.text.toString()
        val modelo = etModelo.text.toString()
        val precio = etPrecio.text.toString().toDoubleOrNull()
        val marcaId = spinnerMarca.selectedItemPosition + 1

        if (nombre.isNotBlank() && modelo.isNotBlank() && precio != null) {
            val db = databaseHelper.writableDatabase
            if (idCelular == null) {
                databaseHelper.insertarCelular(db, Celular(0, nombre, modelo, precio, marcaId, ""))
            } else {
                databaseHelper.actualizarCelular(db, Celular(idCelular!!, nombre, modelo, precio, marcaId, ""))
            }
            finish()
        } else {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
        }
    }
}
