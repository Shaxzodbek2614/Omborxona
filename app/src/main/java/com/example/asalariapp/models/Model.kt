package com.example.asalariapp.models

class Model {
    var id: String? = null
    var name: String? = null
    var price: Int? = null
    var soni: Int? = null
    var image: String? = null

    constructor()
    constructor(id:String?,name: String?, price: Int?, soni: Int?, image: String?) {
        this.id = id
        this.name = name
        this.price = price
        this.soni = soni
        this.image = image
    }

    override fun toString(): String {
        return "Model(id=$id, name=$name, price=$price, soni=$soni, image=$image)"
    }

}