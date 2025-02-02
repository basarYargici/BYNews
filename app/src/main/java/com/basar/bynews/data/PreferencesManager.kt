package com.basar.bynews.data

import android.content.Context
import android.content.SharedPreferences
import com.basar.bynews.extension.isNull
import com.basar.bynews.model.NewsDetailResponse
import com.basar.bynews.model.NewsResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.util.concurrent.TimeUnit

enum class PreferenceKey(val key: String) {
    NEWS("news"),
    NEWS_DETAIL("news_detail"),
    IS_DESCENDING("is_descending")
}

class PreferencesManager(context: Context) {
    private val name = "BYNews"
    private val mode = Context.MODE_PRIVATE
    private val preferences: SharedPreferences by lazy { context.getSharedPreferences(name, mode) }
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
            if (value.isNull()) {
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

    fun getLastUpdated(preferenceKey: PreferenceKey): Long {
        return preferences.getLong("${preferenceKey.key}_timestamp", 0)
    }

    fun getDetailedKBSize(): Int {
        val sizeMap = preferences.all.mapValues { (_, value) ->
            when (value) {
                is String -> value.length * 2 // UTF-16 encoding
                is Int -> 4
                is Long -> 8
                is Float -> 4
                is Set<*> -> (value as Set<String>).sumOf { it.length * 2 }
                else -> 0
            }
        }

        return sizeMap.values.sum()
    }

    fun clearAllCache() {
        clearCacheForKey(PreferenceKey.NEWS)
        clearCacheForKey(PreferenceKey.NEWS_DETAIL)
    }
}
