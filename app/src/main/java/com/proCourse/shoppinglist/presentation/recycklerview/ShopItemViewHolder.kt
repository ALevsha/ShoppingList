package com.proCourse.shoppinglist.presentation.recycklerview

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.proCourse.shoppinglist.R

class ShopItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvName = view.findViewById<TextView>(R.id.tv_name)
    val tvCount = view.findViewById<TextView>(R.id.tv_count)
}