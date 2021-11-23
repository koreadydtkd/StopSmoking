package hys.hmonkeyys.stopsmoking.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import hys.hmonkeyys.stopsmoking.R
import hys.hmonkeyys.stopsmoking.screen.intro.IntroActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {

    /** 토큰 갱신 */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
//        Log.i(TAG, token)

//        val spf = applicationContext.getSharedPreferences(APP_DEFAULT_KEY, Context.MODE_PRIVATE)
//        spf.edit().putString(FCM_TOKEN, token).apply()

//        saveTokenInFirebase(token)
    }

    /** FCM 메세지 수신 */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        createNotificationChannel()

        val type = remoteMessage.data["type"]?.toInt() ?: 0
        val title = remoteMessage.data["title"] ?: "제목"
        val message = remoteMessage.data["message"] ?: "내용"

        NotificationManagerCompat.from(this).notify(type, createNotification(type, title, message))
    }

    /** 알림 채널 생성 */
    private fun createNotificationChannel() {
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = CHANNEL_DESCRIPTION

        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)

    }

    /** 알림 생성 */
    private fun createNotification(type: Int, title: String, message: String): Notification {
            val intent = Intent(this, IntroActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(this, type, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)                           // 작은 아이콘 설정
                .setContentTitle(title)                                             // 제목
                .setContentText(message)                                            // 내용
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)                   // 우선순위
                .setContentIntent(pendingIntent)                                    // 펜딩인텐트 위임
                .setAutoCancel(true)                                                // 클릭 시 사라짐

            when (type) {
                0 -> {
                    // 기본
                }

                1 -> {
                    // 이미지
                }
            }

            return notificationBuilder.build()


    }

    companion object {
        private const val TAG = "SS_MyFirebaseMessagingService"
        private const val CHANNEL_ID = "stop_smoking_channel"
        private const val CHANNEL_NAME = "stop_smoking"
        private const val CHANNEL_DESCRIPTION = "진짜 금연 채널"
    }
}