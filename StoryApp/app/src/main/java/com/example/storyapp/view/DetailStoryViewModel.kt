package com.example.storyapp.view

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.api.DetailResponse
import com.example.storyapp.api.GeneralResponse
import com.example.storyapp.api.Story
import com.example.storyapp.helper.Event
import com.example.storyapp.preferences.AuthPreference
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailStoryViewModel(application: Application): AndroidViewModel(application) {

    companion object {
        private const val TAG = "DetailStoryViewModel"
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _storyDetail = MutableLiveData<Story?>()
    val storyDetail: LiveData<Story?> = _storyDetail

    private val _detailResp = MutableLiveData<Event<String>>()
    val detailResp: LiveData<Event<String>> = _detailResp

    fun getDetail(idStory: String){
        _isLoading.value = true

        // Get jwt token
        val authPreference = AuthPreference(getApplication())
        val token = authPreference.getKey()

        val apiService = ApiConfig().getApiService()
        val detailRequest = apiService.getDetailStories("Bearer $token", idStory)
        detailRequest.enqueue(object : Callback<DetailResponse>{
            override fun onResponse(
                call: Call<DetailResponse>,
                response: Response<DetailResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful){
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error!!){
                        _storyDetail.value = responseBody.story
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    if (errorBody != null){
                        try{
                            val errorResponse = Gson().fromJson(errorBody, GeneralResponse::class.java)
                            _detailResp.value = Event(errorResponse.message)
                        } catch (e: Exception) {
                            _detailResp.value = Event("Failed: ${response.code()} ${response.message()}")
                        }
                    } else {
                        _detailResp.value = Event("Failed: ${response.code()} ${response.message()}")
                    }
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

}