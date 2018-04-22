package com.stocks_analyzer.app.android.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import com.stocks_analyzer.app.android.R;
import com.stocks_analyzer.app.android.widget.activity.SummaryWidgetProviderConfigureActivity;

/**
 * サマリウィジェットプロバイダー.
 * <p>ウィジェット設定画面 {@link SummaryWidgetProviderConfigureActivity SummaryWidgetProviderConfigureActivity}
 *
 * @author chotto-martini
 * @since 1.0.0 2018/04/22
 */
public class SummaryWidgetProvider extends AppWidgetProvider {

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
}

