package com.stocks_analyzer.app.android.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 共通定数クラス.
 *
 * @author chotto-martini
 * @since 1.0.0 2018/04/24
 */
public class Constant {

    public static final String PREF_KEY_PREFIX = Constant.class.getName();
    public static final String PREF_KEY = PREF_KEY_PREFIX + "_PREF_KEY";

    public static final String PREF_KEY_TOTAL_ACCOUNT_VALUE_JP = PREF_KEY_PREFIX + "_PREF_KEY_TOTAL_ACCOUNT_VALUE_JP";

    /**
     * サフィックス付きプリファレンス用キーを生成する.
     *
     * @param date 日付
     * @param suffix サフィックス
     * @return サフィックス付き文字列
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public static String getPrefDateKey(Date date, String suffix) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.JAPAN);
        return sdf.format(date) + suffix;
    }
}
