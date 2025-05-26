package com.example.asalariapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.asalariapp.models.Model
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object MySharedPreferences {
    private const val NAME = "catch_file_name1"
    private const val MODE = Context.MODE_PRIVATE

    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var kirim: Int
        get() = preferences.getInt("totalIncome", 0)  // Agar topilmasa, 0 qaytaradi
        set(value) = preferences.edit {
            it.putInt("totalIncome", value)
        }
    var chiqim: Int
        get() = preferences.getInt("totalOutcome", 0)  // Agar topilmasa, 0 qaytaradi
        set(value) = preferences.edit {
            it.putInt("totalOutcome", value)
        }
    var sharedList1: ArrayList<Model>
        get() = gsonStringToList(preferences.getString("sharedList1", "[]")!!)
        set(value) = preferences.edit {
            it.putString("sharedList1", listToGsonString(value))
        }

    private fun gsonStringToList(gsonString: String): ArrayList<Model> {
        val list = ArrayList<Model>()
        val type = object : TypeToken<ArrayList<Model>>() {}.type
        list.addAll(Gson().fromJson(gsonString, type))
        return list
    }

    private fun listToGsonString(list: ArrayList<Model>): String {
        return Gson().toJson(list)
    }
}
