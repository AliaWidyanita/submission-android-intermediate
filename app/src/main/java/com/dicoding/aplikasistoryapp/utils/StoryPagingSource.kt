package com.dicoding.aplikasistoryapp.utils

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.aplikasistoryapp.data.local.UserDataStore
import com.dicoding.aplikasistoryapp.data.remote.ApiConfig
import com.dicoding.aplikasistoryapp.data.remote.ListStoryItem
import kotlinx.coroutines.flow.first

class StoryPagingSource(private val apiConfig: ApiConfig, private val dataStore: UserDataStore) : PagingSource<Int, ListStoryItem>() {
    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val token = dataStore.getSession().first().token
            val responseData = apiConfig.getApiService(token).getStories(position, params.loadSize).listStory

            LoadResult.Page(
                data = responseData,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}