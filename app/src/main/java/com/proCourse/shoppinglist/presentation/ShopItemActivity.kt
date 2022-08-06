package com.proCourse.shoppinglist.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.proCourse.shoppinglist.R
import com.proCourse.shoppinglist.domain.model.ShopItem
import com.proCourse.shoppinglist.presentation.viewmodel.ShopItemViewModel
import java.lang.RuntimeException

class ShopItemActivity : AppCompatActivity() {

    private lateinit var viewModel: ShopItemViewModel

    private lateinit var tilName: TextInputLayout
    private lateinit var tilCount: TextInputLayout
    private lateinit var etName: EditText
    private lateinit var etCount: EditText
    private lateinit var buttonSave: Button

    private var screenMode = MODE_UNKNOWN
    private var shopItemId = ShopItem.UNDEFINED_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_item)
        /*
        необходимо знать режим запуска
        */
        parseIntent()
        viewModel = ViewModelProvider(this).get(ShopItemViewModel::class.java)
        initViews()


        etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                tilName.error = null
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        etCount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                tilCount.error = null
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
        when (screenMode) {
            MODE_EDIT -> launchEditMode()
            MODE_ADD -> launchAddMode()
        }
    }

    private fun launchEditMode() {
        viewModel.getShopItem(shopItemId)
        val oldName = viewModel.shopItem.value?.name.toString()
        etName.setText(oldName)
        val oldCount = viewModel.shopItem.value?.count.toString()
        etCount.setText(oldCount.toString())
        buttonSave.setOnClickListener {
            val newName = etName.text.toString()
            val newCount = etCount.text.toString()
            if (oldName != newName ||
                oldCount != newCount
            ) {
                viewModel.editShopItem(newName, newCount)
                when {
                    viewModel.errorInputName.value == true
                            && viewModel.errorInputCount.value == true -> {
                        tilName.error = "Unavailable name"
                        tilCount.error = "Unavailable count"
                    }
                    viewModel.errorInputName.value == true -> tilName.error = "Unavailable name"
                    viewModel.errorInputCount.value == true -> tilCount.error = "Unavailable count"
                    else -> {

                        finish()
                    }
                }
            }
        }
    }

    private fun launchAddMode() {
        buttonSave.setOnClickListener {
            val name = etName.text.toString()
            val count = etCount.text.toString()
            viewModel.addShopItem(name, count)
            when {
                viewModel.errorInputName.value == true
                        && viewModel.errorInputCount.value == true -> {
                    tilName.error = "Unavailable name"
                    tilCount.error = "Unavailable count"
                }
                viewModel.errorInputName.value == true -> tilName.error = "Unavailable name"
                viewModel.errorInputCount.value == true -> tilCount.error = "Unavailable count"
                else -> finish()
            }
        }
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

    private fun initViews() {
        tilName = findViewById(R.id.til_name)
        tilCount = findViewById(R.id.til_count)
        etName = findViewById(R.id.et_name)
        etCount = findViewById(R.id.et_count)
        buttonSave = findViewById(R.id.save_button)
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
}