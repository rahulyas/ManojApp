package com.rahul.manojapp

class DataClass(
    productName: String,
    productRate: String,
    productDiscountrate: String,
    productDescription: String,
    imageUrl: String?
) {
    var productName: String? = null
    var productRate: String? = null
    var productDiscountrate: String? = null
    var productDescription: String? = null
    var dataImage: String? = null
    var key: String? = null

    override fun toString(): String {
        return "DataClass(productName=$productName, productRate=$productRate, productDiscountrate=$productDiscountrate, productDescription=$productDescription, dataImage=$dataImage, key=$key)"
    }

}