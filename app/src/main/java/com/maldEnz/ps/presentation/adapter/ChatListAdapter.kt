package com.maldEnz.ps.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.maldEnz.ps.R
import com.maldEnz.ps.presentation.mvvm.model.ChatModel

class ChatListAdapter(private val chatList: List<ChatModel>) :
    RecyclerView.Adapter<ChatListAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chatNameTextView: TextView = itemView.findViewById(R.id.chatNameTextView)
        // Otros elementos de la vista, si los necesitas
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recycler_chat_list, parent, false)
        return ChatViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatList[position]

        // Configura la vista con los datos del chat
        holder.chatNameTextView.text = chat.chatId
        // Puedes mostrar otros datos relevantes del chat aquí

        // Agrega un oyente de clic a la vista si es necesario
        holder.itemView.setOnClickListener {
            // Maneja el clic en el elemento del chat
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }
}