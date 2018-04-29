package com.stocks_analyzer.app.android.entity;

import com.stocks_analyzer.app.android.BuildConfig;

import org.apache.http.message.BasicNameValuePair;

import java.util.HashMap;
import java.util.Map;

/**
 * 設定値を操作するユーティリティクラス.
 *
 * @author chotto-martini
 * @since 1.0.0 2018/04/24
 */
public class SettingUtil {

    /** SBIアカウント */
    public static String SBI_ACCOUNT = BuildConfig.SBI_ACCOUNT;
    /** SBIパスワード */
    public static String SBI_PASSWORD = BuildConfig.SBI_PASSWORD;

    /**
     * 設定値をセットしたentityを返す.
     *
     * @return エンティティ
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public static SettingEntity getSettingEntity(){
        SettingEntity settingEntity = new SettingEntity();
        settingEntity.name = "SBI証券";
        settingEntity.loginUrl = "https://k.sbisec.co.jp/bsite/visitor/loginUserCheck.do";
        settingEntity.loginPostParams.add(new BasicNameValuePair("username", SBI_ACCOUNT));
        settingEntity.loginPostParams.add(new BasicNameValuePair("password", SBI_PASSWORD));

        Map<String, String> map = new HashMap<String, String>();
        map.put(SettingEntity.DATA_MAP_KEY_NAME, "買付余力");
        map.put(SettingEntity.DATA_MAP_KEY_KEY, "buyingPowerCash");
        map.put(SettingEntity.DATA_MAP_KEY_URL, "https://k.sbisec.co.jp/bsite/member/acc/purchaseMarginList.do");
        map.put(SettingEntity.DATA_MAP_KEY_CSS_PATH, "html body table tbody tr:eq(0) td:eq(0) div table tbody tr:eq(0) td:eq(0) table tbody tr:eq(0) td:eq(0) table tbody tr:eq(0) td:eq(0) table tbody tr:eq(1) td:eq(1)");
        settingEntity.data.add(map);

        map = new HashMap<String, String>();
        map.put(SettingEntity.DATA_MAP_KEY_NAME, "評価額合計");
        map.put(SettingEntity.DATA_MAP_KEY_KEY, "marketValueCash");
        map.put(SettingEntity.DATA_MAP_KEY_URL, "https://k.sbisec.co.jp/bsite/member/acc/holdStockList.do");
        map.put(SettingEntity.DATA_MAP_KEY_CSS_PATH, "html body table tbody tr:eq(0) td:eq(0) table tbody tr:eq(0) td:eq(0) table tbody tr:eq(0) td:eq(0) table tbody tr:eq(0) td:eq(1)");
        settingEntity.data.add(map);

        map = new HashMap<String, String>();
        map.put(SettingEntity.DATA_MAP_KEY_NAME, "損益合計");
        map.put(SettingEntity.DATA_MAP_KEY_KEY, "marketValueCashGainLoss");
        map.put(SettingEntity.DATA_MAP_KEY_URL, "https://k.sbisec.co.jp/bsite/member/acc/holdStockList.do");
        map.put(SettingEntity.DATA_MAP_KEY_CSS_PATH, "html body table tbody tr:eq(0) td:eq(0) table tbody tr:eq(0) td:eq(0) table tbody tr:eq(0) td:eq(0) table tbody tr:eq(0) td:eq(3)");
        settingEntity.data.add(map);

        map = new HashMap<String, String>();
        map.put(SettingEntity.DATA_MAP_KEY_NAME, "建代金合計");
        map.put(SettingEntity.DATA_MAP_KEY_KEY, "marketValueMargin");
        map.put(SettingEntity.DATA_MAP_KEY_URL, "https://k.sbisec.co.jp/bsite/member/acc/positionList.do");
        map.put(SettingEntity.DATA_MAP_KEY_CSS_PATH, "html body table tbody tr:eq(0) td:eq(0) div table tbody tr:eq(0) td:eq(0) table tbody tr:eq(0) td:eq(0) table tbody tr:eq(1) td:eq(1)");
        settingEntity.data.add(map);

        map = new HashMap<String, String>();
        map.put(SettingEntity.DATA_MAP_KEY_NAME, "建玉評価損益額");
        map.put(SettingEntity.DATA_MAP_KEY_KEY, "marketValueMarginGainLoss");
        map.put(SettingEntity.DATA_MAP_KEY_URL, "https://k.sbisec.co.jp/bsite/member/acc/positionList.do");
        map.put(SettingEntity.DATA_MAP_KEY_CSS_PATH, "html body table tbody tr:eq(0) td:eq(0) div table tbody tr:eq(0) td:eq(0) table tbody tr:eq(0) td:eq(0) table tbody tr:eq(2) td:eq(1)");
        settingEntity.data.add(map);

        map = new HashMap<String, String>();
        map.put(SettingEntity.DATA_MAP_KEY_NAME, "実質保証金");
        map.put(SettingEntity.DATA_MAP_KEY_KEY, "marketValueDeposit");
        map.put(SettingEntity.DATA_MAP_KEY_URL, "https://k.sbisec.co.jp/bsite/member/acc/RealMaintenanceFactor.do");
        map.put(SettingEntity.DATA_MAP_KEY_CSS_PATH, "html body table tbody tr:eq(0) td:eq(0) div table tbody tr:eq(7) td:eq(1)");
        settingEntity.data.add(map);
        return settingEntity;
    }
}
