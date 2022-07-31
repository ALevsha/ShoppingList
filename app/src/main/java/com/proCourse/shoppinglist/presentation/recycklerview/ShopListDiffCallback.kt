package com.proCourse.shoppinglist.presentation.recycklerview

import androidx.recyclerview.widget.DiffUtil
import com.proCourse.shoppinglist.domain.model.ShopItem

class ShopListDiffCallback(
    private val oldList: List<ShopItem>,
    private val newList: List<ShopItem>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    /**
     * Если поменялся объект, но id остался такой же.
     * То адаптер будет знать, что это тот же объект, но с измененными полями
     */
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    /**
     * Проверяет содержимое объекта, поменялись ли его поля.
     * Если поля изменились, объект надо перерисовать
     */
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}