package com.example.storyapp.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface ApiService {
    @POST("register")
    fun register(
        @Body registerRequest: RegisterRequest
    ): Call<GeneralResponse>

    @POST("login")
    fun login(
        @Body loginRequest: LoginRequest
    ): Call<LoginResponse>

    @Multipart
    @POST("stories")
    fun uploadStory(
        @Header("Authorization") authorization: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<GeneralResponse>

    @GET("stories")
    fun getStories(
        @Header("Authorization") authorization: String
    ): Call<ListStoriesResponse>

    @GET("stories/{id}")
    fun getDetailStories(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): Call<DetailResponse>

    @GET("stories")
    fun getStoriesWithLocation(
        @Header("Authorization") authorization: String,
        @Query("location") location: Double
    ): Call<ListStoriesResponse>

    @GET("stories")
    suspend fun getStoriesWithPaging(
        @Header("Authorization") authorization: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): ListStoriesResponse
}

