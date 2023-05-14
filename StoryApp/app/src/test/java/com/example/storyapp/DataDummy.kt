package com.example.storyapp

import com.example.storyapp.database.Story
import kotlin.text.Typography.quote

object DataDummy {
    fun generateDummyStoryResponse(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val story = Story(
                i.toString(),
                "photoUrl $i",
                "createdAt $i",
                "name + $i",
                "description $i",
            )
            items.add(story)
        }
        return items
    }
}