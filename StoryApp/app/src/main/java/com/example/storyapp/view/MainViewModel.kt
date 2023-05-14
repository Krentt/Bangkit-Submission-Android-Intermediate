package com.example.storyapp.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.database.Story
import com.example.storyapp.helper.Event
import com.example.storyapp.preferences.AuthPreference

class MainViewModel(private val storyRepository: StoryRepository, application: Application) : AndroidViewModel(application) {
    var isToastShown = false

    private val authPreference = AuthPreference(getApplication())
    val token = "Bearer " + authPreference.getKey()

    val listStories: LiveData<PagingData<Story>> by lazy {
        storyRepository.getStory(token).cachedIn(viewModelScope)
    }

    private val _getResp = MutableLiveData<Event<String?>>()
    val getResp: LiveData<Event<String?>> = _getResp

}