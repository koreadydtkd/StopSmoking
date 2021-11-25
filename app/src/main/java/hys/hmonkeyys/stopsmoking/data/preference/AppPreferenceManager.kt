package hys.hmonkeyys.stopsmoking.data.preference

import android.content.Context
import android.content.SharedPreferences

class AppPreferenceManager(
    private val context: Context
) {

    companion object {
        const val APP_DEFAULT_KEY = "stop_smoking"

        // Default
        private const val DEFAULT_VALUE_STRING = ""
        private const val DEFAULT_VALUE_BOOLEAN = false
        private const val DEFAULT_VALUE_INT = -1
        private const val DEFAULT_VALUE_LONG = -1L
        private const val DEFAULT_VALUE_FLOAT = -1f

        private const val IS_FIRST_VALUE = true

        // SharedPreference key
        const val USER_UID = "user_uid"
        const val IS_REGISTRATION = "is_registration"
        const val STOP_SMOKING_DATE = "stop_smoking_date"
        const val NICK_NAME = "nick_name"
        const val AMOUNT_OF_SMOKING_PER_DAY = "amount_of_smoking_per_day"
        const val TOBACCO_PRICE = "tobacco_price"
        const val MY_RESOLUTION = "my_resolution"

        const val KEY_ID_TOKEN = "ID_TOKEN"
    }

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(APP_DEFAULT_KEY, Context.MODE_PRIVATE)
    }

    private val prefs by lazy { getPreferences(context) }
    private val editor by lazy { prefs.edit() }

    /** @set String */
    fun setString(key: String?, value: String?) {
        editor.putString(key, value)
        editor.apply()
    }

    /** @set boolean */
    fun setBoolean(key: String?, value: Boolean) {
        editor.putBoolean(key, value)
        editor.apply()
    }

    /** @set int */
    fun setInt(key: String?, value: Int) {
        editor.putInt(key, value)
        editor.apply()
    }

    /** @set long */
    fun setLong(key: String?, value: Long) {
        editor.putLong(key, value)
        editor.apply()
    }

    /** @set float */
    fun setFloat(key: String?, value: Float) {
        editor.putFloat(key, value)
        editor.apply()
    }

    /** @get String */
    fun getString(key: String?): String? = prefs.getString(key, null)

    /** @get boolean */
    fun getBoolean(key: String?): Boolean = prefs.getBoolean(key, DEFAULT_VALUE_BOOLEAN)

    /** @get int */
    fun getInt(key: String?): Int = prefs.getInt(key, DEFAULT_VALUE_INT)

    /** @get long */
    fun getLong(key: String?): Long = prefs.getLong(key, DEFAULT_VALUE_LONG)

    /** @get float */
    fun getFloat(key: String?): Float = prefs.getFloat(key, DEFAULT_VALUE_FLOAT)


    /** Custom */
    fun setIsFirst(key: String?, value: Boolean) {
        editor.putBoolean(key, value)
        editor.apply()
    }
    fun getIsFirst(key: String?): Boolean = prefs.getBoolean(key, IS_FIRST_VALUE)

    /** 모든 저장 데이터 삭제 */
    fun clear() {
        editor.clear()
        editor.apply()
    }

}