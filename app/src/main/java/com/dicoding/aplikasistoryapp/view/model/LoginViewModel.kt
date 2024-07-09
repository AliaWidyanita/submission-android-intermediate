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
import com.dicoding.aplikasistoryapp.data.remote.LoginResponse
import com.dicoding.aplikasistoryapp.data.remote.LoginResult
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    private val _dataLogin = MutableLiveData<LoginResult?>()
    val dataLogin : LiveData<LoginResult?> = _dataLogin

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess : LiveData<Boolean> = _loginSuccess

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading : LiveData<Boolean> = _showLoading

    fun saveSession(user: User) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun getSession(): LiveData<User> {
        return repository.getSession().asLiveData()
    }

    fun postDataLogin(email: String, password: String, token: String) {
        _showLoading.value = true
        viewModelScope.launch {
            val client = ApiConfig.getApiService(token).login(email, password)
            client.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    _showLoading.value = false
                    if (response.isSuccessful) {
                        val responseDetail = response.body()
                        if (responseDetail != null) {
                            _dataLogin.value = responseDetail.loginResult
                            _loginSuccess.value = true
                            Log.d(TAG, "onResponse: ${responseDetail.loginResult?.token}")
                        }
                    } else {
                        _loginSuccess.value = false
                        Log.e(TAG, "onFailure: ${response.message()}")
                    }
                }
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    _showLoading.value = false
                    Log.e(TAG, "onFailure: ${t.message}")
                }
            })
        }
    }

    companion object {
        const val TAG = "LoginViewModel"
    }
}