package com.stocks_analyzer.app.android.common;

import android.util.Log;

import com.stocks_analyzer.app.android.BuildConfig;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * ログ出力クラス.
 *
 * @author chotto-martini
 * @since 1.0.0 2018/04/24
 */
public class Logger {
    private static final int TRACE_CALLER_COUNT = 2;

    public static void v() {
        if (BuildConfig.SUPER_DEBUG) {
            Log.v(getClassName(), getCommonValues());
        }
    }

    public static void v(String msg) {
        if (BuildConfig.SUPER_DEBUG) {
            Log.v(getClassName(), getMethodName() + ", " + nonNull(msg) + " ### " + getCommonValues());
        }
    }

    public static void d() {
        if (BuildConfig.SUPER_DEBUG) {
            Log.d(getClassName(), getCommonValues());
        }
    }

    public static void d(String msg) {
        if (BuildConfig.SUPER_DEBUG) {
            Log.d(getClassName(), getMethodName() + ", " + nonNull(msg) + " ### " + getCommonValues());
        }
    }

    public static void hr() {
        if (BuildConfig.SUPER_DEBUG) {
            // ログの途中で区切りをつけたくなったとき用。
            Log.i("##########", "######################################################################");
        }
    }

    public static void i() {
        if (BuildConfig.SUPER_DEBUG) {
            Log.i(getClassName(), getMethodName());
        }
    }

    public static void i(String msg) {
        if (BuildConfig.SUPER_DEBUG) {
            Log.i(getClassName(), getMethodName() + ", " + nonNull(msg));
        }
    }

    public static void w(String msg) {
        if (BuildConfig.SUPER_DEBUG) {
            Log.w(getClassName(), getMethodName() + ", " + nonNull(msg));
        }
    }

    public static void w(String msg, Throwable e) {
        if (BuildConfig.SUPER_DEBUG) {
            Log.w(getClassName(), getMethodName() + ", " + nonNull(msg), e);
        }
    }

    public static void e(String msg) {
        if (BuildConfig.SUPER_DEBUG) {
            Log.e(getClassName(), getMethodName() + ", " + nonNull(msg));
        }
    }

    public static void e(String msg, Throwable e) {
        if (BuildConfig.SUPER_DEBUG) {
            Log.e(getClassName(), getMethodName() + ", " + nonNull(msg), e);
        }
    }

    private static String nonNull(String s) {
        if (s == null) {
            return "(null)";
        }
        return s;
    }

    private static String getClassName() {
        String fn = "";
        try {
            fn = new Throwable().getStackTrace()[TRACE_CALLER_COUNT].getClassName();

            String className = new Throwable().getStackTrace()[TRACE_CALLER_COUNT].getClassName();
            String[] classNames = className.split("\\.");
            fn = classNames[classNames.length - 1];

        } catch (Exception e) {
            Logger.w("", e);
        }
        return fn;
    }

    private static String getMethodName() {
        String str = "";
        try {
            StackTraceElement element = new Throwable().getStackTrace()[TRACE_CALLER_COUNT];
            str = element.getMethodName() + "(" + element.getLineNumber() + ")";
        } catch (Exception e) {
            Logger.w("", e);
        }
        return str;
    }

    private static String getCommonValues() {
        String str = "";
        try {
            // ※共通出力したい内容を実装。
        } catch (Exception e) {
            Logger.w("", e);
        }
        return str;
    }

    /**
     * 呼び出し元の「クラス名」・「メソッド名」・「行番号」を取得する.
     *
     * @return String    「クラス名」・「メソッド名」・「行番号」
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public static String print() {
        Throwable throwable = new Throwable();
        StringBuffer sb = new StringBuffer();
        StackTraceElement[] stackTrace = throwable.getStackTrace();

        if (stackTrace.length > 2) {
            sb.append(stackTrace[2].getClassName()+".")
                    .append(stackTrace[2].getMethodName()+"(")
                    .append(stackTrace[2].getFileName()+":")
                    .append(stackTrace[2].getLineNumber()+")");
        }

        return sb.toString();
    }

    /**
     * スタックトレースの文字列を取得する.
     *
     * @param t Throwable
     * @return スタックトレースの文字列
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public static String toStringPrintStackTrace(Throwable t) {
        // エラーのスタックトレースを表示
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }
}