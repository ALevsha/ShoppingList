package com.proCourse.shoppinglist.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.proCourse.shoppinglist.R
import com.proCourse.shoppinglist.domain.model.ShopItem
import com.proCourse.shoppinglist.presentation.ShopItemActivity.Companion.newIntentAddItem
import com.proCourse.shoppinglist.presentation.ShopItemActivity.Companion.newIntentEditItem
import com.proCourse.shoppinglist.presentation.recycklerview.ShopListAdapter
import com.proCourse.shoppinglist.presentation.viewmodel.MainViewModel
import java.lang.RuntimeException

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var shopListAdapter: ShopListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupRecyclerView()
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.shopList.observe(this) {// подписка на liveData
            /*изменения логики - в RecyclerView...ListAdapter по другому присваиваются списки:
            * Запускается новый поток, в котором и происходят все вычисления относительно списков*/
            shopListAdapter.submitList(it)
        }
        val buttonAddItem = findViewById<FloatingActionButton>(R.id.button_add_shop_item)

        buttonAddItem.setOnClickListener {
            val intent = newIntentAddItem(this)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        val rvShopList = findViewById<RecyclerView>(R.id.rv_shop_list)
        // with используется, если происходит работа с одним элементом, чтоб его не упоминать
        with(rvShopList) {
            shopListAdapter = ShopListAdapter()
            adapter = shopListAdapter
            recycledViewPool.setMaxRecycledViews(
                ShopListAdapter.VIEW_TYPE_ENABLED,
                ShopListAdapter.MAX_POOL_SIZE
            )
            recycledViewPool.setMaxRecycledViews(
                ShopListAdapter.VIEW_TYPE_DISABLED,
                ShopListAdapter.MAX_POOL_SIZE
            )
        }

        setupLongClickListener()

        setupClickListener()

        setupTouchHelper(rvShopList)
    }

    private fun setupTouchHelper(rvShopList: RecyclerView) {
        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(
                0/*модификатор перемещения*/,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            )/*модификатор удаления*/ {
            /*перегрузка метода перемещения*/
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            /*Перегрузка метода удаления*/
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deleteShopItem: ShopItem =
                    /*для получения списка адаптера в ListAdapter используется поле currentList*/
                    shopListAdapter.currentList[viewHolder.adapterPosition]
                viewModel.deleteShopItem(deleteShopItem)
            }
        }).attachToRecyclerView(rvShopList)/*подключение TouchHelper'a к RecyclerView*/
    }

    private fun setupClickListener() {
        shopListAdapter.onShopItemClickListener = {
            val intent = newIntentEditItem(this, it.id)
            startActivity(intent)
        }
    }

    private fun setupLongClickListener() {
        // пример использования анонимного класса для реализации интерфейа
        /* shopListAdapter.onShopItemLongClickListener =
             object : ShopListAdapter.OnShopItemLongClickListener {
                 override fun onShopItemLongClick(shopItem: ShopItem) {
                     viewModel.changeEnableState(shopItem)
                 }
             }*/
        shopListAdapter.onShopItemLongClickListener =
            {
                viewModel.changeEnableState(it)
            }
    }
}


