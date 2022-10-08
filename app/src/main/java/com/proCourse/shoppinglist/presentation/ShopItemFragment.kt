package com.proCourse.shoppinglist.presentation

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import com.proCourse.shoppinglist.R
import com.proCourse.shoppinglist.databinding.FragmentShopItemBinding
import com.proCourse.shoppinglist.domain.model.ShopItem
import com.proCourse.shoppinglist.presentation.viewmodel.ShopItemViewModel

/*
    * В конструктор класса, который наследуется от фрагмента нельзя передавать
    * переменные, т.к эти данные будут правильно отображены только 1 раз т.к,
    * при закрытии (переворот экрана) система вызывает конструктор по умолчанию
    * без параметров, который является системным.
    */
class ShopItemFragment : Fragment() {

    private var _binding: FragmentShopItemBinding? = null
    private val binding: FragmentShopItemBinding
        get() = _binding ?: throw RuntimeException("FragmentShopItemBinding is null")

    private lateinit var viewModel: ShopItemViewModel
    private lateinit var onEditingFinishListener: OnEditingFinishListener
    /*
    для инициации действия активити из фрагмента используется открытый интерфейс, активити реализует
    метод этого интерфейса и по его вызову выполняет его. Например показ уведомления о выполнении
    действия. Т.о. активити, использующие фрагмент должны реализовывать его, иначе принято
    выбрасывать исключение, в котором будет указано на необходимость реализации интерфейса.
    Именно поэтому она не может быть нуллабельной
     */

//    private lateinit var tilName: TextInputLayout
//    private lateinit var tilCount: TextInputLayout
//    private lateinit var etName: EditText
//    private lateinit var etCount: EditText
//    private lateinit var buttonSave: Button

    private var screenMode: String = MODE_UNKNOWN
    private var shopItemId: Int = ShopItem.UNDEFINED_ID

    /*
    * очень важный метод, обозначающий момент прикрепления фрагмента к активити. Контекстом является
    * сама активити, к которой происходит прикрепление. Именно поэтому активити, использующая фрагмент
    * должна реализовывать интерфейс взаимодействия
    * */
    override fun onAttach(context: Context) {
        Log.d("LifeCycle", "onAttach")
        super.onAttach(context)
        if(context is OnEditingFinishListener) // если интерфейс реализован,
                                               // явно приводим к типу интерфейса
            onEditingFinishListener = context
        else
            throw RuntimeException("Activity must implement OnEditingFinishListener")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("LifeCycle", "onCreate")
        super.onCreate(savedInstanceState)
        // перенос в onCreate т.к параметры должны быть известны до создания view
        parseParams()
    }

    // В этом методе из макета создается view
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // вот здесь:
        Log.d("LifeCycle", "onCreateView")
        _binding = FragmentShopItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    // этот метод обозначает создание view на экране фрагмента и тот момент,
    // когда с этой view можно начинать работать
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("LifeCycle", "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        /*
        необходимо знать режим запуска
        */
//        parseParams()
        viewModel = ViewModelProvider(this).get(ShopItemViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        // инициализация view элементов
//        initViews(view)

        addTextChangeListeners()

        // установка режимов работы
        launchRightMode()

        // подписка на все объекты ViewModel
        observeViewModel()
    }

    private fun observeViewModel() {
        // подписка на выявление ошибки поля ввода count
        // viewLifecycleOwner ссылка на ЖЦ подключенной activity
        /*viewModel.errorInputCount.observe(viewLifecycleOwner) {
            val message = if (it) {
                getString(R.string.error_input_count)
            } else {
                null
            }
            binding.tilCount.error = message //показ ошибки делается через объект контейнера для TextEdit
            //смотри в xml
        }

        // подписка на выявление ошибки поля ввода name
        viewModel.errorInputName.observe(viewLifecycleOwner) {
            val message = if (it) {
                getString(R.string.error_input_name)
            } else {
                null
            }
            binding.tilName.error = message
        }*/

        // подписка на выявление завершения работы activity
        viewModel.shouldCloseScreen.observe(viewLifecycleOwner) {
            // инициация действия
            onEditingFinishListener.onEditingFinishListener()
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
        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorInputName()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.etCount.addTextChangedListener(object : TextWatcher {
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
        /*viewModel.shopItem.observe(viewLifecycleOwner) {
            binding.etName.setText(it.name)
            binding.etCount.setText(it.count.toString())
        }*/
        binding.saveButton.setOnClickListener {
            viewModel.editShopItem(binding.etName.text?.toString(), binding.etCount.text?.toString())
        }
    }

    // действия при создании объекта
    private fun launchAddMode() {
        binding.saveButton.setOnClickListener {
            viewModel.addShopItem(binding.etName.text?.toString(), binding.etCount.text?.toString())
        }
    }

    private fun parseParams() {
        /* фрагмент - отдельный объект, он не может зависеть от определенных полей view,
        => от полей интента активити тоже
        * requireActivity() возвращает прикрепленную к фрагменту activity*/
        /*if (!requireActivity().intent.hasExtra(EXTRA_SCREEN_MODE)) {
            throw RuntimeException("Param screen mode is absent")
        }*/

        // здесь применим requireArguments() т.к с точки зрения архитектуры
        // null прилететь не может
        val args = requireArguments()
        // Bundle().containsKey() говорит, есть ли ключ в словаре?
        if (!args.containsKey(SCREEN_MODE)) {
            throw RuntimeException("Param screen mode is absent")
        }

        val mode = args.getString(SCREEN_MODE)
        if (mode != MODE_EDIT && mode != MODE_ADD) {
            throw RuntimeException("Unknown screen mode $mode")
        }
        screenMode = mode

        if (screenMode == MODE_EDIT) {
            if (!args.containsKey(SHOP_ITEM_ID)) {
                throw RuntimeException("Param shop item id is absent")
            }
            shopItemId = args.getInt(SHOP_ITEM_ID, ShopItem.UNDEFINED_ID)
        }
    }

//    private fun initViews(view: View) {
//        tilName = view.findViewById(R.id.til_name)
//        tilCount = view.findViewById(R.id.til_count)
//        etName = view.findViewById(R.id.et_name)
//        etCount = view.findViewById(R.id.et_count)
//        buttonSave = view.findViewById(R.id.save_button)
//    }

    // вот интерфейс взаимодействия

    interface OnEditingFinishListener{
        fun onEditingFinishListener()
    }

    /*
    чтобы не ошибаться в EXTRA - полях интентов, их значения выносятся в константы.
    Они будут приватными. Для работы с ними в объекте-компаньоне будут созданы
    следующие методы, формирующие интенты для вызова др. activity в нужном режиме
     */
    companion object {
        private const val SCREEN_MODE = "extra_mode"
        private const val SHOP_ITEM_ID = "extra_shop_item_id"
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_ADD = "mode_add"
        private const val MODE_UNKNOWN = ""

        // статический метод по созданию экземпляра объекта
        fun newInstanceAddItem(): ShopItemFragment{
            // метод в стиле Kotlin
            return ShopItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_ADD)
                }
            }
        }

        fun newInstanceEditItem(shopItemId: Int): ShopItemFragment{
            // метод не совсем в стиле Java
            val args = Bundle().apply {
                putString(SCREEN_MODE, MODE_EDIT)
                putInt(SHOP_ITEM_ID, shopItemId)
            }
            val fragment = ShopItemFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onStart() {
        Log.d("LifeCycle", "onStart")
        super.onStart()
    }

    override fun onResume() {
        Log.d("LifeCycle", "onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.d("LifeCycle", "onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d("LifeCycle", "onStop")
        super.onStop()
    }

    override fun onDestroyView() {
        Log.d("LifeCycle", "onDestroyView")
        _binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.d("LifeCycle", "onDestroy")
        super.onDestroy()
    }

    override fun onDetach() {
        Log.d("LifeCycle", "onDetach")
        super.onDetach()
    }
}
