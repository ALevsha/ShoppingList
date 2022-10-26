package com.proCourse.shoppinglist.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity - Entity класс используется для создания таблицы. По умолчанию в качестве имени таблицы
 * используется имя этого класса. Но мы можем указать свое имя, используя параметр tableName.
 *
 * Такую аннотацию нельзя использовать сразу в domain слое, т.к в таком случае, domain слой уже
 * будет зависеть от data слоя, что уже неправильно, с точки зрения чистой архитектуры
 *
 * По сути, это копия объекта ShopItem, но с необходимыми аннотациями
 */

@Entity(tableName = "shop_items")
data class ShopItemDBModel(
    /**
     * Мы уже знаем, как с помощью @PrimaryKey назначить какое-либо поле ключом.
     * Каждый Entity класс должен содержать хотя бы одно такое поле.
     * Даже если в классе всего одно поле.
     * У PrimaryKey есть параметр autoGenerate. Он позволяет включить для поля режим autoincrement,
     * в котором база данных сама будет генерировать значение, если вы его не укажете.
     *
     * Давать значение для id будем автоматически с помощью БД Room
     */
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val count: Int,
    val enabled: Boolean
)
