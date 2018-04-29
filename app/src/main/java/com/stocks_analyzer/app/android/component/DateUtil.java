package com.stocks_analyzer.app.android.component;

import java.util.Calendar;
import java.util.Date;

/**
 * 日付に関するユーティリティクラス.
 *
 * @author chotto-martini
 * @since 1.0.0 2018/04/24
 */
public class DateUtil {

    /**
     * 日数を加算（減算）する.
     *
     * @param date 加算（減算）対象の日付
     * @param addDays 加算（減算）する日数
     * @return 加算（減算）結果の日付
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public static Date addDate(Date date, int addDays) {
        if(date == null){
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, addDays);
        return cal.getTime();
    }

    /**
     * 指定時間をセットする.
     * 
     * @param date 指定時間を設定する対象の日付
     * @param hourOfDay 設定する時間
     * @return 指定時間をセットした日付
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public static Date setHourOfDay(Date date, int hourOfDay) {
        if(date == null){
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 0, 0, 0);
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);

        return cal.getTime();
    }
}
