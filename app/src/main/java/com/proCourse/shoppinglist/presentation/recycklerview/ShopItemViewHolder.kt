package com.proCourse.shoppinglist.presentation.recycklerview

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * Потому что класс ViewHolder не умеет работать с DataBinding, объект DataBinding подается
 * на вход
 */
//                                                       наследуемся от ViewHolder, которому на вход
//                       объект на вход                  приходит view
class ShopItemViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)