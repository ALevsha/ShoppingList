package com.proCourse.shoppinglist.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
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

class MainActivity : AppCompatActivity(), ShopItemFragment.OnEditingFinishListener{

    // TODO - внедрить в приложение DataBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var shopListAdapter: ShopListAdapter
    private var fragmentContainerView : FragmentContainerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fragmentContainerView = findViewById(R.id.shop_item_container)
        setupRecyclerView()
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.shopList.observe(this) {// подписка на liveData
            /*изменения логики - в RecyclerView...ListAdapter по другому присваиваются списки:
            * Запускается новый поток, в котором и происходят все вычисления относительно списков*/
            shopListAdapter.submitList(it)
        }
        val buttonAddItem = findViewById<FloatingActionButton>(R.id.button_add_shop_item)

        buttonAddItem.setOnClickListener {
            if (isOnePaneMode()) {
                val intent = newIntentAddItem(this)
                startActivity(intent)
            }
            else{
                val fragment = ShopItemFragment.newInstanceAddItem()
                launchFragment(fragment)
            }
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

    override fun onEditingFinishListener() {
        Toast.makeText(this,"Success",Toast.LENGTH_SHORT).show()
        supportFragmentManager.popBackStack()
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
            if (isOnePaneMode()) {
                val intent = newIntentEditItem(this, it.id)
                startActivity(intent)
            }
            else{
                val fragment = ShopItemFragment.newInstanceEditItem(it.id)
                launchFragment(fragment)
            }
        }
    }

    private fun launchFragment(fragment: Fragment) {
        supportFragmentManager.popBackStack() // очистит backStack, если там что-то было
        supportFragmentManager.beginTransaction()
            .replace(R.id.shop_item_container, fragment)
            /*
            при использовании имен фрагментов в методе addToBackStack() каждому фрагменту
             присваивается переданное имя, которое можно использовать в системных методах типа
             onBackPressed(<name> <- вот эта метка, <флаг> <- значение этого флага определяет
             порядок удаления элементов из backStack. 0 - то элементы с именем <name> не будут
             удалены...)
             */
            .addToBackStack(null)
                /* при использовании фрагмена в обычном режиме,  ShopItemActivity добавляется
                в backStack. В логике фрагмента прописано, что при окончании работы для выхода
                из activity по окончании работы используется имитация нажатия кнопки назад.
                В этом случае ShopItemActivity будет закрыта и в backStack на верхний уровень снова
                вернется MainActivity. При альбомной ориентации чтобы добавить вызванный фрагмент
                в стек надо использовать метод addToBackStack(<имя_фрагмента?>)
                 */
            .commit()
    }

    private fun isOnePaneMode() = fragmentContainerView == null

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


