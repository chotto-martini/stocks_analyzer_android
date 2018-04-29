package com.stocks_analyzer.app.android.common;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * ライフサイクルコールバッククラス.
 *
 * @author chotto-martini
 * @since 1.0.0 2018/04/24
 */
public class StocksAnalyzerActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    private int created;
    private int resumed;
    private int paused;
    private int started;
    private int stopped;
    private int destroyed;
    private WeakReference<Activity> current;
    private List<WeakReference<Activity>> activityList = new ArrayList<>();

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        ++created;
        activityList.add(new WeakReference<>(activity));
        current = new WeakReference<>(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        ++started;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        ++resumed;
        current = new WeakReference<>(activity);
    }

    public void onActivityResult(Activity activity) {
        current = new WeakReference<>(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        ++paused;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        ++stopped;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {}

    @Override
    public void onActivityDestroyed(Activity activity) {
        ++destroyed;
    }

    public boolean isApplicationVisible() {
        return started > stopped;
    }

    public boolean isApplicationInForeground() {
        return resumed > paused;
    }

    public boolean isApplicationRunning() {
        return created > destroyed;
    }

    /**
     * バックグラウンドから復帰したか.
     * <p>【注意】Activity.onRestart() から呼ばれた際に正しく動くように調整してあるため、他の場所では呼ばないこと！！
     *
     * ※バックグラウンドに移行した場合、onStarted された全ての Activity で onStopped が呼ばれた状態（started == stopped）。
     * ※バックグラウンドから復帰した際、onRestarted が呼ばれる。そのタイミングではまだ onStarted が呼ばれていないため、 started == stopped のまま。
     * ※重ねて開いていたActivityから戻ってきた場合にも onRestarted が呼ばれるが、この際はまだ onStop が呼ばれていないため、 started > stopped となり、false が返る
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public boolean isReturnFromBackgroundForOnlyOnRestart() {
        return (started == stopped);
    }

    public Activity getCurrentActivity() {
        return (current != null) ? current.get() : null;
    }

    /**
     * クラスを指定してアクティビティを終了する.
     * <p>※バックスタックに含まれるアクティビティを後から消したくなった際に利用する。
     *
     * @param clazz 対象クラス
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public void finishActivity(Class clazz) {
        for (WeakReference<Activity> activityWeakReference : activityList) {
            Activity activity = activityWeakReference.get();
            if (activity != null && activity.getClass().equals(clazz)) {
                Logger.i("Activity finish. " + clazz);
                activity.finish();
            }
        }
    }

    @Override
    public String toString() {
        return String.format(Locale.JAPAN, "created=%d, resumed=%d, paused=%d, started=%d, stopped=%d, destroyed=%d, current=%s",
                created, resumed, paused, started, stopped, destroyed, (current != null ? current.getClass() : "empty"));
    }
}