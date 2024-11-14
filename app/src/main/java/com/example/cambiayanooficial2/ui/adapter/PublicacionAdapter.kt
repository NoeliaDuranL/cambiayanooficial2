package com.example.cambiayanooficial2.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cambiayanooficial2.R
import com.example.cambiayanooficial2.models.Publicacion

class PublicacionAdapter(
    private val listaPublicaciones: List<Publicacion>,
    private val onMeInteresaClick: (Publicacion) -> Unit
) : RecyclerView.Adapter<PublicacionAdapter.PublicacionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicacionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_publicacion, parent, false)
        return PublicacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: PublicacionViewHolder, position: Int) {
        val publicacion = listaPublicaciones[position]
        holder.bind(publicacion)
    }

    override fun getItemCount(): Int = listaPublicaciones.size

    inner class PublicacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val publisherName: TextView = itemView.findViewById(R.id.publisherName)
        private val publicationDate: TextView = itemView.findViewById(R.id.publicationDate)
        private val productImage: ImageView = itemView.findViewById(R.id.productImage)
        private val productName: TextView = itemView.findViewById(R.id.productName)
        private val productDescription: TextView = itemView.findViewById(R.id.productDescription)
        private val btnMeInteresa: Button = itemView.findViewById(R.id.btnMeInteresa)

        fun bind(publicacion: Publicacion) {
            // Asignar el nombre del publicador y la fecha de publicación
            publisherName.text = publicacion.usuario.usuario
            publicationDate.text = publicacion.created_at

            // Asignar nombre y descripción del producto
            productName.text = publicacion.producto.nombre
            productDescription.text = publicacion.producto.descripcion

            // Cargar la imagen del producto usando Glide
            Glide.with(itemView.context)
                .load(publicacion.producto.imagen)
                .into(productImage)

            // Configurar el botón "Me Interesa"
            btnMeInteresa.setOnClickListener {
                onMeInteresaClick(publicacion)
            }
        }


    }
}
