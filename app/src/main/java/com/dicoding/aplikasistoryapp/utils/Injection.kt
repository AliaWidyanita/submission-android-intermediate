package com.dicoding.aplikasistoryapp.utils

import android.content.Context
import com.dicoding.aplikasistoryapp.data.local.UserDataStore
import com.dicoding.aplikasistoryapp.data.local.UserRepository
import com.dicoding.aplikasistoryapp.data.local.dataStore
import com.dicoding.aplikasistoryapp.data.remote.ApiConfig

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val datastore = UserDataStore.getInstance(context.dataStore)
        val apiConfig = ApiConfig
        return UserRepository.getInstance(datastore, apiConfig)
    }
}