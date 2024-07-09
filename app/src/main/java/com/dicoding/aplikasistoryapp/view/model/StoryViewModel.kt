package com.dicoding.aplikasistoryapp.view.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.aplikasistoryapp.data.local.User
import com.dicoding.aplikasistoryapp.data.local.UserRepository
import com.dicoding.aplikasistoryapp.data.remote.AddStoryResponse
import com.dicoding.aplikasistoryapp.data.remote.ApiConfig
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class StoryViewModel(private val repository: UserRepository): ViewModel() {
    private val _dataNewStory = MutableLiveData<AddStoryResponse?>()

    private val _uploadSuccess = MutableLiveData<Boolean>()
    val uploadSuccess : LiveData<Boolean> = _uploadSuccess

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading : LiveData<Boolean> = _showLoading

    fun getSession(): LiveData<User> {
        return repository.getSession().asLiveData()
    }

    fun postNewStory(imageFile: File, description: String, token: String) {
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )

        _showLoading.value = true
        viewModelScope.launch {
            val client = ApiConfig.getApiService(token).addNewStory(multipartBody, requestBody)
            client.enqueue(object : Callback<AddStoryResponse> {
                override fun onResponse(
                    call: Call<AddStoryResponse>,
                    response: Response<AddStoryResponse>
                ) {
                    _showLoading.value = false
                    if (response.isSuccessful) {
                        val responseDetail = response.body()
                        if (responseDetail != null) {
                            _dataNewStory.value = responseDetail
                            _uploadSuccess.value = true
                        }
                    } else {
                        _uploadSuccess.value = false
                        Log.e(LoginViewModel.TAG, "onFailure: ${response.message()}")
                    }
                }
                override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                    _showLoading.value = false
                    Log.e(LoginViewModel.TAG, "onFailure: ${t.message}")
                }
            })
        }
    }
}