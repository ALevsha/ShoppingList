package com.proCourse.shoppinglist.presentation.recycklerview

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.proCourse.shoppinglist.R
import com.proCourse.shoppinglist.domain.model.ShopItem
import java.lang.RuntimeException

class ShopListAdapter : RecyclerView.Adapter<ShopListAdapter.ShopItemViewHolder>() {

    var count = 0
    var shopList = listOf<ShopItem>()
        // переопределение сеттера при установке значения снаружи
        set(value) {
            // создание коллбэка для сравнения старого и нового списков
            val callback = ShopListDiffCallback(shopList, value)
            // создание объекта со всеми изменениями(DiffUtil.calculateDiff(callback) их сичтает)
            val diffResult = DiffUtil.calculateDiff(callback)
            // применение изменений к адаптеру(в этом случае адаптер - this)
            diffResult.dispatchUpdatesTo(this)
            field = value
        }

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
        Log.d("onBindViewHolder", "number ${++count}")
        val shopItem = shopList[position]
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

    override fun onViewRecycled(viewHolder: ShopItemViewHolder) {
        // действие, выполняемое при переиспользовании объекта адаптера
        // (ставим значения пол умолчанию)
        super.onViewRecycled(viewHolder)
        viewHolder.tvName.text = ""
        viewHolder.tvCount.text = ""
    }

    override fun getItemCount(): Int {
        return shopList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (shopList[position].enabled)
            VIEW_TYPE_ENABLED
        else
            VIEW_TYPE_DISABLED
    }

    class ShopItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName = view.findViewById<TextView>(R.id.tv_name)
        val tvCount = view.findViewById<TextView>(R.id.tv_count)
    }

    interface OnShopItemLongClickListener {

        fun onShopItemLongClick(shopItem: ShopItem)
    }

    interface OnShopItemClickListener {

        fun onShopItemClick(shopItem: ShopItem)
    }

    companion object {
        // константы выводятся в объекте-компаньоне
        const val VIEW_TYPE_ENABLED = 1
        const val VIEW_TYPE_DISABLED = 0

        // размер пула подбирается в зависимости от мощности устройства и размера экрана устройства
        const val MAX_POOL_SIZE = 15
    }
}