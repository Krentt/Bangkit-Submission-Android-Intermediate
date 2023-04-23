package com.example.storyapp.view

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.api.GeneralResponse
import com.example.storyapp.api.ListStoriesResponse
import com.example.storyapp.api.ListStoryItem
import com.example.storyapp.helper.Event
import com.example.storyapp.preferences.AuthPreference
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(application: Application) : AndroidViewModel(application) {
    var isToastShown = false

    companion object {
        private const val TAG = "MainViewModel"
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _listStories = MutableLiveData<List<ListStoryItem?>?>()
    val listStories: LiveData<List<ListStoryItem?>?> = _listStories

    private val _getResp = MutableLiveData<Event<String?>>()
    val getResp: LiveData<Event<String?>> = _getResp

    fun getStories() {
        _isLoading.value = true

        // Get jwt token
        val authPreference = AuthPreference(getApplication())
        val token = authPreference.getKey()

        val apiService = ApiConfig().getApiService()
        val getStoriesRequest = apiService.getStories("Bearer $token")
        getStoriesRequest.enqueue(object : Callback<ListStoriesResponse> {
            override fun onResponse(
                call: Call<ListStoriesResponse>,
                response: Response<ListStoriesResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error!!) {
                        _listStories.value = responseBody.listStory
                        _getResp.value = Event(responseBody.message)
                    } else {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            try {
                                val errorResponse =
                                    Gson().fromJson(errorBody, GeneralResponse::class.java)
                                _getResp.value = Event(errorResponse.message)
                            } catch (e: Exception) {
                                _getResp.value =
                                    Event("Failed: ${response.code()} ${response.message()}")
                            }
                        } else {
                            _getResp.value =
                                Event("Failed: ${response.code()} ${response.message()}")
                        }
                        Log.e(TAG, "onFailure: ${response.message()}")
                    }
                }
            }

            override fun onFailure(call: Call<ListStoriesResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}