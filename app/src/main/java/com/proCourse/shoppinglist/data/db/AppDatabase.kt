package com.proCourse.shoppinglist.data.db

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// класс создания БД: класс полей               версия      ???
@Database(entities = [ShopItemDBModel::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    /**
     * функция, возвращающая экземпляр интерфейса Dao
     */
    abstract fun shopListDao(): ShopListDao

    /**
     * БД должна быть синглтоном (одна на всё приложение):
     */
    companion object{

        // экземпляр БД
        private var INSTANCE: AppDatabase? = null
        // объект синхронизации
        private val LOCK = Any()
        private const val DB_NAME = "shop_item.db"

        fun getInstance(application: Application): AppDatabase {
            // если объект не null, возвращаем, иначе исполняется то, что стоит после скобок
            INSTANCE?.let {
                return it
            }
            /**
             * Если 2 или более потока захотят получить экземпляр БД, то для этого вводится объект
             * синхронизации для того, чтобы второй поток не переинициализировал БД. Блок
             * синхронизации заставляет потоки ожидать, пока впередиидущий поток не закончит
             * свое выполнение.
             * Такой способ реализации singleTone называется DoubleCheck
             */
            synchronized(LOCK){
                INSTANCE?.let {
                    return it
                }
                // построение БД
                val db = Room.databaseBuilder(
                    application.applicationContext,                // контекст
                    AppDatabase::class.java,    // класс БД
                    DB_NAME                     // имя БД
                )
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = db
                // возвращается не INSTANCE т.к он является нуллабельным объектом, а возвращаемый
                // экземпляр не должен быть нуллабельным
                return db
            }
        }
    }
}