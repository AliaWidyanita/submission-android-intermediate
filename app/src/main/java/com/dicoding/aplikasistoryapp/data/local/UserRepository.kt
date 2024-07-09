package com.dicoding.aplikasistoryapp.data.local

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.aplikasistoryapp.data.remote.ApiConfig
import com.dicoding.aplikasistoryapp.data.remote.ListStoryItem
import com.dicoding.aplikasistoryapp.utils.StoryPagingSource
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(private val userDataStore: UserDataStore, private val apiConfig: ApiConfig) {
    suspend fun saveSession(user: User) {
        userDataStore.saveSession(user)
    }

    fun getSession(): Flow<User> {
        return userDataStore.getSession()
    }

    suspend fun logout() {
        userDataStore.logout()
    }

    fun getStory(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiConfig ,userDataStore)
            }
        ).liveData
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(userDataStore: UserDataStore, apiConfig: ApiConfig): UserRepository = instance ?: synchronized(this) {
            instance ?: UserRepository(userDataStore, apiConfig)
            }.also { instance = it }
    }
}