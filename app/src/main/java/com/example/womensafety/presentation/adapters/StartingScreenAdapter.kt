package com.example.womensafety.presentation.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.womensafety.R
import com.example.womensafety.domain.models.StartingScreenModel
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.starting_item_view.view.*

class StartingScreenAdapter(private val context: Context): RecyclerView.Adapter<StartingScreenAdapter.StartingScreenViewHolder>() {
    private var list = arrayListOf<StartingScreenModel>()
    class StartingScreenViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var title : MaterialTextView = view.findViewById(R.id.tv)
        var titleOne : MaterialTextView = view.findViewById(R.id.tv1)
        var titleTwo: MaterialTextView = view.findViewById(R.id.tv2)
        var imgView: ImageView = view.findViewById(R.id.ivIntro)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun bindView(list: ArrayList<StartingScreenModel>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StartingScreenViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.starting_item_view, parent, false)
        return StartingScreenViewHolder(adapterLayout)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: StartingScreenViewHolder, position: Int) {
        holder.apply {
            list[position].apply {
                title.text = tv1
                titleOne.text = tv2
                titleTwo.text = tv3
                imgView.setImageDrawable(context.getDrawable(img))
            }
        }
    }

    override fun getItemCount(): Int {
        return  3
    }
}