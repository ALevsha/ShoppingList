package com.proCourse.shoppinglist.presentation

import android.content.Intent
import android.content.LocusId
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.proCourse.shoppinglist.R
import java.lang.RuntimeException

class EditShoppingItem : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editTextCount: EditText
    private var oldId = ""
    private var oldEndabled = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_shopping_item)
        editTextName = findViewById(R.id.editTextName)
        editTextCount = findViewById(R.id.editTextCount)
        val oldName = intent.getStringExtra("name")
        val oldCount = intent.getStringExtra("count")
        oldId = intent.getStringExtra("id").toString()
        oldEndabled = intent.getStringExtra("enabled").toString()

        editTextName.setText(oldName)
        editTextCount.setText(oldCount)
    }

    override fun onBackPressed() {
        val backIntent = Intent()
        val newName = editTextName.text.toString()
        val newCount = editTextCount.text.toString()
        backIntent.putExtra("name", newName)
        backIntent.putExtra("count", newCount)
        backIntent.putExtra("id", oldId)
        backIntent.putExtra("enabled", oldEndabled)
        setResult(RESULT_OK, backIntent)
        finish()
    }
}