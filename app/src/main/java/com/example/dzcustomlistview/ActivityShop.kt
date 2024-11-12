package com.example.dzcustomlistview

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ListAdapter
import java.io.IOException

class ActivityShop : AppCompatActivity() {
    private lateinit var photoPickerLauncher: ActivityResultLauncher<Intent>
    var bitmap: Bitmap? = null
    var products: MutableList<Product> = mutableListOf()
    private val GALLERY_REQUEST = 302
    private lateinit var toolbarShop: Toolbar
    private lateinit var editImageIV: ImageView
    private lateinit var productNameET: EditText
    private lateinit var productPriceET: EditText
    private lateinit var addBTN: Button
    private lateinit var listViewLV: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_shop)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        toolbarShop = findViewById(R.id.toolbarShop)
        setSupportActionBar(toolbarShop)
        title = " Магазин продуктов"
        toolbarShop.subtitle = "  Версия 1. Страница магазина"
        toolbarShop.setLogo(R.drawable.shop)

        editImageIV = findViewById(R.id.editImageIV)
        productNameET = findViewById(R.id.productNameET)
        productPriceET = findViewById(R.id.productPriceET)
        addBTN = findViewById(R.id.addBTN)
        listViewLV = findViewById(R.id.listViewLV)

        photoPickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImage = result.data?.data  // selectedImage для загрузки изображения
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                editImageIV.setImageBitmap(bitmap)
            }
        }

        editImageIV.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            photoPickerLauncher.launch(photoPickerIntent)
        }

        addBTN.setOnClickListener {
            if (productNameET.text.isEmpty() || productPriceET.text.isEmpty()) return@setOnClickListener
            val productName = productNameET.text.toString()
            val productPrice = productPriceET.text.toString()
            val productImage = bitmap
            val product = Product(productName, productPrice, productImage)
            products.add(product)
            val listAdapter = ListAdapter(this@ActivityShop, products)
            listViewLV.adapter = listAdapter
            listAdapter.notifyDataSetChanged()
            productNameET.text.clear()
            productPriceET.text.clear()
            editImageIV.setImageResource(R.drawable.baseline_production_quantity_limits_24)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.infoMenuMain -> {
                Toast.makeText(applicationContext, "Автор Ефремов О.В. Создан 13.11.2024",
                    Toast.LENGTH_LONG).show()
            }
            R.id.exitMenuMain ->{
                Toast.makeText(applicationContext, "Работа приложения завершена",
                    Toast.LENGTH_LONG).show()
                finishAffinity()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}