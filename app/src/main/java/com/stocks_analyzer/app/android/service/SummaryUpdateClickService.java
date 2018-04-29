package com.stocks_analyzer.app.android.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.stocks_analyzer.app.android.R;
import com.stocks_analyzer.app.android.common.Logger;
import com.stocks_analyzer.app.android.task.AsyncSBIAcountSummayJPRequest;
import com.stocks_analyzer.app.android.widget.SummaryWidgetProvider;

/**
 * ウィジェット更新処理を行うサービスクラス.
 *
 * @author chotto-martini
 * @since 1.0.0 2018/04/24
 */
public class SummaryUpdateClickService extends Service {
    public static final String _CLICK_ACTION = SummaryUpdateClickService.class.getSimpleName();

    public class BindServiceBinder extends Binder {
        SummaryUpdateClickService getService(){
            return SummaryUpdateClickService.this;
        }
    }

    private final IBinder mBinder = new BindServiceBinder();

    @Override
    public IBinder onBind(Intent intent) {
        Logger.d("▼▼▼サービス起動▼▼▼");

        // ボタンが押された時に発行されるインテントを準備する
        Intent buttonIntent = new Intent();
        buttonIntent.setAction(_CLICK_ACTION);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, buttonIntent, 0);

        // リモートview取得
        RemoteViews rv = new RemoteViews(getPackageName(), R.layout.summary_widget_provider);
        rv.setOnClickPendingIntent(R.id.updateButton, pendingIntent);

        // AppWidgetの画面更新
        ComponentName widget = new ComponentName(this, SummaryWidgetProvider.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        // ボタンが押された時の処理
        if (_CLICK_ACTION.equals(intent.getAction())) {
            Logger.d("ボタン押下処理 開始：" + _CLICK_ACTION);

            // SBI（国内）データ取得Service起動
            AsyncSBIAcountSummayJPRequest asynctask = new AsyncSBIAcountSummayJPRequest(getApplicationContext(), appWidgetManager, rv);
            asynctask.execute();

            Logger.d("ボタン押下処理 終了：" + _CLICK_ACTION);
        }

        appWidgetManager.updateAppWidget(widget, rv);

        Logger.d("▲▲▲サービス終了▲▲▲");
        return mBinder;
    }
}
