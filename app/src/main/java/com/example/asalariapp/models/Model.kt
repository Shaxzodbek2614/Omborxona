package com.example.asalariapp.models

class Model {
    var name: String? = null
    var price: Int? = null
    var image: String? = null

    constructor()
    constructor(name: String?, price: Int?, image: String?) {
        this.name = name
        this.price = price
        this.image = image
    }
}