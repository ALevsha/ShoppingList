package com.proCourse.shoppinglist.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import com.proCourse.shoppinglist.R
import com.proCourse.shoppinglist.databinding.ActivityShopItemBinding
import com.proCourse.shoppinglist.domain.model.ShopItem
import com.proCourse.shoppinglist.presentation.viewmodel.ShopItemViewModel

class ShopItemActivity : AppCompatActivity(), ShopItemFragment.OnEditingFinishListener {

    private var screenMode = MODE_UNKNOWN
    private var shopItemId = ShopItem.UNDEFINED_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_item)
        /*
        необходимо знать режим запуска
        */
        parseIntent()

        if (savedInstanceState == null) {
            /* эта проверка необходима, чтобы метод установки нового режима работы вызывался только
            * при первом вызове фрагмента*/
            // установка режимов работы
            launchRightMode()
        }
    }

    private fun launchRightMode() {
        // выбор фрагмента для отображения в зависимости от режима работы
        // методы возвращают фрагмент, который уже будет настроен на определенный
        // режим работы
        val fragment = when (screenMode) {
            /*
            Для фрагментов, как и для интентов лучше создавать статические фабричные методы,
            которые будут создавать фрагмент и возвращать новый его экземпляр.
            Методы реализуются в объекте-компаньоне фрагмента
             */
            MODE_EDIT   -> ShopItemFragment.newInstanceEditItem(shopItemId)
            MODE_ADD    -> ShopItemFragment.newInstanceAddItem()
            else        -> throw RuntimeException("Unknown screen mode $screenMode")
        }
    /* для работы с фрагментами используется класс supportFragmentManager
    (getSupportFragmentManager() Java). Для отображения фрагмента используется метод
    beginTransaction(). Т.к макетов раньше не отображалось, то используется метод add(),
    на вход которого идет id контейнера и сам фрагмент. Далее методом comit() транзакция
    будет запущена на выполнение
     */
    supportFragmentManager.beginTransaction()
        .replace(R.id.shop_item_container, fragment) // заменяет старый фрагмент на новый,
            // если старого не было, создает
        .commit()
    }

    private fun parseIntent() {
        if (!intent.hasExtra(EXTRA_SCREEN_MODE)) {
            throw RuntimeException("Param screen mode is absent")
        }

        val mode = intent.getStringExtra(EXTRA_SCREEN_MODE)
        if (mode != MODE_EDIT && mode != MODE_ADD) {
            throw RuntimeException("Unknown screen mode $mode")
        }
        screenMode = mode

        if (screenMode == MODE_EDIT) {
            if (!intent.hasExtra(EXTRA_SHOP_ITEM_ID)) {
                throw RuntimeException("Param shop item id is absent")
            }
            shopItemId = intent.getIntExtra(EXTRA_SHOP_ITEM_ID, ShopItem.UNDEFINED_ID)
        }
    }

    /*
    чтобы не ошибаться в EXTRA - полях интентов, их значения выносятся в константы.
    Они будут приватными. Для работы с ними в объекте-компаньоне будут созданы
    следующие методы, формирующие интенты для вызова др. activity в нужном режиме
     */
    companion object {
        private const val EXTRA_SCREEN_MODE = "extra_mode"
        private const val EXTRA_SHOP_ITEM_ID = "extra_shop_item_id"
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_ADD = "mode_add"
        private const val MODE_UNKNOWN = ""


        fun newIntentAddItem(context: Context): Intent {
            val intent = Intent(context, ShopItemActivity::class.java)
            intent.putExtra(EXTRA_SCREEN_MODE, MODE_ADD)
            return intent
        }

        fun newIntentEditItem(context: Context, shopItemId: Int): Intent {
            val intent = Intent(context, ShopItemActivity::class.java)
            intent.putExtra(EXTRA_SCREEN_MODE, MODE_EDIT)
            intent.putExtra(EXTRA_SHOP_ITEM_ID, shopItemId)
            return intent
        }
    }

    override fun onEditingFinishListener() {
        finish()
    }
}