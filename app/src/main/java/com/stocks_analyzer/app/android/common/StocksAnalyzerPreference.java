package com.stocks_analyzer.app.android.common;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * プリファレンス関連の処理をまとめたクラス.
 * <p>※シングルトンパターン。
 *
 * @author chotto-martini
 * @since 1.0.0 2018/04/24
 */
public class StocksAnalyzerPreference extends Application {

    /* ----------------------------------------------------------------------------------------------------
     * プリファレンスキー定義
     * ---------------------------------------------------------------------------------------------------- */
    /** 共通 SharedPreference キー. */
    private static final String PREF_KEY_COMMON = "private_config";


    /* ----------------------------------------------------------------------------------------------------
     * このクラス関連・その他の定義
     * ---------------------------------------------------------------------------------------------------- */
    /** このクラスのインスタンス. */
    private static StocksAnalyzerPreference instance;

    /** SharedPreference. */
    private SharedPreferences sharedPref;

    /**
     * ※コンストラクタは提供していない.
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    private StocksAnalyzerPreference() {}

    /**
     * このクラスのインスタンスを返す.
     * <p>※インスタンス生成はこの関数でもできるが、基本 {@link StocksAnalyzerApplication#getPreference()} で行う。
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public static StocksAnalyzerPreference getInstance(Context context) {
        if (instance == null) {
            instance = new StocksAnalyzerPreference();
            instance.sharedPref = context.getSharedPreferences(PREF_KEY_COMMON, Context.MODE_PRIVATE);
        }
        return instance;
    }
}
