package com.proCourse.shoppinglist.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import com.proCourse.shoppinglist.R
import com.proCourse.shoppinglist.domain.model.ShopItem
import com.proCourse.shoppinglist.presentation.viewmodel.ShopItemViewModel

class ShopItemFragment(
    /*
    * В конструктор класса, который наследуется от фрагмента нельзя передавать
    * переменные, т.к эти данные будут правильно отображены только 1 раз т.к,
    * в отличие от activity, фрагмент не пересоздается при закрытии.
    * Также Fragment является системным объектом, т.е его конструктор изменять нельзя
    * */
    private val screenMode: String = MODE_UNKNOWN,
    private val shopItemId: Int = ShopItem.UNDEFINED_ID
) : Fragment() {

    private lateinit var viewModel: ShopItemViewModel

    private lateinit var tilName: TextInputLayout
    private lateinit var tilCount: TextInputLayout
    private lateinit var etName: EditText
    private lateinit var etCount: EditText
    private lateinit var buttonSave: Button

    // В этом методе из макета создается view
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // вот здесь:
        return inflater.inflate(R.layout.fragment_shop_item, container, false)
    }


    // этот метод обозначает создание view на экране фрагмента и тот момент,
    // когда с этой view можно начинать работать
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*
        необходимо знать режим запуска
        */
        parseParams()
        viewModel = ViewModelProvider(this).get(ShopItemViewModel::class.java)
        // инициализация view элементов
        initViews(view)

        addTextChangeListeners()

        // установка режимов работы
        launchRightMode()

        // подписка на все объекты ViewModel
        observeViewModel()
    }

    private fun observeViewModel() {
        // подписка на выявление ошибки поля ввода count
        // viewLifecycleOwner ссылка на ЖЦ подключенной activity
        viewModel.errorInputCount.observe(viewLifecycleOwner) {
            val message = if (it) {
                getString(R.string.error_input_count)
            } else {
                null
            }
            tilCount.error = message //показ ошибки делается через объект контейнера для TextEdit
            //смотри в xml
        }

        // подписка на выявление ошибки поля ввода name
        viewModel.errorInputName.observe(viewLifecycleOwner) {
            val message = if (it) {
                getString(R.string.error_input_name)
            } else {
                null
            }
            tilName.error = message
        }

        // подписка на выявление завершения работы activity
        viewModel.shouldCloseScreen.observe(viewLifecycleOwner) {
            activity?.onBackPressed()
            /*
            * имитация нажатия кнопки назад
            * activity (getActivity() в Java) дает ссылку на прикрепленное к фрагменту
            * activity, которая может быть null, если activity не прикреплен к фрагменту
            * или удален из activity, requireActivity() также дает эту ссылку,
            * но при null выдает исключение
            * по аналогии есть также context (getContext()) - requireContext()
            *                        view (getView()) - requireView()
            * */
        }
    }

    private fun launchRightMode() {
        when (screenMode) {
            MODE_EDIT -> launchEditMode()
            MODE_ADD -> launchAddMode()
        }
    }

    private fun addTextChangeListeners() {
        // установка слушателей изменения текста
        etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorInputName()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        etCount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorInputCount()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    // действия при редактировании объекта
    private fun launchEditMode() {
        viewModel.getShopItem(shopItemId)
        viewModel.shopItem.observe(viewLifecycleOwner) {
            etName.setText(it.name)
            etCount.setText(it.count.toString())
        }
        buttonSave.setOnClickListener {
            viewModel.editShopItem(etName.text?.toString(), etCount.text?.toString())
        }
    }

    // действия при создании объекта
    private fun launchAddMode() {
        buttonSave.setOnClickListener {
            viewModel.addShopItem(etName.text?.toString(), etCount.text?.toString())
        }
    }

    private fun parseParams() {
        /* фрагмент - отдельный объект, он не может зависеть от определенных полей view,
        => от полей интента активити тоже
        * requireActivity() возвращает прикрепленную к фрагменту activity*/
        /*if (!requireActivity().intent.hasExtra(EXTRA_SCREEN_MODE)) {
            throw RuntimeException("Param screen mode is absent")
        }*/
        if (screenMode != MODE_EDIT && screenMode != MODE_ADD) {
            throw RuntimeException("Param screen mode is absent")
        }
        if (screenMode == MODE_EDIT && shopItemId == ShopItem.UNDEFINED_ID) {
            throw RuntimeException("Param shop item is absent")
        }


    }

    private fun initViews(view: View) {
        tilName = view.findViewById(R.id.til_name)
        tilCount = view.findViewById(R.id.til_count)
        etName = view.findViewById(R.id.et_name)
        etCount = view.findViewById(R.id.et_count)
        buttonSave = view.findViewById(R.id.save_button)
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

        fun newInstanceAddItem(): ShopItemFragment{
            return ShopItemFragment(MODE_ADD)
        }

        fun newInstanceEditItem(shopItemId: Int): ShopItemFragment{
            return ShopItemFragment(MODE_EDIT,shopItemId)
        }

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
