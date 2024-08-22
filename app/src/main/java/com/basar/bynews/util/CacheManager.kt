package com.basar.bynews.util

import android.content.Context
import android.content.SharedPreferences
import com.basar.bynews.model.NewsDetailResponse
import com.basar.bynews.model.NewsResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.util.concurrent.TimeUnit

class CacheManager(context: Context) {
    private val NAME = "BYNews"
    private val MODE = Context.MODE_PRIVATE
    private val preferences: SharedPreferences by lazy { context.getSharedPreferences(NAME, MODE) }
    private val gson: Gson by lazy { GsonBuilder().create() }
    private val cacheTimeout = TimeUnit.SECONDS.toMillis(120)

    var news: NewsResponse?
        get() = getCachedItem(CacheKey.NEWS)
        set(value) = setCachedItem(CacheKey.NEWS, value)

    var newsDetail: NewsDetailResponse?
        get() = getCachedItem(CacheKey.NEWS_DETAIL)
        set(value) = setCachedItem(CacheKey.NEWS_DETAIL, value)

    private enum class CacheKey(val key: String) {
        NEWS("news"),
        NEWS_DETAIL("news_detail")
    }

    private inline fun <reified T : Any> getCachedItem(cacheKey: CacheKey): T? {
        val data = preferences.getString(cacheKey.key, null) ?: return null
        val timestamp = preferences.getLong("${cacheKey.key}_timestamp", 0)

        return if (System.currentTimeMillis() - timestamp > cacheTimeout) {
            clearCacheForKey(cacheKey)
            null
        } else {
            try {
                gson.fromJson(data, T::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }

    private inline fun <reified T : Any> setCachedItem(cacheKey: CacheKey, value: T?) {
        preferences.edit().apply {
            if (value == null) {
                clearCacheForKey(cacheKey)
            } else {
                putString(cacheKey.key, gson.toJson(value))
                putLong("${cacheKey.key}_timestamp", System.currentTimeMillis())
            }
        }.apply()
    }

    private fun clearCacheForKey(cacheKey: CacheKey) {
        preferences.edit().apply {
            remove(cacheKey.key)
            remove("${cacheKey.key}_timestamp")
        }.apply()
    }

    fun clearAllCache() = preferences.edit().clear().apply()
}
