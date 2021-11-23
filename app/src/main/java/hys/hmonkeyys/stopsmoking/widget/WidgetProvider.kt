package hys.hmonkeyys.stopsmoking.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import hys.hmonkeyys.stopsmoking.R
import hys.hmonkeyys.stopsmoking.data.preference.AppPreferenceManager.Companion.APP_DEFAULT_KEY
import hys.hmonkeyys.stopsmoking.data.preference.AppPreferenceManager.Companion.STOP_SMOKING_DATE
import hys.hmonkeyys.stopsmoking.screen.intro.IntroActivity
import hys.hmonkeyys.stopsmoking.utils.Constant.WIDGET_UPDATE
import hys.hmonkeyys.stopsmoking.utils.Utility.dDayCalculation


class WidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        val spf = context?.getSharedPreferences(APP_DEFAULT_KEY, Context.MODE_PRIVATE)

        // Provider 에 속한 각 앱 위젯에 대해 루프 절차 수행
        appWidgetIds?.forEach { appWidgetId ->

            // Activity 를 시작하기 위한 인텐트 생성
            val pendingIntent: PendingIntent = Intent(context, IntroActivity::class.java).let { intent ->
                PendingIntent.getActivity(context, 0, intent, 0)
            }

            // 앱 위젯의 레이아웃을 가져오고 클릭 시 리스너를 버튼에 연결합니다.
            val views: RemoteViews = RemoteViews(context?.packageName, R.layout.widget).apply {
                val dDay = spf?.getString(STOP_SMOKING_DATE, "0") ?: "0"
                setTextViewText(R.id.dDayWidgetTextView, "금연한지 +${dDayCalculation(dDay)}일")
                setOnClickPendingIntent(R.id.dDayWidgetTextView, pendingIntent)
            }

            // 현재 앱 위젯에서 업데이트를 수행하도록 AppWidgetManager에 지시
            appWidgetManager?.updateAppWidget(appWidgetId, views)
        }
    }

    /** 위젯 업데이트 */
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        // 업데이트 요청 확인
        if (intent.action.equals(WIDGET_UPDATE)) {
            // 재 설정 된 날짜 데이터 가져오기
            val spf = context.getSharedPreferences(APP_DEFAULT_KEY, Context.MODE_PRIVATE)
            val dDay = spf?.getString(STOP_SMOKING_DATE, "0") ?: "0"

            // 변경된 데이터 웨젯에 업데이트
            val remoteViews = RemoteViews(context.packageName, R.layout.widget)
            remoteViews.setTextViewText(R.id.dDayWidgetTextView, "금연한지 +${dDayCalculation(dDay)}일")
            AppWidgetManager.getInstance(context).updateAppWidget(ComponentName(context, WidgetProvider::class.java), remoteViews)
        }
    }

    companion object {
        private const val DAY = 86400000
    }
}