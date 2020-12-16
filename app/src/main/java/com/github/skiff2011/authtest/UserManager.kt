package com.github.skiff2011.authtest

import com.github.skiff2011.authtest.extensions.sha256

class UserManager(private val localStorage: PasswordLocalStorage) {

    fun saveUser(login: String, password: String) {
        localStorage.saveUser(login, password.sha256())
    }

    fun checkUser(login: String, password: String): Boolean {
        val storedPassword = localStorage.getUserPassword(login)
        return password.sha256() == storedPassword
    }

}