package com.dicoding.aplikasistoryapp.view.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.aplikasistoryapp.data.local.User
import com.dicoding.aplikasistoryapp.data.local.UserRepository
import com.dicoding.aplikasistoryapp.data.remote.ApiConfig
import com.dicoding.aplikasistoryapp.data.remote.ListStoryItem
import com.dicoding.aplikasistoryapp.data.remote.StoryResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapViewModel(private val repository: UserRepository) : ViewModel() {
    private val _dataLocationStories = MutableLiveData<ArrayList<ListStoryItem>>()
    val dataStoryLocation : LiveData<ArrayList<ListStoryItem>> = _dataLocationStories

    fun getSession(): LiveData<User> {
        return repository.getSession().asLiveData()
    }

    fun getStoryLocation(token: String) {
        viewModelScope.launch {
            val client = ApiConfig.getApiService(token).getStoriesWithLocation()
            client.enqueue(object : Callback<StoryResponse> {
                override fun onResponse(
                    call: Call<StoryResponse>,
                    response: Response<StoryResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseDetail = response.body()
                        if (responseDetail != null) {
                            _dataLocationStories.value = ArrayList(responseDetail.listStory)
                        }
                    } else {
                        Log.e(LoginViewModel.TAG, "onFailure: ${response.message()}")
                    }
                }
                override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                    Log.e(LoginViewModel.TAG, "onFailure: ${t.message}")
                }
            })
        }
    }
}