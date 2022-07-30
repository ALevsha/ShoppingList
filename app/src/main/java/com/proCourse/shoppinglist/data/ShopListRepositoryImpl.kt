package com.proCourse.shoppinglist.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.proCourse.shoppinglist.domain.model.ShopItem
import com.proCourse.shoppinglist.domain.repository.ShopListRepository
import java.lang.RuntimeException
import kotlin.random.Random

/**
 * Класс является object, чтобы сделать класс SingleTone'ом,
 * т.е. обращение было к одному и тому же экземпляру. Чтобы обращаться к нему со всех экранов.
 * В этом проекте есть схема внутри mainActivity.
 */

object ShopListRepositoryImpl: ShopListRepository {

    private val shopListLD = MutableLiveData<List<ShopItem>>()
    private val shopList = sortedSetOf<ShopItem>({ o1, o2 -> o1.id.compareTo(o2.id)})
            // создание сортированного списка.
            // В лямбде передается параметр, по которому идет сортировка

    private var autoIncrementId = 0

    /**
     * Для теста чтобы после создания объекта были сразу
     * добавлены элементы, или вызывались любые методы
     */
    init {
       for (i in 0 until 10000){
           val item = ShopItem("Name $i", i, Random.nextBoolean())
           addShopItem(item)
       }
    }

    override fun addShopItem(shopItem: ShopItem) {
        if (shopItem.id == ShopItem.UNDEFINED_ID) // метод используется в др. методе. проверка,
            // чтобы не менять, если id уже есть
            shopItem.id = autoIncrementId++
        shopList.add(shopItem)
        updateList()
    }

    override fun deleteShopItem(shopItem: ShopItem) {
        shopList.remove(shopItem)
        updateList()
    }

    override fun editShopItem(shopItem: ShopItem) {
        val oldElement = getShopItem(shopItem.id)
        shopList.remove(oldElement)
        addShopItem(shopItem)
    }

    /**
     * Если для приложения норм, что может прийти null, то можно сделать возвращаемый элемент
     * нуллабельным и обработать этот момент по-другому
     */
    override fun getShopItem(shopItemId: Int): ShopItem {
        return shopList.find { it.id == shopItemId
        } ?: throw RuntimeException("Element with id $shopItemId not found")
    }

    override fun getShopList(): LiveData<List<ShopItem>> {
        return shopListLD
    }

    // метод обновления liveData: LiveData.значение = списокОбъектов.копияСпискаОбъектов
    private fun updateList(){
        shopListLD.value = shopList.toList()
    }

}