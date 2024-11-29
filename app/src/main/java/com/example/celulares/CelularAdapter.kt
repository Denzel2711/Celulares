package com.example.celulares

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CelularAdapter(private val celulares: List<Celular>, private val onItemClicked: (Celular) -> Unit, private val onItemDelete: (Celular) -> Unit) :
    RecyclerView.Adapter<CelularAdapter.CelularViewHolder>() {

    inner class CelularViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvModelo: TextView = itemView.findViewById(R.id.tvModelo)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        val tvMarca: TextView = itemView.findViewById(R.id.tvMarca) // TextView para mostrar la marca
        val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminar)

        fun bind(celular: Celular) {
            tvNombre.text = celular.nombre
            tvModelo.text = celular.modelo
            tvPrecio.text = "$${celular.precio}"
            tvMarca.text = celular.marcaNombre // Mostrar el nombre de la marca
            itemView.setOnClickListener { onItemClicked(celular) }
            btnEliminar.setOnClickListener { onItemDelete(celular) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CelularViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_celular, parent, false)
        return CelularViewHolder(view)
    }

    override fun onBindViewHolder(holder: CelularViewHolder, position: Int) {
        holder.bind(celulares[position])
    }

    override fun getItemCount() = celulares.size
}