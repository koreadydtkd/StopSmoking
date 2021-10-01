package hys.hmonkeyys.stopsmoking.data.preference

interface PreferenceManager {

    fun getString(key: String): String?
    fun putString(key: String, value: String)

    fun getInt(key: String): Int
    fun putInt(key: String, value: Int)

    fun getLong(key: String): Long
    fun putLong(key: String, value: Long)

    fun getBoolean(key: String): Boolean
    fun putBoolean(key: String, value: Boolean)
}