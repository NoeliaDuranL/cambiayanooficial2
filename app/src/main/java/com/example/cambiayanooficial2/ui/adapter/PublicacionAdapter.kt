package com.example.cambiayanooficial2.ui.adapter

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
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
        private val btnContactar: Button = itemView.findViewById(R.id.btnContactar)

        fun bind(publicacion: Publicacion) {
            // Asignar datos del usuario
            publisherName.text = publicacion.usuario.usuario

            // Asignar la fecha de publicación
            publicationDate.text = publicacion.created_at

            // Asignar datos del producto
            productName.text = publicacion.producto.nombre
            productDescription.text = publicacion.producto.descripcion

            // Cargar la imagen del producto usando Glide
            Glide.with(itemView.context)
                .load(publicacion.producto.imagen)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(productImage)

            // Configurar el botón "Me Interesa"
            btnMeInteresa.setOnClickListener {
                onMeInteresaClick(publicacion)
            }

            // Configurar el botón "Contactar"
            btnContactar.setOnClickListener {
                // Asegúrate de que el número de celular esté disponible
                val numberUser = publicacion.usuario.numero_celular // Obtén el número de celular de la publicación
                val phoneNumber = "+591" + numberUser // Concatenar el código de país con el número
                val url = "https://wa.me/$phoneNumber" // Construir la URL para WhatsApp
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                itemView.context.startActivity(intent) // Iniciar la actividad de WhatsApp

            }

            Log.d("PublicacionAdapter", "Number: ${publicacion.usuario.numero_celular}")
            Log.d("PublicacionAdapter", "Publicacionr: ${publicacion}")
        }
    }
}
