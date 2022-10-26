package com.proCourse.shoppinglist.data

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.proCourse.shoppinglist.data.db.AppDatabase
import com.proCourse.shoppinglist.data.db.ShopListMapper
import com.proCourse.shoppinglist.domain.model.ShopItem
import com.proCourse.shoppinglist.domain.repository.ShopListRepository
import java.lang.RuntimeException
import kotlin.random.Random

class ShopListRepositoryImpl(
    // т.к для класса БД нужен application, реализация д.б обычным классом
    // и на вход передается application
    application: Application
) : ShopListRepository {

    private val shopListDao = AppDatabase.getInstance(application).shopListDao()
    private val mapper = ShopListMapper()


    override fun addShopItem(shopItem: ShopItem) {
        shopListDao.addShopItem(mapper.mapEntityToDbModel(shopItem))
    }

    override fun deleteShopItem(shopItem: ShopItem) {
        shopListDao.deleteShopItem(mapper.mapEntityToDbModel(shopItem).id)
    }

    override fun editShopItem(shopItem: ShopItem) {
        /**
         * повтор, т.к в Dao запрос поразумевает в случае коллизии замену элемента
         * */
        shopListDao.addShopItem(mapper.mapEntityToDbModel(shopItem))
    }

    override fun getShopItem(shopItemId: Int): ShopItem {
        val dbModel = shopListDao.getShopItem(shopItemId)
        return mapper.mapDbModelToEntity(dbModel)
    }

    override fun getShopList(): LiveData<List<ShopItem>> =
        /**
         * Transformations.map аналогичен 1 способу, но так удобнее
         * */
        Transformations.map(shopListDao.getShopList()) {
            mapper.mapListDbModelToListEntity(it)
/* первый способ через класс MediatorLiveData
= MediatorLiveData<List<ShopItem>>().apply {
        addSource(shopListDao.getShopList()){
            value = mapper.mapListDbModelToListEntity(it)
        }*/
        }

}