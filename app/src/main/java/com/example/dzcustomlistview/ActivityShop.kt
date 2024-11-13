package com.example.dzcustomlistview

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ListAdapter
import java.io.IOException

class ActivityShop : AppCompatActivity() {

    private lateinit var photoPickerLauncher: ActivityResultLauncher<Intent>

    private val GALLERY_REQUEST = 302
    private lateinit var toolbarShop: Toolbar
    private lateinit var editImageIV: ImageView
    private lateinit var productNameET: EditText
    private lateinit var productPriceET: EditText
    private lateinit var addBTN: Button

    private lateinit var listViewLV: ListView
    var bitmap: Bitmap? = null
    private lateinit var productViewModel: ProductViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_shop)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        extracted()

        productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]
        val adapter = ListAdapter(this@ActivityShop, productViewModel.products)
        listViewLV.adapter = adapter

        productViewModel.listProduct.observe(this, Observer { it ->
            val adapter = ListAdapter(this@ActivityShop, productViewModel.products)
            adapter.notifyDataSetChanged()
        })

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
            val name = productNameET.text.toString()
            val price = productPriceET.text.toString()
            val image = bitmap
            val product = Product(name, price, image)
            productViewModel.addProduct(product)
            val listAdapter = ListAdapter(this@ActivityShop, productViewModel.products)
            listViewLV.adapter = listAdapter
            listAdapter.notifyDataSetChanged()
            productNameET.text.clear()
            productPriceET.text.clear()
            editImageIV.setImageResource(R.drawable.baseline_production_quantity_limits_24)
        }

        listViewLV.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val listAdapter = ListAdapter(this@ActivityShop, productViewModel.products)
                listViewLV.adapter = listAdapter
                val product = listViewLV.adapter.getItem(position)
                productViewModel.products.remove(product)
                Toast.makeText(this, "Продукт ${product} удалён", Toast.LENGTH_LONG).show()
            }
    }

    private fun extracted() {
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