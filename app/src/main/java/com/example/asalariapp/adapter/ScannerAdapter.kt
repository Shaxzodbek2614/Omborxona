package com.example.asalariapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.asalariapp.databinding.ItemRvBinding
import com.example.asalariapp.models.Mahsulot
import com.example.asalariapp.models.Model
import com.example.asalariapp.utils.MyData
import com.example.asalariapp.utils.MySharedPreferences
import com.squareup.picasso.Picasso

class ScannerAdapter(var context: Context, var list: ArrayList<Model>) : Adapter<ScannerAdapter.Vh>() {

    var umumiyNarx: Long =  0

    inner class Vh(var itemRvBinding: ItemRvBinding) : ViewHolder(itemRvBinding.root) {
        fun onBind(product: Model) {
            list.toSet()
            itemRvBinding.apply {
                Picasso.get().load(product.image).into(qrCode)
                mahsulotNomi.text = "${product.name}"
                mahsulotNarxi.text = "${product.price} so'm"
                mahsulotSoni.text = "${product.soni}"
                MySharedPreferences.init(context)
                var umumiyNarx: Long = 0
                for (i in list) {
                    umumiyNarx+=i.price!!*i.soni!!
                }

                MyData.umumiyNarx.postValue(umumiyNarx)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemRvBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = list.size


    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position])
    }
}