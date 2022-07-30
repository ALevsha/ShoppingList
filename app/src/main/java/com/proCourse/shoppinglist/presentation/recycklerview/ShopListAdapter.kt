package com.proCourse.shoppinglist.presentation.recycklerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.proCourse.shoppinglist.R
import com.proCourse.shoppinglist.domain.model.ShopItem

class ShopListAdapter: RecyclerView.Adapter<ShopListAdapter.ShopItemViewHolder>() {

    val list = listOf<ShopItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopItemViewHolder {
        // как создавать view
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shop_disabled,
            parent, false)
        return ShopItemViewHolder(view)

    }

    override fun onBindViewHolder(viewHolder: ShopItemViewHolder, position: Int) {
        // как вставить значения внутри этого view
        val shopItem = list[position]
        viewHolder.tvName.text = shopItem.name
        viewHolder.tvCount.text = shopItem.count.toString()
        viewHolder.itemView.setOnLongClickListener{
            //viewModel.changeEnableState(shopItem)
            true
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ShopItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        val tvName = view.findViewById<TextView>(R.id.tv_name)
        val tvCount = view.findViewById<TextView>(R.id.tv_count)
    }
}