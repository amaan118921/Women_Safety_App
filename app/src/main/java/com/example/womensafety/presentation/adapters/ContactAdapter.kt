package com.example.womensafety.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.womensafety.R
import com.example.womensafety.data.room.enitities.ContactEntity
import com.example.womensafety.domain.models.ContactModel
import com.example.womensafety.domain.models.toContactModel
import com.google.android.material.textview.MaterialTextView

class ContactAdapter(private val listener: IListener?, private var key: Int): RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {
    private var list = mutableListOf<ContactEntity>()

    @SuppressLint("NotifyDataSetChanged")
    fun bindList(list: MutableList<ContactEntity>) {
        this.list = list
        notifyDataSetChanged()
    }
    class ContactViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var tvName: MaterialTextView = view.findViewById(R.id.tvName)
        var tvPhone: MaterialTextView = view.findViewById(R.id.tvPhone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return when(key) {
            0 -> {
                val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.item_view_contacts, parent, false)
                ContactViewHolder(adapterLayout)
            }
            else -> {
                val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.item_view_home_contacts, parent, false)
                ContactViewHolder(adapterLayout)
            }
        }
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        list[position].apply {
            holder.apply {
                tvName.text = name
                tvPhone.text = phone
            }
            holder.itemView.setOnClickListener {
                listener?.onItemClick(this.toContactModel())
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface IListener {
        fun onItemClick(model: ContactModel)
    }
}