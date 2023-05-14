package com.example.storyapp.helper

import com.example.storyapp.api.ListStoryItem

object ListStoryMapper {
    fun fromApiResponse(apiResponse: ListStoryItem): com.example.storyapp.database.Story {
        return com.example.storyapp.database.Story(
            id = apiResponse.id!!,
            photoUrl = apiResponse.photoUrl,
            createdAt = apiResponse.createdAt,
            name = apiResponse.name,
            description = apiResponse.description,
            lon = apiResponse.lon,
            lat = apiResponse.lat
        )
    }
}