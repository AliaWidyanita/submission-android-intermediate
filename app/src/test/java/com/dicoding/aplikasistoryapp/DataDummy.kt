package com.dicoding.aplikasistoryapp

import com.dicoding.aplikasistoryapp.data.remote.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "createdAt + $i",
                "name $i",
                "desc $i",
                i.toDouble(),
                "id + $i",
                i.toDouble(),
                )
            items.add(story)
        }
        return items
    }
}