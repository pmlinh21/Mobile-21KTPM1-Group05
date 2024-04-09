package com.example.applepie

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context?) {
    val PRIVATE_MODE = 0
    private val PREF_NAME = "SharedPreferences"
    private val IS_LOGIN = "is_login"

    val pref: SharedPreferences? = context?.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
    val editor: SharedPreferences.Editor? = pref?.edit()

    fun setLogin(isLogin: Boolean) {
        editor?.putBoolean(IS_LOGIN, isLogin)
        editor?.commit()
    }

    fun setUsername(username: String?) {
        editor?.putString("username", username)
        editor?.commit()
    }

    fun isLogin() : Boolean? {
        return pref?.getBoolean(IS_LOGIN, false)
    }

    fun getUsername(): String? {
        return pref?.getString("username", "")
    }

    fun removeData() {
        editor?.clear()
        editor?.commit()
    }
}