package com.proCourse.shoppinglist.presentation.recycklerview

import android.view.LayoutInflater
import android.view.ViewGroup
import com.proCourse.shoppinglist.R
import com.proCourse.shoppinglist.domain.model.ShopItem

class ShopListAdapter :
    androidx.recyclerview.widget.ListAdapter<ShopItem, ShopItemViewHolder>(ShopItemDiffCallback()) {

    /*androidx.recyclerview.widget.ListAdapter удобен тем, что скрывает всю логику работы со списками
    * нет необходимости за ними следить + есть свои методы для получения списка, элементов и т.п*/

    /*эта переменная var чтобы этот слушатель устанавливался при необходимости, а до этого был null
    так обычно делают, если пишут на Javathis method is called
                // when the item is moved.
    var onShopItemLongClickListener: OnShopItemLongClickListener? = null
    т.к OnShopItemClickListener - функциональный интерфейс с 1 методом, его можно заменить
    лямбда-выражением. Эту функцию можно создать и сохранить в переменную.
    Unit -> ничего не возвращает. Тип оформлен в знак вопроса => может лежать либо null,
    либо функция (ShopItem) -> Unit*/
    var onShopItemLongClickListener: ((ShopItem) -> Unit)? = null

    var onShopItemClickListener: ((ShopItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopItemViewHolder {
        // как создавать view
        val layout = when (viewType) {
            VIEW_TYPE_ENABLED -> R.layout.item_shop_enabled
            VIEW_TYPE_DISABLED -> R.layout.item_shop_disabled
            else -> throw RuntimeException("Unknown view type $viewType")
        }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ShopItemViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ShopItemViewHolder, position: Int) {
        // как вставить значения внутри этого view
        val shopItem = getItem(position)
        viewHolder.tvName.text = shopItem.name
        viewHolder.tvCount.text = shopItem.count.toString()
        viewHolder.itemView.setOnLongClickListener {
            //viewModel.changeEnableState(shopItem) нельзя в адаптере обращаться к методам activity
            // для выполнения логики используется связка интерфейс-реализация
            //onShopItemLongClickListener?.onShopItemLongClick(shopItem)
            onShopItemLongClickListener?.invoke(shopItem)
            // invoke - перегрузка операндов
            true
        }
        viewHolder.itemView.setOnClickListener {
            onShopItemClickListener?.invoke(shopItem)
        }
    }

    // onViewRecycled больше не нужен, т.к. ListAdapter сам занимается списками

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).enabled)
            VIEW_TYPE_ENABLED
        else
            VIEW_TYPE_DISABLED
    }



    companion object {
        // константы выводятся в объекте-компаньоне
        const val VIEW_TYPE_ENABLED = 1
        const val VIEW_TYPE_DISABLED = 0

        // размер пула подбирается в зависимости от мощности устройства и размера экрана устройства
        const val MAX_POOL_SIZE = 15
    }
}