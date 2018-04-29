package com.stocks_analyzer.app.android.component;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.util.Log;

import com.stocks_analyzer.app.android.BuildConfig;
import com.stocks_analyzer.app.android.common.Constant;
import com.stocks_analyzer.app.android.common.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Googleカレンダー操作クラス
 *
 * @author chotto-martini
 * @since 1.0.0 2018/04/24
 */
public class GCalendar {

    /** 投稿用カレンダーアカウント. */
    private static final String CALENDAR_ACCOUNT = BuildConfig.CALENDAR_ACCOUNT;

    /** カレンダー情報取得カラム */
    private static final String[] _EVENT_PROJECTION = new String[] {
        Calendars._ID,
        Calendars.ACCOUNT_NAME,
        Calendars.CALENDAR_DISPLAY_NAME
    };

    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;

    private static final String[] _EVENT_COL = new String[] {
        Events.TITLE,
        Events.DTSTART,
    };

    /** コンテキスト */
    private Context mContext;

    /**
     * デフォルトコンストラクタ.
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public GCalendar(Context context) {
        this.mContext = context;
    }


    /**
     * 更新対象のカレンダーIDを取得する.
     *
     * @return カレンダーID
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    private long _getCalId() {
        // カレンダー取得テスト
        // Run query
        Cursor cur = null;
        ContentResolver cr = this.mContext.getContentResolver();
        Uri uri = Calendars.CONTENT_URI;
        String selection =
                "((" + Calendars.ACCOUNT_NAME + " = ?) AND (" + Calendars.ACCOUNT_TYPE + " = ?))";
        String[] selectionArgs = new String[] {CALENDAR_ACCOUNT, "com.google"};
        // Submit the query and get a Cursor object back.
        cur = cr.query(uri, _EVENT_PROJECTION, selection, selectionArgs, null);

        // Use the cursor to step through the returned records
        long retCalId = 0;
        while (cur.moveToNext()) {
            long calID = 0;
            String displayName = null;
            String accountName = null;

            // Get the field values
            calID = cur.getLong(PROJECTION_ID_INDEX);
            displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);

            // Do something with the values...
            Logger.d(calID + ":" + displayName + ":" + accountName);

            if ("口座サマリー".equals(displayName)) {
                retCalId = calID;
                break;
            }
        }
        return retCalId;
    }

    /**
     * イベントを追加する.
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public void insertEvent(String title, Date now) {
        Logger.d("▼▼▼ カレンダー更新処理：開始 ▼▼▼");
        // イベント追加テスト
        long calID = _getCalId();
        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        beginTime.setTime(now);
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.setTime(now);
        endMillis = endTime.getTimeInMillis();

        ContentResolver cr = this.mContext.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, startMillis);
        values.put(Events.DTEND, endMillis);
        values.put(Events.TITLE, title);
        values.put(Events.CALENDAR_ID, calID);
        values.put(Events.EVENT_TIMEZONE, "Asia/Tokyo");
        cr.insert(Events.CONTENT_URI, values);
        Logger.d("▲▲▲ カレンダー更新処理：終了 ▲▲▲");
    }

    /**
     * タイトルを取得する.
     *
     * @return
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public String selectEvent(String str) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 0, 0, 0);

        Date startDate = DateUtil.addDate(cal.getTime(), -5);

        // カレンダー取得テスト
        Cursor cur = null;
        ContentResolver cr = this.mContext.getContentResolver();
        Uri uri = Events.CONTENT_URI;
        String selection = Events.CALENDAR_ID + " = ? and " + Events.DTSTART + " >= ?";
        String[] selectionArgs = new String[] {String.valueOf(_getCalId()), String.valueOf(startDate.getTime())};
        // Submit the query and get a Cursor object back.
        cur = cr.query(uri, _EVENT_COL, selection, selectionArgs, null);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.JAPAN);

        StringBuffer titles = new StringBuffer();
        while (cur.moveToNext()) {
            String title = cur.getString(0);
            long start = cur.getLong(1);

            if (title.indexOf(str) > -1) {
                String item = sdf.format(new Date(start)) + " " + title;
                Logger.d(sdf.format(new Date(start)) + " " + title);
                titles.append(item).append(System.getProperty("line.separator"));
            }
        }

        return titles.toString();
    }
}
