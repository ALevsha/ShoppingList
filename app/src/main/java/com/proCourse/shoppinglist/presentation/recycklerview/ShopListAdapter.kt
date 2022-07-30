package com.proCourse.shoppinglist.presentation.recycklerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.proCourse.shoppinglist.R
import com.proCourse.shoppinglist.domain.model.ShopItem

class ShopListAdapter : RecyclerView.Adapter<ShopListAdapter.ShopItemViewHolder>() {

    var shopList = listOf<ShopItem>()
        // переопределение сеттера при установке значения снаружи
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopItemViewHolder {
        // как создавать view
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_shop_disabled,
            parent,
            false
        )
        return ShopItemViewHolder(view)

    }

    override fun onBindViewHolder(viewHolder: ShopItemViewHolder, position: Int) {
        // как вставить значения внутри этого view
        val shopItem = shopList[position]
        val status = if (shopItem.enabled) {
            "Active"
        } else {
            "Not active"
        }
        viewHolder.itemView.setOnLongClickListener {
            //viewModel.changeEnableState(shopItem)
            true
        }
        if (shopItem.enabled) {
            viewHolder.tvName.setTextColor(
                ContextCompat.getColor(
                    viewHolder.itemView.context,
                    android.R.color.holo_red_light
                )
            )
            viewHolder.tvName.text = "${shopItem.name} $status"
            viewHolder.tvCount.text = shopItem.count.toString()
        }
    }

    override fun onViewRecycled(viewHolder: ShopItemViewHolder) {
        // действие, выполняемое при переиспользовании объекта адаптера
        // (ставим значения пол умолчанию)
        super.onViewRecycled(viewHolder)
        viewHolder.tvName.setTextColor(
            ContextCompat.getColor(
                viewHolder.itemView.context,
                android.R.color.white
            )
        )
        viewHolder.tvName.text = ""
        viewHolder.tvCount.text = ""
    }

    override fun getItemCount(): Int {
        return shopList.size
    }

    override fun getItemViewType(position: Int): Int {
        // возвращает тип view по его позициии. Он нужен, когда для разных объектов разные layout'ы
        return position
    }

    class ShopItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName = view.findViewById<TextView>(R.id.tv_name)
        val tvCount = view.findViewById<TextView>(R.id.tv_count)
    }
}