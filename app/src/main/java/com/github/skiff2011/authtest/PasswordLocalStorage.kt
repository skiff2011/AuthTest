package com.github.skiff2011.authtest

interface PasswordLocalStorage {

    fun saveUser(login: String, password: String)

    fun getUserPassword(login: String): String?
}