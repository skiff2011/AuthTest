package com.github.skiff2011.authtest

import android.content.Context
import androidx.core.content.edit

const val PREFS_NAME = "_users"

class SharedPrefsPasswordStorageImpl(context: Context) : PasswordLocalStorage {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun saveUser(login: String, password: String) {
        prefs.edit {
            putString(login, password)
        }
    }

    override fun getUserPassword(login: String): String? = prefs.getString(login, null)
}