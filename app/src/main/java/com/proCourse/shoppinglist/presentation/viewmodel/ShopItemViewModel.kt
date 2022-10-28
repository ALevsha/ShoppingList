package com.proCourse.shoppinglist.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.proCourse.shoppinglist.data.ShopListRepositoryImpl
import com.proCourse.shoppinglist.domain.model.ShopItem
import com.proCourse.shoppinglist.domain.usecase.AddShopItemUseCase
import com.proCourse.shoppinglist.domain.usecase.EditShopItemUseCase
import com.proCourse.shoppinglist.domain.usecase.GetShopItemUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ShopItemViewModel(application: Application ) : AndroidViewModel(application) {
    private val repository = ShopListRepositoryImpl(application)

    private val getShopItemUseCase = GetShopItemUseCase(repository)
    private val addShopItemUseCase = AddShopItemUseCase(repository)
    private val editShopItemUseCase = EditShopItemUseCase(repository)

    // объект LiveData для отображения ошибки ввода имени. Через нее общаются view с viewModel

    // для отделения переменной от использования в других классах
    // в Kotlin используют следующий метод
    private val _errorInputName =
        MutableLiveData<Boolean>() // приватная переменная для местного класса
    val errorInputName: LiveData<Boolean> // публичная переменная для других классов.
        // в классе LiveData сеттеры и геттеры имеют модификатор protected
        // и, => изменить значение нельзя
        get() = _errorInputName // с геттером, которая возвращает значение приватной

    // объект LiveData для отображения ошибки ввода количества. Через нее общаются view с viewModel
    private val _errorInputCount = MutableLiveData<Boolean>()
    val errorInputCount: LiveData<Boolean>
        get() = _errorInputCount

    // объект LiveData для получения объекта ShopItem по id. Через нее общаются view с viewModel
    private val _shopItem = MutableLiveData<ShopItem>()
    val shopItem: LiveData<ShopItem>
        get() = _shopItem

    // объект LiveData для ожидания окончания всех операций с объектом ShopItem.
    // Через нее общаются view с viewModel
    private val _shouldCloseScreen = MutableLiveData<Unit>()
    val shouldCloseScreen: LiveData<Unit>
        get() = _shouldCloseScreen

//    private val scope = CoroutineScope(Dispatchers.IO)

    fun getShopItem(shopItemId: Int) {
        viewModelScope.launch {
            val item = getShopItemUseCase.getShopItem(shopItemId)
            /**
             * LiveData.value исполняется только на главном потоке, postValue - в любом
             */
            _shopItem.postValue(item)
        }

    }

    fun addShopItem(inputName: String?, inputCount: String?) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val fieldsValid = validateInput(name, count)
        if (fieldsValid) {
            viewModelScope.launch {
                val shopItem = ShopItem(name, count, true)
                addShopItemUseCase.addShopItem(shopItem = shopItem)
                finishWork()
            }

        }
    }

    fun editShopItem(inputName: String?, inputCount: String?) {
        val name = parseName(inputName)
        val count = parseCount(inputCount)
        val fieldsValid = validateInput(name, count)
        if (fieldsValid) {
            _shopItem.value?.let {// если объект получен успешно, и не == null,
                // выполнится лямбда в let {}
                viewModelScope.launch {
                    val item = it.copy(name = name, count = count)
                    editShopItemUseCase.editShopItem(item)
                    finishWork()
                }

            }
        }
    }

    private fun parseName(inputName: String?): String {
        return inputName?.trim() ?: ""
    }

    private fun parseCount(inputCount: String?): Int {
        return try {
            inputCount?.trim()?.toInt() ?: 0
        } catch (e: Exception) {
            Log.w("EditingCountValue", "Unavailable value: $inputCount")
            0
        }
    }

    private fun validateInput(name: String, count: Int): Boolean {
        var result = true
        if (name.isBlank()) {
            _errorInputName.value = true
            result = false
        }
        if (count <= 0) {
            _errorInputCount.value = true
            result = false
        }
        return result
    }

    // метод будет вызываться из activity, его надо оставить публичным
    fun resetErrorInputName() {
        _errorInputName.value = false
    }

    fun resetErrorInputCount() {
        _errorInputCount.value = false
    }

    private fun finishWork() {
        _shouldCloseScreen.postValue(Unit)
    }

    /*override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }*/
}