package com.proCourse.shoppinglist.presentation

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout
import com.proCourse.shoppinglist.R

@BindingAdapter("errorInputName")
fun bindErrorInputName(textInputLayout: TextInputLayout, isError: Boolean){
    val message = if (isError) {
        textInputLayout.context.getString(R.string.error_input_name)
    } else {
        null
    }
    textInputLayout.error = message //показ ошибки делается через объект контейнера для TextEdit
    //смотри в xml
}

@BindingAdapter("errorInputCount")
fun bindErrorInputCount(textInputLayout: TextInputLayout, isError: Boolean){
    val message = if (isError) {
        textInputLayout.context.getString(R.string.error_input_count)
    } else {
        null
    }
    textInputLayout.error = message //показ ошибки делается через объект контейнера для TextEdit
    //смотри в xml
}