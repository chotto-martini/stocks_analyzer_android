package com.stocks_analyzer.app.android.common;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * オブジェクトチェックに関する機能を提供します.
 *
 * @author chotto-martini
 * @since 1.0.0 2018/04/24
 */
public class CheckUtils {

    /**
     * 引数の{@code list}が{@code null}または、
     * {@link List#isEmpty()}の場合{@code true}を返します。
     * 要素が存在する場合は{@code false}を返します。
     *
     * @param list チェック対象のリスト
     * @return 引数の{@code list}が{@code null}または、
     *             {@link List#isEmpty()}の場合{@code true}。
     *             要素が存在する場合は{@code false}。
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public static boolean isEmpty(List<?> list) {
        if (list == null || list.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * 引数の{@code map}が{@code null}または、
     * {@link Map#isEmpty()}の場合{@code true}を返します。
     * 要素が存在する場合は{@code false}を返します。
     *
     * @param map チェック対象のマップ
     * @return 引数の{@code map}が{@code null}または、
     *             {@link Map#isEmpty()}の場合{@code true}。
     *             要素が存在する場合は{@code false}。
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public static boolean isEmpty(Map<?, ?> map) {
        if (map == null || map.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * 引数の{@code hashSet}が{@code null}または、
     * {@link HashSet#isEmpty()}の場合{@code true}を返します。
     * 要素が存在する場合は{@code false}を返します。
     *
     * @param hashSet チェック対象のリスト
     * @return 引数の{@code hashSet}が{@code null}または、
     *             {@link HashSet#isEmpty()}の場合{@code true}。
     *             要素が存在する場合は{@code false}。
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public static boolean isEmpty(HashSet<?> hashSet) {
        if (hashSet == null || hashSet.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * 引数の{@code str}が{@code null}または、
     * {@link String#isEmpty()}の場合{@code true}を返します。
     * 要素が存在する場合は{@code false}を返します。
     *
     * @param str チェック対象の文字列
     * @return 引数の{@code str}が{@code null}または、
     *             {@link Map#isEmpty()}の場合{@code true}。
     *             要素が存在する場合は{@code false}。
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public static boolean isEmpty(String str) {
        if (str == null || str.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * 引数の{@code num}が{@code null}または、
     * {@link Number#doubleValue()}の結果が0.0以下の場合{@code true}を返します。
     * 要素が存在する場合は{@code false}を返します。
     *
     * @param num チェック対象の数値
     * @return 引数の{@code num}が{@code null}または、
     *             {@link Number#doubleValue()}の結果が0.0以下の場合{@code true}。
     *             要素が存在する場合は{@code false}。
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public static boolean isEmpty(Number num) {
        if (num == null || num.doubleValue() <= 0.0) {
            return true;
        }
        return false;
    }
}
