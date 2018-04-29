package com.stocks_analyzer.app.android.entity;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 設定ファイル読み込み用エンティティ
 *
 * @author chotto-martini
 * @since 1.0.0 2018/04/24
 */
public class SettingEntity {

    /** マップ用キー：項目名 */
    public static final String DATA_MAP_KEY_NAME = "name";
    /** マップ用キー：項目キー */
    public static final String DATA_MAP_KEY_KEY = "key";
    /** マップ用キー：項目取得先のURL */
    public static final String DATA_MAP_KEY_URL = "url";
    /** マップ用キー：項目取得先のcss path */
    public static final String DATA_MAP_KEY_CSS_PATH = "cssPath";

    /** ルート名：証券会社名など */
    public String name = "";
    /** ログインURL */
    public String loginUrl = "";
    /** ログインPostパラメータ */
    public List<NameValuePair> loginPostParams = new ArrayList<NameValuePair>();
    /** データセット */
    public List<Map<String, String>> data = new ArrayList<Map<String, String>>();
}
