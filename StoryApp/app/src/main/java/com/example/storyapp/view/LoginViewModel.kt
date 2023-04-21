package com.example.storyapp.view

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.api.LoginRequest
import com.example.storyapp.api.LoginResponse
import com.example.storyapp.api.GeneralResponse
import com.example.storyapp.helper.Event
import com.example.storyapp.preferences.AuthPreference
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val context: Context): ViewModel() {
    companion object {
        private const val TAG = "LoginViewModel"
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    private val _loginResp = MutableLiveData<Event<String>>()
    val loginResp: LiveData<Event<String>> = _loginResp

    fun login(loginRequest: LoginRequest){
        _isLoading.value = true
        val apiService = ApiConfig().getApiService()
        val loginAccount = apiService.login(loginRequest)
        loginAccount.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if (response.isSuccessful){
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error){
                        _isSuccess.value = true
                        _loginResp.value = Event(responseBody.message)
                        val authPreference = AuthPreference(context)
                        authPreference.setKey(responseBody.loginResult.token)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _isSuccess.value = false
                    if (errorBody != null){
                        try{
                            val errorResponse = Gson().fromJson(errorBody, GeneralResponse::class.java)
                            _loginResp.value = Event(errorResponse.message)
                        } catch (e: Exception) {
                            _loginResp.value = Event("Failed: ${response.code()} ${response.message()}")
                        }
                    } else {
                        _loginResp.value = Event("Failed: ${response.code()} ${response.message()}")
                    }
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _isSuccess.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun logout(){
        val authPreference = AuthPreference(context)
        authPreference.clear()
    }
}