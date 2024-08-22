package com.basar.bynews.util

import android.content.Context
import android.content.SharedPreferences
import com.basar.bynews.model.NewsDetailResponse
import com.basar.bynews.model.NewsResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.util.concurrent.TimeUnit

class PreferencesManager(context: Context) {
    private val NAME = "BYNews"
    private val MODE = Context.MODE_PRIVATE
    private val preferences: SharedPreferences by lazy { context.getSharedPreferences(NAME, MODE) }
    private val gson: Gson by lazy { GsonBuilder().create() }
    private val cacheTimeout = TimeUnit.MINUTES.toMillis(2)

    var news: NewsResponse?
        get() = getCachedItem(PreferenceKey.NEWS)
        set(value) = setCachedItem(PreferenceKey.NEWS, value)

    var newsDetail: NewsDetailResponse?
        get() = getCachedItem(PreferenceKey.NEWS_DETAIL)
        set(value) = setCachedItem(PreferenceKey.NEWS_DETAIL, value)

    var isDescending: Boolean
        get() = preferences.getBoolean(PreferenceKey.IS_DESCENDING.key, false)
        set(value) = preferences.edit().putBoolean(PreferenceKey.IS_DESCENDING.key, value).apply()

    private enum class PreferenceKey(val key: String) {
        NEWS("news"),
        NEWS_DETAIL("news_detail"),
        IS_DESCENDING("is_descending")
    }

    private inline fun <reified T : Any> getCachedItem(preferenceKey: PreferenceKey): T? {
        val data = preferences.getString(preferenceKey.key, null) ?: return null
        val timestamp = preferences.getLong("${preferenceKey.key}_timestamp", 0)

        return if (System.currentTimeMillis() - timestamp > cacheTimeout) {
            clearCacheForKey(preferenceKey)
            null
        } else {
            try {
                gson.fromJson(data, T::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }

    private inline fun <reified T : Any> setCachedItem(preferenceKey: PreferenceKey, value: T?) {
        preferences.edit().apply {
            if (value == null) {
                clearCacheForKey(preferenceKey)
            } else {
                putString(preferenceKey.key, gson.toJson(value))
                putLong("${preferenceKey.key}_timestamp", System.currentTimeMillis())
            }
        }.apply()
    }

    private fun clearCacheForKey(preferenceKey: PreferenceKey) {
        preferences.edit().apply {
            remove(preferenceKey.key)
            remove("${preferenceKey.key}_timestamp")
        }.apply()
    }

    fun clearAllCache() = preferences.edit().clear().apply()
}
