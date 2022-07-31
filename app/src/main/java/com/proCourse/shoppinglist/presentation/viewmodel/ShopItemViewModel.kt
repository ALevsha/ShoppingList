package com.proCourse.shoppinglist.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.proCourse.shoppinglist.data.ShopListRepositoryImpl
import com.proCourse.shoppinglist.domain.model.ShopItem
import com.proCourse.shoppinglist.domain.usecase.AddShopItemUseCase
import com.proCourse.shoppinglist.domain.usecase.EditShopItemUseCase
import com.proCourse.shoppinglist.domain.usecase.GetShopItemUseCase
import com.proCourse.shoppinglist.domain.usecase.GetShopListUseCase

class ShopItemViewModel : ViewModel() {
    private val repository = ShopListRepositoryImpl

    private val getShopItemUseCase = GetShopItemUseCase(repository)
    private val addShopItemUseCase = AddShopItemUseCase(repository)
    private val editShopItemUseCase = EditShopItemUseCase(repository)

    fun getShopItem(shopItemId: Int) {
        getShopItemUseCase.getShopItem(shopItemId)
    }

    fun addShopItem(inputName: String?, inputCount: String?) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val fieldsValid = validateInput(name, count)
        if (fieldsValid){
            val shopItem = ShopItem(name, count, true)
            addShopItemUseCase.addShopItem(shopItem = shopItem)
        }
    }

    fun editShopItem(inputName: String?, inputCount: String?) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val fieldsValid = validateInput(name, count)
        if (fieldsValid) {
            val shopItem = ShopItem(name, count, true)
            editShopItemUseCase.editShopItem(shopItem)
        }
    }

    private fun parseName(inputName: String?): String{
        return inputName?.trim() ?: ""
    }

    private fun parseCount(inputCount: String?): Int{
        return try {
            inputCount?.trim()?.toInt() ?: 0
        } catch (e: Exception){
            Log.w("EditingCountValue", "Unavailable value: $inputCount")
            0
        }
    }

    private fun validateInput(name: String, count: Int): Boolean{
        var result = true
        if (name.isBlank()) {
            //TODO: show error input name
            result = false
        }
        if(count <= 0) {
            //TODO: show error input count
            result = false
        }
        return result
    }
}