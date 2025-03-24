package com.example.marketbooking.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.marketbooking.model.User
import com.google.gson.Gson

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveUser(user: User) {
        val userJson = gson.toJson(user)
        prefs.edit().putString("user", userJson).apply()
    }

    fun getUser(): User? {
        val userJson = prefs.getString("user", null)
        return if (userJson != null) {
            gson.fromJson(userJson, User::class.java)
        } else null
    }

    fun clearUser() {
        prefs.edit().remove("user").apply()
    }

    fun isLoggedIn(): Boolean {
        return getUser() != null
    }
}