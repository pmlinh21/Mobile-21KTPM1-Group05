package com.example.applepie.database

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context?) {
    val PRIVATE_MODE = 0
    private val PREF_INDEX = 0
    private val PREF_NAME = "SharedPreferences"
    private val IS_LOGIN = "is_login"
    private val MUSIC_STATUS = "music_status"

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

    fun setMusicStatus(music: Boolean) {
        editor?.putBoolean(MUSIC_STATUS, music)
        editor?.commit()
    }
    
    fun setIndex(index: Int) {
        editor?.putInt("index", index)
        editor?.commit()
    }

    fun isLogin() : Boolean? {
        return pref?.getBoolean(IS_LOGIN, false)
    }

    fun getUsername(): String? {
        return pref?.getString("username", "")
    }

    fun getMusicStatus(): Boolean? {
        return pref?.getBoolean(MUSIC_STATUS, false)
    }
    
    fun getIndex(): Int? {
        return pref?.getInt("index", -1)
    }

    fun removeData() {
        editor?.clear()
        editor?.commit()
    }
}