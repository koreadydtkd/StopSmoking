package hys.hmonkeyys.stopsmoking.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import hys.hmonkeyys.stopsmoking.activity.intro.IntroActivity
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.APP_DEFAULT_KEY
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.STOP_SMOKING_DATE
import java.util.*
import android.content.ComponentName
import hys.hmonkeyys.stopsmoking.R
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.WIDGET_UPDATE


class WidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        val spf = context?.getSharedPreferences(APP_DEFAULT_KEY, Context.MODE_PRIVATE)

        // Provider 에 속한 각 앱 위젯에 대해 이 루프 절차를 수행합니다.
        appWidgetIds?.forEach { appWidgetId ->

            // Activity 를 시작하기 위한 인텐트 생성
            val pendingIntent: PendingIntent = Intent(context, IntroActivity::class.java)
                .let { intent ->
                    PendingIntent.getActivity(context, 0, intent, 0)
                }

            // 앱 위젯의 레이아웃을 가져오고 클릭 시 리스너를 버튼에 연결합니다.
            val views: RemoteViews = RemoteViews(context?.packageName, R.layout.widget).apply {
                val dDay = spf?.getString(STOP_SMOKING_DATE, "0") ?: "0"
                setTextViewText(R.id.dDayWidgetTextView, "금연한지 +${getDDay(dDay)}일")
                setOnClickPendingIntent(R.id.dDayWidgetTextView, pendingIntent)
            }

            // 현재 앱 위젯에서 업데이트를 수행하도록 AppWidgetManager에 지시
            appWidgetManager?.updateAppWidget(appWidgetId, views)
        }
    }


    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action.equals(WIDGET_UPDATE)) {
            // widget update started
            val remoteViews = RemoteViews(context.packageName, R.layout.widget)

            // Update text , images etc
            val spf = context.getSharedPreferences(APP_DEFAULT_KEY, Context.MODE_PRIVATE)
            val dDay = spf?.getString(STOP_SMOKING_DATE, "0") ?: "0"
            remoteViews.setTextViewText(R.id.dDayWidgetTextView, "금연한지 +${getDDay(dDay)}일")

            // Trigger widget layout update
            AppWidgetManager.getInstance(context).updateAppWidget(ComponentName(context, WidgetProvider::class.java), remoteViews)
        }
    }


    /** 오늘 날짜, 입력한 날짜로 d-day 계산 */
    private fun getDDay(stopSmokingDate: String): Int {
        return try {
            val dDayList = stopSmokingDate.split("-")

            val year = dDayList[0].toInt()
            val month = dDayList[1].toInt()
            val day = dDayList[2].toInt()

            val todayCal = Calendar.getInstance()
            val dDayCal = Calendar.getInstance()

            // D-day의 날짜를 입력
            dDayCal[year, month - 1] = day

            val today = todayCal.timeInMillis / DAY
            val dDay = dDayCal.timeInMillis / DAY

            // 오늘 날짜에서 d day 날짜를 빼기
            val count = today - dDay

            // 오늘 부터 시작이면 1일 차
            count.toInt() + 1
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    companion object {
        private const val DAY = 86400000
    }
}