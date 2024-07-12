package com.example.asalariapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.asalariapp.databinding.ItemRvBinding
import com.example.asalariapp.models.Model
import com.squareup.picasso.Picasso

class MahsulotAdapter(val rvAction: RvAction,val list: ArrayList<Model>):RecyclerView.Adapter<MahsulotAdapter.Vh>() {
    inner class Vh(val itemRvBinding: ItemRvBinding):ViewHolder(itemRvBinding.root){
        fun onBind(model: Model){
            itemRvBinding.mahsulotNomi.text = model.name
            itemRvBinding.mahsulotSoni.text = model.price.toString()
            Picasso.get().load(model.image).into(itemRvBinding.qrCode)

            itemRvBinding.root.setOnClickListener {
                rvAction.itemClick(model)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemRvBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }


    override fun getItemCount(): Int =list.size


    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position])
    }
    interface RvAction{
        fun itemClick(model: Model)
    }
}