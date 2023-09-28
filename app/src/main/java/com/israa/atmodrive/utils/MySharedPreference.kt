import android.content.Context
import android.content.SharedPreferences
import com.israa.atmodrive.auth.domain.model.CheckCode


object MySharedPreferences {

    private var mAppContext: Context? = null
    fun init(appContext: Context?) {
        mAppContext = appContext
    }

    private fun getSharedPreferences(): SharedPreferences {
        return mAppContext!!.getSharedPreferences(
            PreferencesKeys.SHARED_PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
    }


    fun setUser(user: CheckCode) {

        val editor = getSharedPreferences().edit()
        editor.apply {
            putString(PreferencesKeys.FULL_NAME, user.fullName)
            putString(PreferencesKeys.AVATAR, user.avatar)
            putString(PreferencesKeys.IS_DARK_MODE, user.isDarkMode .toString())
            putString(PreferencesKeys.EMAIL, user.email)
            putString(PreferencesKeys.LANG, user.lang)
            putString(PreferencesKeys.MOBILE, user.mobile)
            putString(PreferencesKeys.PASSENGER_CODE, user.passengerCode)
            putString(PreferencesKeys.RATE, user.rate.toString())
            putString(PreferencesKeys.SHAKE_PHONE, user.shakePhone.toString())
            putString(PreferencesKeys.STATUS, user.status.toString())
            putString(PreferencesKeys.SUSPEND, user.suspend.toString())
            putBoolean(PreferencesKeys.IS_NEW_USER, user.isNew)
            putBoolean(PreferencesKeys.IS_FIRST_RUN, false)
            putString(PreferencesKeys.REMEMBER_TOKEN, user.rememberToken)
            apply()
        }

    }

    fun getUser(): CheckCode {
        val avatar = getSharedPreferences().getString(PreferencesKeys.AVATAR, "")
        val email = getSharedPreferences().getString(PreferencesKeys.EMAIL, "")
        val full_name = getSharedPreferences().getString(PreferencesKeys.FULL_NAME, "")
        val is_dark_mode = getSharedPreferences().getString(PreferencesKeys.IS_DARK_MODE, null)
        val lang = getSharedPreferences().getString(PreferencesKeys.LANG, "")
        val mobile = getSharedPreferences().getString(PreferencesKeys.MOBILE, "")
        val passenger_code = getSharedPreferences().getString(PreferencesKeys.PASSENGER_CODE, "")
        val rate = getSharedPreferences().getString(PreferencesKeys.RATE, null)
        val remember_token = getSharedPreferences().getString(PreferencesKeys.REMEMBER_TOKEN, "")
        val shake_phone = getSharedPreferences().getString(PreferencesKeys.SHAKE_PHONE, null)
        val status = getSharedPreferences().getString(PreferencesKeys.STATUS, null)
        val suspendd = getSharedPreferences().getString(PreferencesKeys.SUSPEND, null)
        val isNew = getSharedPreferences().getBoolean(PreferencesKeys.IS_NEW_USER, false)
        return CheckCode(
            avatar,
            email,
            full_name,
            is_dark_mode?.toInt(),
            lang,
            mobile,
            passenger_code,
            rate?.toInt(),
            remember_token,
            shake_phone?.toInt(),
            status?.toInt(),
            suspendd?.toInt(),
            isNew
        )
    }

    fun deleteCurrentUser() {
        getSharedPreferences().edit().clear().apply()
    }

    fun getUserToken(): String? {
        return getSharedPreferences().getString(PreferencesKeys.REMEMBER_TOKEN, "")
    }

    fun getIsFirstRun(): Boolean {
        return getSharedPreferences().getBoolean(PreferencesKeys.IS_FIRST_RUN, true)
    }

    fun setIsFirstRun(isFirstRun: Boolean) {
        getSharedPreferences().edit().putBoolean(PreferencesKeys.IS_FIRST_RUN, isFirstRun).apply()
    }

    private object PreferencesKeys {
        val SHARED_PREFERENCES_NAME = "user"
        val AVATAR = "avatar"
        val EMAIL = "email"
        val FULL_NAME = "full_name"
        val IS_DARK_MODE = "is_dark_mode"
        val LANG = "lang"
        val MOBILE = "mobile"
        val PASSENGER_CODE = "passenger_code"
        val RATE = "rate"
        val REMEMBER_TOKEN = "remember_token"
        val SHAKE_PHONE = "shake_phone"
        val STATUS = "status"
        val SUSPEND = "suspend"
        val IS_FIRST_RUN = "is_first_run"
        val IS_NEW_USER = "is_first_run"

    }


}