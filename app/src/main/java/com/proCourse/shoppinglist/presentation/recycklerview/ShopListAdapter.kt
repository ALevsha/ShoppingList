package com.proCourse.shoppinglist.presentation.recycklerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.proCourse.shoppinglist.R
import com.proCourse.shoppinglist.databinding.ItemShopDisabledBinding
import com.proCourse.shoppinglist.databinding.ItemShopEnabledBinding
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
        // как создавать view при разном viewType
        val layout = when (viewType) {
            VIEW_TYPE_ENABLED -> R.layout.item_shop_enabled
            VIEW_TYPE_DISABLED -> R.layout.item_shop_disabled
            else -> throw RuntimeException("Unknown view type $viewType")
        }

        /**
         * viewHolder хранит ссылку на саму view, а также на элементы внутри самой view.
         * DataBinding также хранит эти ссылки. Но RecyclerView не умеет с ними работать, поэтому
         * объект Binding передается внутрь viewHolder, а далее уже берется для работы с ним
         *
         * При создании binding используется класс DataBindingUtil, куда в метод inflate, который
         * работает с родительским классом ViewDataBinding, передается:
         */
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context), // вставщик xml формы в view
            layout,                             // саму форму
            parent,                             // родителя
            false                   // также не пристыковываем форму
        )
        return ShopItemViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ShopItemViewHolder, position: Int) {
        // как вставить значения внутри этого view
        val shopItem = getItem(position)

        /**
         * В случае с общими для всех viewType функций можно использовать приведенный к общему
         * типу ViewDataBinding объект binding
         */
        val binding = viewHolder.binding

        binding.root.setOnLongClickListener {
            //viewModel.changeEnableState(shopItem) нельзя в адаптере обращаться к методам activity
            // для выполнения логики используется связка интерфейс-реализация
            //onShopItemLongClickListener?.onShopItemLongClick(shopItem)
            onShopItemLongClickListener?.invoke(shopItem)
            // invoke - перегрузка операндов
            true
        }
        binding.root.setOnClickListener {
            onShopItemClickListener?.invoke(shopItem)
        }
        /**
         * однако в частных случаях, когда возможно другое поведение, сравнивают определенный класс
         * DataBinding и действуют, в зависимости от разных случаев по разному. Тут не явное
         * приведение типов, а сравнение с действительным наименованием класса. Иначе не будет
         * доступа к ссылкам внутренних view, т.к у родительского класса их нет
         */
        when (binding) {
            is ItemShopDisabledBinding -> {
                binding.shopItem = shopItem
            }
            is ItemShopEnabledBinding -> {
                binding.shopItem = shopItem
            }
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