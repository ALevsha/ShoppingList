package com.proCourse.shoppinglist.domain.model

data class ShopItem(
    /**
     * Обязательные (неизменяемые) поля указываются первыми
     */
    val name: String,
    val count: Int,
    val enabled: Boolean,
    /**
     * изменяемые после
     */
    var id: Int = UNDEFINED_ID // магическое число - плохая практика. Значит, что настоящий id не установлен,
                     // и надо его установить
){
    /**
     * для обозначения магических чисел используют константы, через companion object
     */

    companion object{
        const val UNDEFINED_ID = -1 // id еще не установлен
    }
}
