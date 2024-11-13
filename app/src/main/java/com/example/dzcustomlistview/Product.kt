package com.example.dzcustomlistview

import android.graphics.Bitmap

class Product(val name: String, val price: String, val image: Bitmap?) {

    override fun toString(): String {
        return "Продукт $name, цена $price"
    }
}