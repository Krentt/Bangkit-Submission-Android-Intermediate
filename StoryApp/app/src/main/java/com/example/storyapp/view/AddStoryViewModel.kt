package com.example.storyapp.view

import android.app.Activity.RESULT_OK
import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.*
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.api.GeneralResponse
import com.example.storyapp.helper.*
import com.example.storyapp.preferences.AuthPreference
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddStoryViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        val TAG = "AddStoryViewModel"
    }

    private val _previewImageBitmap = MutableLiveData<Bitmap>()
    val previewImageBitmap: LiveData<Bitmap> = _previewImageBitmap

    private val _getFile = MutableLiveData<File>()
    val getFile: LiveData<File> = _getFile

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    private val _uploadResp = MutableLiveData<Event<String>>()
    val uploadResp: LiveData<Event<String>> = _uploadResp

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private lateinit var currentPhotoPath: String

    fun uploadStory(description: String, getFile: File) {

        _isLoading.value = true

        // Get jwt token
        val authPreference = AuthPreference(getApplication())
        val token = authPreference.getKey()

        //reduce file
        val file = reduceFileImage(getFile)

        val requestDescription = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )
        val apiService = ApiConfig().getApiService()
        val uploadImageRequest =
            apiService.uploadStory("Bearer $token", imageMultipart, requestDescription)
        uploadImageRequest.enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(
                call: Call<GeneralResponse>,
                response: Response<GeneralResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _isSuccess.value = true
                        _uploadResp.value = Event(responseBody.message)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _isSuccess.value = false
                    if (errorBody != null) {
                        try {
                            val errorResponse =
                                Gson().fromJson(errorBody, GeneralResponse::class.java)
                            _uploadResp.value = Event(errorResponse.message)
                        } catch (e: Exception) {
                            _uploadResp.value =
                                Event("Failed: ${response.code()} ${response.message()}")
                        }
                    } else {
                        _uploadResp.value =
                            Event("Failed: ${response.code()} ${response.message()}")
                    }
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                _isLoading.value = false
                _isSuccess.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun onCameraClicked() {
        createCustomTempFile(getApplication()).also {
            val photoURI: Uri =
                FileProvider.getUriForFile(getApplication(), "com.example.storyapp", it)
            currentPhotoPath = it.absolutePath
            _takePhotoIntent.value = photoURI
        }
    }

    fun onGalleryButtonClicked() {
        _galleryIntent.value = true
    }

    private val _takePhotoIntent = MutableLiveData<Uri>()
    val takePhotoIntent: LiveData<Uri> = _takePhotoIntent

    private val _galleryIntent = MutableLiveData<Boolean>()
    val galleryIntent: LiveData<Boolean> = _galleryIntent

    fun onTakePhotoResult(resultCode: Int) {
        if (resultCode == RESULT_OK) {
            viewModelScope.launch(Dispatchers.IO) {
                val myFile = File(currentPhotoPath)
                _getFile.postValue(myFile)
                myFile.let { file ->
                    val bitmap = BitmapFactory.decodeFile(file.path)
                    _previewImageBitmap.postValue(bitmap)
                }
            }
        }
    }

    fun onGalleryResult(uri: Uri) {
        uri.let { uri ->
            val myFile = uriToFile(uri, getApplication())
            _getFile.postValue(myFile)
            _previewImageBitmap.postValue(BitmapFactory.decodeFile(myFile.path))
        }
    }
}