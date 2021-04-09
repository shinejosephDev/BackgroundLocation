package com.sample.backgroundlocation.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sample.backgroundlocation.db.AppDatabase
import com.sample.backgroundlocation.db.User
import com.sample.backgroundlocation.db.UserDao
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao: UserDao = AppDatabase.getDatabase(application).userDao()

    fun insert(loc: User) {
        viewModelScope.launch {
            userDao.insert(loc)
        }
    }

    fun getAll(): Int {
        return userDao.getAll()
    }
}