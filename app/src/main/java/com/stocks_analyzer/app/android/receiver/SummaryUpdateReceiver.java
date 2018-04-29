package com.stocks_analyzer.app.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.stocks_analyzer.app.android.service.SummaryUpdateAlarmService;

/**
 * 更新用レシーバークラス.
 *
 * @author chotto-martini
 * @since 1.0.0 2018/04/24
 */
public class SummaryUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SummaryUpdateAlarmService.startAlarm(context);
    }
}
