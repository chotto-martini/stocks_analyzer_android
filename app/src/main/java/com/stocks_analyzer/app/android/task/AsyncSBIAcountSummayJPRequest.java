package com.stocks_analyzer.app.android.task;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.RemoteViews;

import com.stocks_analyzer.app.android.R;
import com.stocks_analyzer.app.android.common.Constant;
import com.stocks_analyzer.app.android.common.Logger;
import com.stocks_analyzer.app.android.component.DateUtil;
import com.stocks_analyzer.app.android.component.GCalendar;
import com.stocks_analyzer.app.android.component.sbi.SBIScraper;
import com.stocks_analyzer.app.android.entity.SettingEntity;
import com.stocks_analyzer.app.android.entity.SettingUtil;
import com.stocks_analyzer.app.android.widget.SummaryWidgetProvider;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * データ取得処理.
 *
 * @author chotto-martini
 * @since 1.0.0 2018/04/24
 */
public class AsyncSBIAcountSummayJPRequest extends AsyncTask<Void, Void, Map<String, Integer>> {

    /** コンテキスト */
    private Context mContext;
    /** ウィジェット */
    private AppWidgetManager mAppWidgetManager;
    /** View */
    private RemoteViews mRemoteViews;

    /**
     * コンストラクタ.
     *
     * @param context コンテキスト
     * @param appWidgetManager ウィジェットマネージャ
     * @param remoteViews リモートView
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public AsyncSBIAcountSummayJPRequest(Context context, AppWidgetManager appWidgetManager, RemoteViews remoteViews) {
        this.mContext = context;
        this.mAppWidgetManager = appWidgetManager;
        this.mRemoteViews = remoteViews;

        // 更新日時をセット
        Date date = new Date();
        remoteViews.setTextViewText(R.id.update, context.getString(R.string.format_item_update, date));
    }

    /* (非 Javadoc)
     * @see android.os.AsyncTask#doInBackground(Params[])
     * バックグラウンド処理
     */
    @Override
    protected Map<String, Integer> doInBackground(Void... params) {

        int gain = 0;
        int loss = 0;
        int marginGain = 0;
        int marginLoss = 0;

        try {
            SBIScraper scraper = new SBIScraper(SettingUtil.SBI_ACCOUNT, SettingUtil.SBI_PASSWORD);
            List<SBIScraper.TickerInfo> marketValueTickerInfoList = scraper.getMarketValueTickerInfoList();
            for (SBIScraper.TickerInfo tickerInfo : marketValueTickerInfoList) {
                if (tickerInfo.gainLoss >= 0) {
                    gain = gain + tickerInfo.gainLoss;
                } else {
                    loss = loss + tickerInfo.gainLoss;
                }

            }
            List<SBIScraper.TickerInfo> marketValueMarginTickerInfoList = scraper.getMarketValueMarginTickerInfoList();
            for (SBIScraper.TickerInfo tickerInfo : marketValueMarginTickerInfoList) {
                if (tickerInfo.gainLoss >= 0) {
                    marginGain = marginGain + tickerInfo.gainLoss;
                } else {
                    marginLoss = marginLoss + tickerInfo.gainLoss;
                }

            }

        } catch (SBIScraper.SBIScraperException e) {
            Logger.w(e.getMessage(), e);
        }

        final SettingEntity entity = SettingUtil.getSettingEntity();
        String ret = "";

        URI loginUrl = null;
        try {
            loginUrl = new URI(entity.loginUrl);
        } catch (URISyntaxException e) {
            Logger.d("ログインURL作成に失敗しました。");
        }

        HttpPost httpPost = new HttpPost(loginUrl);

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(entity.loginPostParams, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Logger.d(e.getMessage());
        }

        Map<String, Integer> retMap = null;
        DefaultHttpClient httpClient = new DefaultHttpClient();
        try {
            Logger.d("▼▼▼ ログイン開始 ▼▼▼");

            // ログイン処理
            ret = httpClient.execute(httpPost, new ResponseHandler<String>(){
                @Override
                public String handleResponse(HttpResponse response)
                        throws ClientProtocolException, IOException {

                    Logger.d("レスポンスコード:" + response.getStatusLine().getStatusCode());

                    switch (response.getStatusLine().getStatusCode()) {
                        case HttpStatus.SC_OK:
                            Logger.d("レスポンス取得に成功");
                            return EntityUtils.toString(response.getEntity(), "UTF-8");

                        case HttpStatus.SC_NOT_FOUND:
                            Logger.d("データが存在しない");
                            return null;

                        default:
                            Logger.d("通信エラー");
                            return null;
                    }
                }
            });

            Logger.d("▲▲▲ ログイン終了 ▲▲▲");
            if(ret == null){
                Logger.d("ログイン失敗");
                return null;
            }

            // データ取得処理
            Logger.d("▼▼▼ データ取得開始 ▼▼▼");
            retMap = new HashMap<String, Integer>();
            for(final Map<String, String> data : entity.data){

                httpPost = new HttpPost(new URI(data.get(SettingEntity.DATA_MAP_KEY_URL)));
                // ログインしたHttpClientを使いまわしてデータを取得する。
                ret = httpClient.execute(httpPost, new ResponseHandler<String>(){
                    @Override
                    public String handleResponse(HttpResponse response)
                            throws ClientProtocolException, IOException {

                        Logger.d("レスポンスコード(" + data.get(SettingEntity.DATA_MAP_KEY_KEY) + "):" + response.getStatusLine().getStatusCode());

                        switch (response.getStatusLine().getStatusCode()) {
                            case HttpStatus.SC_OK:
                                Logger.d("レスポンス取得に成功");
                                return EntityUtils.toString(response.getEntity(), "UTF-8");

                            case HttpStatus.SC_NOT_FOUND:
                                Logger.d("データが存在しない");
                                return null;

                            default:
                                Logger.d("通信エラー");
                                return null;
                        }
                    }
                });

                if(ret == null){
                    Logger.d("データ取得失敗:" + data.get(SettingEntity.DATA_MAP_KEY_KEY));
                    return null;
                }else{
                    Logger.d("データ取得成功:" + data.get(SettingEntity.DATA_MAP_KEY_KEY));
                }

                // HTML解析
                Document document = Jsoup.parse(ret);
                Elements e = document.select(data.get(SettingEntity.DATA_MAP_KEY_CSS_PATH));
                ret = e.eq(0).text();
                Integer iRet = (int) Double.parseDouble(ret.replaceAll("[^0-9\\-.]", ""));

                // 取得データを戻り値のマップに詰め込む
                Logger.d("■ 取得データ:" + data.get(SettingEntity.DATA_MAP_KEY_NAME) + " ■:" + data.get(SettingEntity.DATA_MAP_KEY_KEY) + "=" + ret);
                retMap.put(data.get(SettingEntity.DATA_MAP_KEY_KEY), iRet);
            }
            Logger.d("▲▲▲ データ取得終了 ▲▲▲");

        } catch (ClientProtocolException e) {
            Logger.d(e.getMessage());
        } catch (IOException e) {
            Logger.d(e.getMessage());
        } catch (URISyntaxException e) {
            Logger.d(e.getMessage());
        } finally {
            Logger.d("シャットダウン前");
            httpClient.getConnectionManager().shutdown();
            Logger.d("シャットダウン後");
        }
        retMap.put("marketValueCashGain", gain);
        retMap.put("marketValueCashLoss", loss);

        retMap.put("marketValueMarginGain", marginGain);
        retMap.put("marketValueMarginLoss", marginLoss);

        return retMap;
    }

    // このメソッドは非同期処理の終わった後に呼び出されます
    @Override
    protected void onPostExecute(Map<String, Integer> retMap) {

        if (retMap == null) {
            return;
        }

        // 【現金】買付余力
        int buyingPowerCash = retMap.get("buyingPowerCash");
        this.mRemoteViews.setTextViewText(R.id.buyingPowerCash,
                this.mContext.getString(R.string.format_item_buyingPowerCash, buyingPowerCash));

        // 【現物】評価額合計
        int marketValueCash = retMap.get("marketValueCash");
        this.mRemoteViews.setTextViewText(R.id.marketValueCash,
                this.mContext.getString(R.string.format_item_marketValueCash, marketValueCash));

        // 【現物】評価額合計
        int marketValueCashGainLoss = retMap.get("marketValueCashGainLoss");
        if(marketValueCashGainLoss > 0){
            // ＋赤
            this.mRemoteViews.setTextColor(R.id.marketValueCashGainLoss, Color.parseColor("#ffff0000"));
        }else if(marketValueCashGainLoss < 0){
            // －青
            this.mRemoteViews.setTextColor(R.id.marketValueCashGainLoss, Color.parseColor("#ff0000ff"));
        }else{
            // ０黒
            this.mRemoteViews.setTextColor(R.id.marketValueCashGainLoss, Color.parseColor("#ff000000"));
        }
        this.mRemoteViews.setTextViewText(R.id.marketValueCashGainLoss,
                this.mContext.getString(R.string.format_item_marketValueCashGainLoss, marketValueCashGainLoss));

        // 【現物】評価額合計
        int marketValueCashGain = retMap.get("marketValueCashGain");
        if(marketValueCashGain > 0){
            // ＋赤
            this.mRemoteViews.setTextColor(R.id.marketValueCashGain, Color.parseColor("#99ff0000"));
        }else if(marketValueCashGain < 0){
            // －青
            this.mRemoteViews.setTextColor(R.id.marketValueCashGain, Color.parseColor("#990000ff"));
        }else{
            // ０黒
            this.mRemoteViews.setTextColor(R.id.marketValueCashGain, Color.parseColor("#99000000"));
        }
        this.mRemoteViews.setTextViewText(R.id.marketValueCashGain,
                this.mContext.getString(R.string.format_item_marketValueCashGain, marketValueCashGain));

        // 【現物】評価額合計
        int marketValueCashLoss = retMap.get("marketValueCashLoss");
        if(marketValueCashLoss > 0){
            // ＋赤
            this.mRemoteViews.setTextColor(R.id.marketValueCashLoss, Color.parseColor("#99ff0000"));
        }else if(marketValueCashLoss < 0){
            // －青
            this.mRemoteViews.setTextColor(R.id.marketValueCashLoss, Color.parseColor("#990000ff"));
        }else{
            // ０黒
            this.mRemoteViews.setTextColor(R.id.marketValueCashLoss, Color.parseColor("#99000000"));
        }
        this.mRemoteViews.setTextViewText(R.id.marketValueCashLoss,
                this.mContext.getString(R.string.format_item_marketValueCashLoss, marketValueCashLoss));

        // 【信用】建代金合計
        int marketValueMargin = retMap.get("marketValueMargin");
        this.mRemoteViews.setTextViewText(R.id.marketValueMargin,
                this.mContext.getString(R.string.format_item_marketValueMargin, marketValueMargin));

        // 【信用】建玉評価損益額
        int marketValueMarginGainLoss = retMap.get("marketValueMarginGainLoss");
        if(marketValueMarginGainLoss > 0){
            // ＋赤
            this.mRemoteViews.setTextColor(R.id.marketValueMarginGainLoss, Color.parseColor("#ffff0000"));
        }else if(marketValueMarginGainLoss < 0){
            // －青
            this.mRemoteViews.setTextColor(R.id.marketValueMarginGainLoss, Color.parseColor("#ff0000ff"));
        }else{
            // ０黒
            this.mRemoteViews.setTextColor(R.id.marketValueMarginGainLoss, Color.parseColor("#ff000000"));
        }
        this.mRemoteViews.setTextViewText(R.id.marketValueMarginGainLoss,
                this.mContext.getString(R.string.format_item_marketValueMarginGainLoss, marketValueMarginGainLoss));

        // 【信用】建玉評価益
        int marketValueMarginGain = retMap.get("marketValueMarginGain");
        if(marketValueMarginGain > 0){
            // ＋赤
            this.mRemoteViews.setTextColor(R.id.marketValueMarginGain, Color.parseColor("#99ff0000"));
        }else if(marketValueMarginGain < 0){
            // －青
            this.mRemoteViews.setTextColor(R.id.marketValueMarginGain, Color.parseColor("#990000ff"));
        }else{
            // ０黒
            this.mRemoteViews.setTextColor(R.id.marketValueMarginGain, Color.parseColor("#99000000"));
        }
        this.mRemoteViews.setTextViewText(R.id.marketValueMarginGain,
                this.mContext.getString(R.string.format_item_marketValueMarginGain, marketValueMarginGain));

        // 【信用】建玉評価損
        int marketValueMarginLoss = retMap.get("marketValueMarginLoss");
        if(marketValueMarginLoss > 0){
            // ＋赤
            this.mRemoteViews.setTextColor(R.id.marketValueMarginLoss, Color.parseColor("#99ff0000"));
        }else if(marketValueMarginLoss < 0){
            // －青
            this.mRemoteViews.setTextColor(R.id.marketValueMarginLoss, Color.parseColor("#990000ff"));
        }else{
            // ０黒
            this.mRemoteViews.setTextColor(R.id.marketValueMarginLoss, Color.parseColor("#99000000"));
        }
        this.mRemoteViews.setTextViewText(R.id.marketValueMarginLoss,
                this.mContext.getString(R.string.format_item_marketValueMarginLoss, marketValueMarginLoss));

        // 【信用】実質保証金
        int marketValueDeposit = retMap.get("marketValueDeposit");
        this.mRemoteViews.setTextViewText(R.id.marketValueDeposit,
                this.mContext.getString(R.string.format_item_marketValueDeposit, marketValueDeposit));

        // 【信用】建代金危険水準
        int calcMarginRiskLevel = (int)(marketValueDeposit / 0.40);
        this.mRemoteViews.setTextViewText(R.id.calcMarginRiskLevel,
                this.mContext.getString(R.string.format_item_calcMarginRiskLevel, calcMarginRiskLevel));

        // 【信用】算出建余力
        int calcBuyingPowerMargin = calcMarginRiskLevel - marketValueMargin;
        if(calcBuyingPowerMargin > 0){
            // ＋赤
            this.mRemoteViews.setTextColor(R.id.calcBuyingPowerMargin, Color.parseColor("#ffff0000"));
        }else if(calcBuyingPowerMargin < 0){
            // －青
            this.mRemoteViews.setTextColor(R.id.calcBuyingPowerMargin, Color.parseColor("#ff0000ff"));
        }else{
            // ０黒
            this.mRemoteViews.setTextColor(R.id.calcBuyingPowerMargin, Color.parseColor("#ff000000"));
        }
        this.mRemoteViews.setTextViewText(R.id.calcBuyingPowerMargin,
                this.mContext.getString(R.string.format_item_calcBuyingPowerMargin, calcBuyingPowerMargin));


        // 【算出資産】益合計
        int totalGain = marketValueCashGain + marketValueMarginGain;
        if(totalGain > 0){
            // ＋赤
            this.mRemoteViews.setTextColor(R.id.totalGain, Color.parseColor("#99ff0000"));
        }else if(totalGain < 0){
            // －青
            this.mRemoteViews.setTextColor(R.id.totalGain, Color.parseColor("#990000ff"));
        }else{
            // ０黒
            this.mRemoteViews.setTextColor(R.id.totalGain, Color.parseColor("#99000000"));
        }
        this.mRemoteViews.setTextViewText(R.id.totalGain,
                this.mContext.getString(R.string.format_item_totalGain, totalGain));

        // 【算出資産】損合計
        int totalLoss = marketValueCashLoss + marketValueMarginLoss;
        if(totalLoss > 0){
            // ＋赤
            this.mRemoteViews.setTextColor(R.id.totalLoss, Color.parseColor("#99ff0000"));
        }else if(totalLoss < 0){
            // －青
            this.mRemoteViews.setTextColor(R.id.totalLoss, Color.parseColor("#990000ff"));
        }else{
            // ０黒
            this.mRemoteViews.setTextColor(R.id.totalLoss, Color.parseColor("#99000000"));
        }
        this.mRemoteViews.setTextViewText(R.id.totalLoss,
                this.mContext.getString(R.string.format_item_totalLoss, totalLoss));

        // 【算出資産】損益合計
        int totalGainLoss = marketValueCashGainLoss + marketValueMarginGainLoss;
        if(totalGainLoss > 0){
            // ＋赤
            this.mRemoteViews.setTextColor(R.id.totalGainLoss, Color.parseColor("#ffff0000"));
        }else if(totalGainLoss < 0){
            // －青
            this.mRemoteViews.setTextColor(R.id.totalGainLoss, Color.parseColor("#ff0000ff"));
        }else{
            // ０黒
            this.mRemoteViews.setTextColor(R.id.totalGainLoss, Color.parseColor("#ff000000"));
        }
        this.mRemoteViews.setTextViewText(R.id.totalGainLoss,
                this.mContext.getString(R.string.format_item_totalGainLoss, totalGainLoss));

        // 【算出資産】計
        int totalAccountValue = buyingPowerCash + marketValueCash + marketValueMarginGainLoss;
        this.mRemoteViews.setTextViewText(R.id.totalAccountValue,
                this.mContext.getString(R.string.format_item_totalAccountValue, totalAccountValue));

        // 合計金額を前日の値としてプリファレンスに保持する。
        Date now = new Date();
        SharedPreferences pref = this.mContext.getSharedPreferences(Constant.PREF_KEY, Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        // 合計金額をプリファレンスに登録
        editor.putInt(Constant.getPrefDateKey(now, "_JP"), totalAccountValue);
        editor.commit();

        // 前日の合計金額を取得する
        int prevTotalAccountValue = pref.getInt(Constant.getPrefDateKey(DateUtil.addDate(now, -1), "_JP"), 0);
        int totalPrevComparison = totalAccountValue - prevTotalAccountValue;
        if(totalPrevComparison > 0){
            // ＋赤
            this.mRemoteViews.setTextColor(R.id.totalPrevComparison, Color.parseColor("#ffff0000"));
        }else if(totalPrevComparison < 0){
            // －青
            this.mRemoteViews.setTextColor(R.id.totalPrevComparison, Color.parseColor("#ff0000ff"));
        }else{
            // ０黒
            this.mRemoteViews.setTextColor(R.id.totalPrevComparison, Color.parseColor("#ff000000"));
        }
        this.mRemoteViews.setTextViewText(R.id.totalPrevComparison,
                this.mContext.getString(R.string.format_item_totalPrevComparison, totalPrevComparison));

        String key = Constant.PREF_KEY_TOTAL_ACCOUNT_VALUE_JP;
        int tmpTotalAccountValue = pref.getInt(key, 0);
        // 前回取得した値と変わらないブログ投稿は行わない
        Logger.d("カレンダー更新処理前：totalAccountValue=" + totalAccountValue + ", tmpTotalAccountValue=" + tmpTotalAccountValue);
        if((totalAccountValue - tmpTotalAccountValue) != 0){
            editor.putInt(key, totalAccountValue);
            editor.commit();

            // TODO パーミッションチェック：googleカレンダー更新
            GCalendar calendar = new GCalendar(this.mContext);
            StringBuffer title = new StringBuffer("【国内】")
                .append("計：").append(String.format("%1$,3d", totalAccountValue))
                .append("(").append(String.format("%1$,3d", (totalAccountValue-prevTotalAccountValue))).append(")");
            calendar.insertEvent(title.toString(), now);
        }

        // ウィジェットにremoteViewsをセット
        ComponentName cn = new ComponentName(this.mContext, SummaryWidgetProvider.class);
        this.mAppWidgetManager.updateAppWidget(cn, this.mRemoteViews);
    }

    /**
     * 起動時間のチェック.
     * <p>→ 08:00 ～ 16:00
     *
     * @return バックグランド実行可否結果を返す。
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public static boolean _validDoInBackground() {
        Date now = new Date();
        Date from = DateUtil.setHourOfDay(now, 8);
        Date to = DateUtil.setHourOfDay(now, 16);

        if (from.getTime() <= now.getTime() && now.getTime() <= to.getTime()) {
            return true;
        }
        return false;
    }
}