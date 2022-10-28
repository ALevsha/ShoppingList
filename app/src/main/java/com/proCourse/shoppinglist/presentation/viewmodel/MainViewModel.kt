package com.proCourse.shoppinglist.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.proCourse.shoppinglist.data.ShopListRepositoryImpl
import com.proCourse.shoppinglist.domain.model.ShopItem
import com.proCourse.shoppinglist.domain.usecase.DeleteShopItemUseCase
import com.proCourse.shoppinglist.domain.usecase.EditShopItemUseCase
import com.proCourse.shoppinglist.domain.usecase.GetShopListUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


/**
 * Наследуемся от ViewModel, т.к. контекст не будет использоваться, иначе наследуемся от AndroidViewModel(application)
 */
class MainViewModel(application: Application): AndroidViewModel(application) {

    private val repository = ShopListRepositoryImpl(application) // неправильно, т.к. реализация зависит о data
                                                    // чего не должно быть, правильно будет
                                                    // с использованием инъекции зависимостей
                                                    // Автор считает, что presentation и data
                                                    // ничего не должны знать друг о друге

    // добавляем 3 useCase
    private val getShopListUseCase = GetShopListUseCase(repository)
    private val deleteShopItemUseCase = DeleteShopItemUseCase(repository)
    private val editShopItemUseCase = EditShopItemUseCase(repository)

    val shopList = getShopListUseCase.getShopList() // LiveData - хранилище данных,
                                                     // в который можно поместить какой либо объект
                                                     // и на него можно ПОДПИСАТЬСЯ для получения
                                                     // объектов, которые в него помещают

    /**
     * CoroutineScope - область выполнения корутины. На вход идет контекст корутины - спец
     * составляющая корутины. Им выступает объект Dispatchers, в котором указывается, в каком
     * потоке будет исполняться корутина:
     * Main - в главном потоке;
     * IO - в др. потоке, хорошо подходит для чтения/записи. Пул потоков м.б расширен до 64(настраиваетс)
     * Default - в др. потоке, подходит для долгих операций на одном потоке. Пул м.б расширен
     *      до количества ядер в процессоре
     * Unconfined - ХЗ
     */

//    private val scope = CoroutineScope(Dispatchers.IO)

    /**
     * для ViewModel был придуман свой scope, который не надо отменять в методе onCleared
     */

    fun deleteShopItem(shopItem: ShopItem){
        viewModelScope.launch {
            deleteShopItemUseCase.deleteShopItem(shopItem)
        }

    }

    fun changeEnableState(shopItem: ShopItem){
        // у data class метод copy переопределен
        val newItem = shopItem.copy(enabled = !shopItem.enabled) // т.к. enabled val
        viewModelScope.launch {
            editShopItemUseCase.editShopItem(newItem)
        }
    }

/*    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }*/
}