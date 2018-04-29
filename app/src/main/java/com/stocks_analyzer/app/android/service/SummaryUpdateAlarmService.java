package com.stocks_analyzer.app.android.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.stocks_analyzer.app.android.R;
import com.stocks_analyzer.app.android.common.Logger;
import com.stocks_analyzer.app.android.task.AsyncSBIAcountSummayJPRequest;
import com.stocks_analyzer.app.android.widget.SummaryWidgetProvider;

/**
 * 更新用スケジューラークラス.
 *
 * @author chotto-martini
 * @since 1.0.0 2018/04/24
 */
public class SummaryUpdateAlarmService extends IntentService {

    /**
     * コンストラクタ.
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public SummaryUpdateAlarmService() {
        super(SummaryUpdateAlarmService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Logger.d("更新用スケジューラ 起動");

        // AppWidgetの画面更新
        ComponentName widget = new ComponentName(this, SummaryWidgetProvider.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        // リモートview取得
        RemoteViews rv = new RemoteViews(getPackageName(), R.layout.summary_widget_provider);

        // SBI（国内）データ取得Service起動
        if (AsyncSBIAcountSummayJPRequest._validDoInBackground()) {
            AsyncSBIAcountSummayJPRequest asynctaskSBIjp = new AsyncSBIAcountSummayJPRequest(getApplicationContext(), appWidgetManager, rv);
            asynctaskSBIjp.execute();
        } else {
            Logger.d("SBI（国内）データ取得Service起動時間外です。");
        }

        appWidgetManager.updateAppWidget(widget, rv);
    }

    public static void startAlarm(Context context) {
        Intent serviceIntent = new Intent(context, SummaryUpdateAlarmService.class);

        // スケジューリングしてServiceを起動
        PendingIntent pendingIntent = PendingIntent.getService(
                context,
                // リクエストコード（※ユニークな値をふる）
                0,
                serviceIntent,
                // 存在していればそれを使う。新しい設定で置き換えない。
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                // 1時間おき
                60 * 60 * 1000,
                pendingIntent);
    }
}
