package hys.hmonkeyys.stopsmoking.utils

class AppShareKey {
    companion object {
        const val APP_DEFAULT_KEY = "stop_smoking"

        // SharedPreference key
        const val IS_REGISTRATION = "is_registration"
        const val STOP_SMOKING_DATE = "stop_smoking_date"
        const val NICK_NAME = "nick_name"
        const val AMOUNT_OF_SMOKING_PER_DAY = "amount_of_smoking_per_day"
        const val TOBACCO_PRICE = "tobacco_price"
        const val MY_RESOLUTION = "my_resolution"

        const val FIREBASE_IMAGE_URL = "firebase_image_url"
//        const val FCM_TOKEN = "firebase_token"

        // share intent key
        const val EDIT = "edit"

        // firebase
        const val DB_NICKNAME = "db_nick_name"

        // widget
        const val WIDGET_UPDATE ="update_widget"
    }
}