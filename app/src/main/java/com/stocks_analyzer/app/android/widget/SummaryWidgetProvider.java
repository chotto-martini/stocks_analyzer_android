package com.stocks_analyzer.app.android.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.stocks_analyzer.app.android.R;
import com.stocks_analyzer.app.android.common.Logger;
import com.stocks_analyzer.app.android.task.AsyncSBIAcountSummayJPRequest;
import com.stocks_analyzer.app.android.widget.activity.SummaryWidgetProviderConfigureActivity;

/**
 * サマリウィジェットプロバイダー.
 * <p>ウィジェット設定画面 {@link SummaryWidgetProviderConfigureActivity SummaryWidgetProviderConfigureActivity}
 *
 * @author chotto-martini
 * @since 1.0.0 2018/04/22
 */
public class SummaryWidgetProvider extends AppWidgetProvider {

    /** インデントアクション：「更新」ボタン押下時 */
    private static final String INTENT_ACTION_UPDATE_BUTTON = "com.stocks_analyzer.app.android.widget.action.CLICK_ACTION_UPDATE";

    /**
     * ウィジェットを更新する.
     *
     * @param context コンテキスト
     * @param appWidgetManager ウィジェットマネージャ
     * @param appWidgetId ウィジェットID
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/22
     */
    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = SummaryWidgetProviderConfigureActivity.loadTitlePref(context, appWidgetId);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.summary_widget_provider);
        views.setTextViewText(R.id.appwidget_text, widgetText);
        views.setOnClickPendingIntent(R.id.updateButton, getPendingIntentClickUpdateButton(context, appWidgetId));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            SummaryWidgetProviderConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        Logger.d("Intent：intent=" + intent.getAction());

        // AppWidgetの画面更新
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.summary_widget_provider);

        // 「更新」ボタン押下時のインデントアクション。
        if (INTENT_ACTION_UPDATE_BUTTON.equals(intent.getAction())) {
            // SBI（国内）データ取得Service起動
            AsyncSBIAcountSummayJPRequest asynctask = new AsyncSBIAcountSummayJPRequest(context, appWidgetManager, views);
            asynctask.execute();
        }
    }

    /**
     * 「更新」ボタンクリックアクションのペンディングインデントを取得する.
     *
     * @param context コンテキスト
     * @param appWidgetId ウィジェットID
     * @return ペンディングインデント
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/22
     */
    private static PendingIntent getPendingIntentClickUpdateButton(Context context, int appWidgetId) {
        Intent intent = new Intent(context, SummaryWidgetProvider.class);
        intent.setAction(INTENT_ACTION_UPDATE_BUTTON);
        return PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}

