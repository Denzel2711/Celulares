package com.example.celulares

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class CelularesDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createMarcaTable = """
        CREATE TABLE Marca (
            id_marca INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT NOT NULL
        );
    """
        db.execSQL(createMarcaTable)

        val createCelularTable = """
        CREATE TABLE Celular (
            id_celular INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT NOT NULL,
            modelo TEXT NOT NULL,
            precio REAL NOT NULL,
            id_marca INTEGER,
            FOREIGN KEY(id_marca) REFERENCES Marca(id_marca)
        );
    """
        db.execSQL(createCelularTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Marca")
        db.execSQL("DROP TABLE IF EXISTS Celular")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "CelularesDB"
        private const val DATABASE_VERSION = 1
    }

    fun obtenerMarcas(db: SQLiteDatabase): List<Marca> {
        val lista = mutableListOf<Marca>()
        val cursor = db.rawQuery("SELECT * FROM Marca", null)
        with(cursor) {
            while (moveToNext()) {
                lista.add(
                    Marca(
                        getInt(getColumnIndexOrThrow("id_marca")),
                        getString(getColumnIndexOrThrow("nombre")),
                    )
                )
            }
            close()
        }
        return lista
    }

    fun insertarMarca(db: SQLiteDatabase, marca: Marca): Long {
        val values = ContentValues().apply {
            put("nombre", marca.nombre)
        }
        return db.insert("Marca", null, values)
    }

    fun insertarCelular(db: SQLiteDatabase, celular: Celular): Long {
        val values = ContentValues().apply {
            put("nombre", celular.nombre)
            put("modelo", celular.modelo)
            put("precio", celular.precio)
            put("id_marca", celular.marcaId)
        }
        return db.insert("Celular", null, values)
    }

    fun actualizarCelular(db: SQLiteDatabase, celular: Celular): Int {
        val values = ContentValues().apply {
            put("nombre", celular.nombre)
            put("modelo", celular.modelo)
            put("precio", celular.precio)
            put("id_marca", celular.marcaId)
        }
        return db.update("Celular", values, "id_celular = ?", arrayOf(celular.id.toString()))
    }

    fun eliminarCelular(db: SQLiteDatabase, idCelular: Int): Int {
        return db.delete("Celular", "id_celular = ?", arrayOf(idCelular.toString()))
    }

}
