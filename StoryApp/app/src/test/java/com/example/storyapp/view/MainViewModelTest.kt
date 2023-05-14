package com.example.storyapp.view

import android.app.Application
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.storyapp.DataDummy
import com.example.storyapp.MainDispatcherRule
import com.example.storyapp.adapter.ListStoriesAdapter
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.database.Story
import com.example.storyapp.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()
    @Mock
    private lateinit var storyRepository: StoryRepository
    @Mock
    private lateinit var application: Application
    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    private val token: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXlzZWt3cFgzSXlQeUk4MmciLCJpYXQiOjE2ODIwNjI5MjB9.kUBqlhFtUqc5lkAtuna10W5JHYsgxVl1MYIlkxmi5AM"

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val dummyStories = DataDummy.generateDummyStoryResponse()
        val data = PagingData.from(dummyStories)
        val expectedStory = MutableLiveData<PagingData<Story>>()
        expectedStory.value = data

        Mockito.`when`(storyRepository.getStory("Bearer $token")).thenReturn(expectedStory)
        Mockito.`when`(application.getSharedPreferences(Mockito.anyString(), Mockito.anyInt())).thenReturn(sharedPreferences)
        Mockito.`when`(sharedPreferences.getString(Mockito.anyString(), Mockito.anyString())).thenReturn(token)

        val mainViewModel = MainViewModel(storyRepository, application)
        val actualStory: PagingData<Story> = mainViewModel.listStories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoriesAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStories.size, differ.snapshot().size)
        assertEquals(dummyStories[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val data: PagingData<Story> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<Story>>()
        expectedStory.value = data
        Mockito.`when`(storyRepository.getStory("Bearer $token")).thenReturn(expectedStory)
        Mockito.`when`(application.getSharedPreferences(Mockito.anyString(), Mockito.anyInt())).thenReturn(sharedPreferences)
        Mockito.`when`(sharedPreferences.getString(Mockito.anyString(), Mockito.anyString())).thenReturn(token)
        val mainViewModel = MainViewModel(storyRepository, application)
        val actualStory: PagingData<Story> = mainViewModel.listStories.getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoriesAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)
        assertEquals(0, differ.snapshot().size)
    }

    val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}