package com.example.cambiayanooficial2.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cambiayanooficial2.R
import com.example.cambiayanooficial2.models.Notification

class NotificationAdapter(private val notifications: List<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    // Crear el ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    // Enlazar los datos con la vista
    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.bind(notification)
    }

    // Número de elementos
    override fun getItemCount(): Int {
        return notifications.size
    }

    // ViewHolder para enlazar la vista del item
    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Usamos findViewById en lugar de Kotlin synthetic
        private val notificationTitle: TextView = itemView.findViewById(R.id.notificationTitle)

        fun bind(notification: Notification) {
            notificationTitle.text = notification.mensaje
        }
    }
}
