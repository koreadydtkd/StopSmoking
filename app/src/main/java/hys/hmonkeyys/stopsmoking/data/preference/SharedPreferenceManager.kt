package hys.hmonkeyys.stopsmoking.data.preference

import android.content.SharedPreferences
import androidx.core.content.edit

class SharedPreferenceManager(
    private val sharedPreferences: SharedPreferences
) : PreferenceManager {

    override fun getString(key: String): String? {
        val value = sharedPreferences.getString(key, "null")

        return if (value == INVALID_STRING_VALUE) {
            null
        } else {
            value
        }
    }
    override fun putString(key: String, value: String) = sharedPreferences.edit { putString(key, value) }

    override fun getInt(key: String): Int = sharedPreferences.getInt(key, -1)
    override fun putInt(key: String, value: Int) = sharedPreferences.edit { putInt(key, value) }

    override fun getLong(key: String): Long = sharedPreferences.getLong(key, -1L)
    override fun putLong(key: String, value: Long) = sharedPreferences.edit { putLong(key, value) }

    override fun getBoolean(key: String): Boolean = sharedPreferences.getBoolean(key, false)
    override fun putBoolean(key: String, value: Boolean) = sharedPreferences.edit { putBoolean(key, value) }

    companion object {
        private const val INVALID_STRING_VALUE = "null"
    }
}