package com.stocks_analyzer.app.android.common;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;

/**
 * アプリケーションクラス.
 * <p>※アプリケーション固有のインスタンス管理を行う。
 * <p>※日本語を含むような定数定義を行う。
 *
 * @author chotto-martini
 * @since 1.0.0 2018/04/24
 */
public class StocksAnalyzerApplication extends Application {

    /** アプリケーションコンテキスト. */
    private static Context appContext;

    /** このクラスのインスタンス. */
    private static StocksAnalyzerApplication instance;

    /** プリファレンス操作のインスタンス. */
    private static StocksAnalyzerPreference appPreference;

    /** ライフサイクル監視コールバック. */
    public static final StocksAnalyzerActivityLifecycleCallbacks LIFECYCLE_CALLBACKS = new StocksAnalyzerActivityLifecycleCallbacks();

    @Override
    public void onCreate() {
        super.onCreate();

        // 初期化
        this.appContext = getApplicationContext();
        this.instance = this;
        this.appPreference = StocksAnalyzerPreference.getInstance(this.appContext);

        // ライフサイクルの監視コールバックを登録
        registerActivityLifecycleCallbacks(LIFECYCLE_CALLBACKS);
    }

    /**
     * アプリケーションコンテキストを取得する.
     *
     * @return アプリケーションコンテキスト
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public static Context getAppContext() {
        return appContext;
    }

    /**
     * このクラスのインスタンスを取得する.
     *
     * @return StocksAnalyzerApplication インスタンスを取得する。
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public static StocksAnalyzerApplication getInstance() {
        return instance;
    }

    /**
     * プリファレンス操作のインスタンスを取得する.
     *
     * @return StocksAnalyzerPreference インスタンスを取得する。
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public static StocksAnalyzerPreference getPreference() {
        return appPreference;
    }

    /**
     * フォアグラウンドかどうか判定する.
     *
     * @return フォアグラウンドなら true
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public static boolean isInForeground() {
        return LIFECYCLE_CALLBACKS.isApplicationInForeground();
    }

    /**
     * アプリバージョンを取得する.
     *
     * @return アプリバージョン文字列
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public static String getAppVersion() {
        PackageManager packageManager = StocksAnalyzerApplication.getInstance().getPackageManager();

        // バージョンを取得
        try {
            return packageManager.getPackageInfo(StocksAnalyzerApplication.getInstance().getPackageName(), PackageManager.GET_ACTIVITIES).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Logger.e("バージョン情報取得に失敗しました。：e=" + e.getMessage(), e);
        }
        // エラーのときは初期値を返す。
        return "1.0.0";
    }
}
