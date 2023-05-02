package com.example.storyapp.view

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.R
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.api.RegisterRequest
import com.example.storyapp.api.GeneralResponse
import com.example.storyapp.helper.Event
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {
    companion object {
        private const val TAG = "RegisterViewModel"
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _registResp = MutableLiveData<Event<String>>()
    val registResp: LiveData<Event<String>> = _registResp

    fun register(registerRequest: RegisterRequest) {
        _isLoading.value = true
        val apiService = ApiConfig().getApiService()
        val registerAccount = apiService.register(registerRequest)
        registerAccount.enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(
                call: Call<GeneralResponse>,
                response: Response<GeneralResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _registResp.value = Event(responseBody.message + ": " +R.string.please_login)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    if (errorBody != null) {
                        try {
                            val errorResponse =
                                Gson().fromJson(errorBody, GeneralResponse::class.java)
                            _registResp.value = Event("Failed: " + errorResponse.message)
                        } catch (e: Exception) {
                            _registResp.value =
                                Event("Failed: ${response.code()} ${response.message()}")
                        }
                    } else {
                        _registResp.value =
                            Event("Failed: ${response.code()} ${response.message()}")
                    }
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}