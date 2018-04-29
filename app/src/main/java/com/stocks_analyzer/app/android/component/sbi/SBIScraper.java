package com.stocks_analyzer.app.android.component.sbi;

import com.stocks_analyzer.app.android.common.Logger;
import com.stocks_analyzer.app.android.component.http.HttpClientRapper;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


/**
 * SBIサイトをスクレーピングするクラス.
 *
 * @author chotto-martini
 * @since 1.0.0 2018/04/24
 */
public class SBIScraper {

    /** ログインURL */
    private static final String _LOGIN_URL = "https://site1.sbisec.co.jp/ETGate/";
    /** 口座情報（国内） URL */
    private static final String _ACCOUNT_SUMMARY_JP_URL = "https://site1.sbisec.co.jp/ETGate/?_ControlID=WPLETacR001Control&_PageID=DefaultPID&_DataStoreID=DSWPLETacR001Control&_ActionID=DefaultAID&getFlg=on";
    /** 口座情報（国内/現物） URL */
    private static final String _MARKET_VALUE_JP_URL = "https://site1.sbisec.co.jp/ETGate/?_ControlID=WPLETacR002Control&_PageID=DefaultPID&_DataStoreID=DSWPLETacR002Control&_ActionID=DefaultAID&getFlg=on";
    /** 口座情報（国内/信用） URL */
    private static final String _MARKET_VALUE_MARGIN_JP_URL = "https://site1.sbisec.co.jp/ETGate/?_ControlID=WPLETacR005Control&_PageID=DefaultPID&_DataStoreID=DSWPLETacR005Control&_ActionID=DefaultAID&getFlg=on";

    /** HttpClient */
    private HttpClientRapper _mHttpClient = null;
    /** アカウント情報 */
    private String _account = null;
    /** パスワード */
    private String _password = null;

    /**
     * 例外クラス.
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public class SBIScraperException extends Exception {
        private static final long serialVersionUID = 1L;
    }

    /**
     * 国内口座情報.
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public class AccountSummaryJP {
        /** 【現金】買付余力 */
        public int buyingPowerCash = 0;
        /** 【現物】評価額合計 */
        public int marketValueCash = 0;
        /** 【現物】評価額合計 */
        public int marketValueCashGainLoss = 0;

        /** 【信用】建代金合計 */
        public int marketValueMargin = 0;
        /** 【信用】建玉評価損益額 */
        public int marketValueMarginGainLoss = 0;
        /** 【信用】建玉評価益 */
        public int marketValueMarginGain = 0;
        /** 【信用】建玉評価損 */
        public int marketValueMarginLoss = 0;
        /** 【信用】実質保証金 */
        public int marketValueDeposit = 0;

        /** 【信用】建代金危険水準 */
        public int calcMarginRiskLevel = (int)(this.marketValueDeposit / 0.40);
        /** 【信用】算出建余力 */
        public int calcBuyingPowerMargin = this.calcMarginRiskLevel - this.marketValueMargin;

        /** 【算出資産】損益合計 */
        public int totalGainLoss = this.marketValueCashGainLoss + this.marketValueMarginGainLoss;
        /** 【算出資産】計 */
        public int totalAccountValue = this.buyingPowerCash + this.marketValueCash + this.marketValueMarginGainLoss;
    }

    /**
     * 銘柄情報.
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public class TickerInfo {
        /** 銘柄名 */
        public String name = "";
        /** 銘柄コード */
        public String code = "";
        /** 損益額 */
        public int gainLoss = 0;
    }

    /**
     * デフォルトコンストラクタ.
     *
     * @param account アカウント
     * @param password パスワード
     * @throws SBIScraperException
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public SBIScraper(String account, String password) throws SBIScraperException {
        this._account = account;
        this._password = password;
        this._mHttpClient = new HttpClientRapper();
        if (!_login()) {
            // 失敗してるので初期化
            this._account = null;
            this._password = null;
            this._mHttpClient = null;
            throw new SBIScraperException();
        }
    }

    /**
     * ログイン処理.
     *
     * @return ログイン結果を返す。
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    private boolean _login() {
        URI url = null;
        try {
            url = new URI(_LOGIN_URL);
        } catch (URISyntaxException e) {
            Logger.w("URL作成に失敗しました。", e);
            return false;
        }

        HttpPost httpPost = new HttpPost(url);
        String html = _mHttpClient.execute(httpPost);

        if(html == null || html.length() < 0){
            Logger.w("ログイン画面取得に失敗しました。");
            return false;
        }

        // POSTパラメータ初期化
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        Document document = Jsoup.parse(html);
        // ログイン情報生成の為、form取得
        Elements elemForms = document.select("form");
        if (elemForms != null && elemForms.size() > 0) {
            int formsSize = elemForms.size();
            for (int i = 0; i < formsSize; i++) {
                Element formElement = elemForms.get(i);
                // ログインform取得
                Elements formElements = formElement.getElementsByAttributeValue("name", "form_login");
                if (formElements != null && formElements.size() > 0) {
                    // input取得
                    Elements elemInputs = formElements.select("input");
                    if (elemInputs != null) {
                        int inputsSize = elemInputs.size();
                        for (int j = 0; j < inputsSize; j++) {
                            Element inputElement = elemInputs.get(j);
                            // name属性取得
                            String name = inputElement.attr("name");

                            // POSTパラメータ生成
                            if ("user_id".equals(name)) {
                                postParams.add(new BasicNameValuePair(name, this._account));
                            } else if ("user_password".equals(name)) {
                                postParams.add(new  BasicNameValuePair(name, this._password));

                                // hidden
                            } else if ("JS_FLG".equals(name)) {
                                postParams.add(new  BasicNameValuePair(name, "1"));
                            } else if ("BW_FLG".equals(name)) {
                                postParams.add(new  BasicNameValuePair(name, "firefox,NaN"));
                            } else {
                                postParams.add(new  BasicNameValuePair(name, inputElement.val()));
                            }
                        }
                    } else {
                        Logger.d("ログイン情報生成用のinputが取得できませんでした。");
                        return false;
                    }
                }
            }

            if (postParams.size() <= 0) {
                Logger.d("ログイン情報生成用のformが取得できませんでした。");
                return false;
            }
        } else {
            Logger.d("formが存在しません。");
            return false;
        }

        // ログイン処理
        httpPost = new HttpPost(url);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(postParams, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }

        html = _mHttpClient.execute(httpPost);
        if(html == null || html.length() < 0 || (html.indexOf("ログアウト") < 0)){
            Logger.d("ログインに失敗しました。");
            return false;
        }

        // ログイン成功
        return true;
    }

    /**
     * 国内口座情報を取得する.
     *
     * @return 国内口座情報を返却する。
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public AccountSummaryJP getAccountSummaryJP() {
        URI url = null;
        try {
            url = new URI(_ACCOUNT_SUMMARY_JP_URL);
        } catch (URISyntaxException e) {
            Logger.w("URL作成に失敗しました。", e);
            return null;
        }
        HttpGet httpGet = new HttpGet(url);
        String html = _mHttpClient.execute(httpGet);

        if(html == null || html.length() < 0){
            Logger.w("国内口座情報取得に失敗しました。");
            return null;
        }

        AccountSummaryJP accountSummary = new AccountSummaryJP();
        Document document = Jsoup.parse(html);
        // レフトエリア取得
        Elements elemTables = document.select("html body div table tbody tr td table tbody tr:eq(1) td table tbody tr td form table tr td table tr td table");
        if (elemTables != null) {
            int tablesSize = elemTables.size();
            for (int i = 0; i < tablesSize; i++) {
                Element tableElement = elemTables.get(i);
                String tableHtml = tableElement.toString();

                // 信用建余力
                if (tableHtml.indexOf("信用建余力") > -1) {
                    Elements elemTds = tableElement.select("td");
                    Logger.d(elemTds.toString());
                    for (int j = 0; j < elemTds.size(); j++) {
                        Element element = elemTds.get(j);
                        String tdHtml = element.toString();

                        if (tdHtml.indexOf("現引可能額") > -1) {
                            int buyingPowerCash = 0;
                            try {
                                buyingPowerCash = Integer.parseInt(elemTds.get(j+1).text().toString().trim().replaceAll("(\\+|,)", ""));
                            } catch (Exception e) {
                                Logger.d(e.getMessage() + ":" + elemTds.get(j+1).text().toString().trim().replaceAll("(\\+|,)", ""));
                            }
                            accountSummary.buyingPowerCash = buyingPowerCash;
                        } else {
                            Logger.d("デバッグ:" + tdHtml.toString());
                        }
                    }

                    // 保有資産評価
                } else if (tableHtml.indexOf("保有資産評価") > -1) {
                    Elements elemTds = tableElement.select("td");
                    Logger.d(elemTds.toString());
                    for (int j = 0; j < elemTds.size(); j++) {
                        Element element = elemTds.get(j);
                        String tdHtml = element.toString();

                        if (tdHtml.indexOf("株式") > -1) {
                            int marketValueCash = 0;
                            try {
                                marketValueCash = Integer.parseInt(elemTds.get(j+1).text().toString().trim().replaceAll("(\\+|,)", ""));
                            } catch (Exception e) {
                                Logger.d(e.getMessage() + ":" + elemTds.get(j+1).text().toString().trim().replaceAll("(\\+|,)", ""));
                            }
                            accountSummary.marketValueCash = marketValueCash;
                        } else if (tdHtml.indexOf("") > -1) {



                        } else {
                            Logger.d("デバッグ:" + tdHtml.toString());
                        }
                    }
                }
            }
        }
        return accountSummary;
    }

    /**
     * 現物銘柄情報の一覧を取得する.
     *
     * @return 現物銘柄情報の一覧を返却する。
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public List<TickerInfo> getMarketValueTickerInfoList() {
        List<TickerInfo> tickerInfoList = new ArrayList<TickerInfo>();

        URI url = null;
        try {
            url = new URI(_MARKET_VALUE_JP_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Logger.d("URL作成に失敗しました。");
            return null;
        }
        HttpGet httpGet = new HttpGet(url);
        String html = _mHttpClient.execute(httpGet);

        if(html == null || html.length() < 0){
            Logger.d("国内口座情報取得に失敗しました。");
            return null;
        }

        Document document = Jsoup.parse(html);
        // レフトエリア取得
        Elements elemTables = document.select("html body div table tbody tr td:eq(0) table tbody tr:eq(1) td form table tbody tr:eq(0) td:eq(1) table  tbody tr td table");
        if (elemTables != null) {
            int tablesSize = elemTables.size();
            for (int i = 0; i < tablesSize; i++) {
                Element tableElement = elemTables.get(i);
                String tableHtml = tableElement.toString();

                if (tableHtml.indexOf("銘柄") > -1) {
                    Elements elemTrs = tableElement.select("tr");
                    for (int j = 1; j < elemTrs.size(); j++) {
                        Element trElement = elemTrs.get(j);
                        String trHtml = trElement.toString();

                        Elements elemTds = trElement.select("td");
                        if (elemTds != null && elemTds.size() >= 7) {
                            TickerInfo tickerInfo = new TickerInfo();

                            // 銘柄名・コード取得
                            String[] ticker = elemTds.get(0).text().split(" ");
                            tickerInfo.name = ticker[0].trim();
                            tickerInfo.code = ticker[1].replaceAll("[^0-9]", "");
                            // 損益額取得
                            int gainLoss = 0;
                            try {
                                gainLoss = (int)Double.parseDouble(elemTds.get(4).text().toString().trim().replaceAll("(\\+|,)", ""));
                            } catch (Exception e) {}
                            tickerInfo.gainLoss = gainLoss;
                            tickerInfoList.add(tickerInfo);
                        }
                    }
                }
            }
        }

        return tickerInfoList;
    }

    /**
     * 信用銘柄情報の一覧を取得する.
     *
     * @return 信用銘柄情報の一覧を返却する。
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public List<TickerInfo> getMarketValueMarginTickerInfoList() {
        List<TickerInfo> tickerInfoList = new ArrayList<TickerInfo>();

        URI url = null;
        try {
            url = new URI(_MARKET_VALUE_MARGIN_JP_URL);
        } catch (URISyntaxException e) {
            Logger.w("URL作成に失敗しました。", e);
            return null;
        }
        HttpGet httpGet = new HttpGet(url);
        String html = _mHttpClient.execute(httpGet);

        if(html == null || html.length() < 0){
            Logger.w("国内口座情報取得に失敗しました。");
            return null;
        }

        Document document = Jsoup.parse(html);
        // レフトエリア取得
        Elements elemTables = document.select("html body div table tbody tr td:eq(0) table tbody tr:eq(1) td form table tbody tr:eq(0) td:eq(1) table  tbody tr td table");
        if (elemTables != null) {
            int tablesSize = elemTables.size();
            for (int i = 0; i < tablesSize; i++) {
                Element tableElement = elemTables.get(i);
                String tableHtml = tableElement.toString();

                if (tableHtml.indexOf("銘柄") > -1) {
                    Elements elemTrs = tableElement.select("tr");
                    for (int j = 1; j < elemTrs.size(); j++) {
                        Element trElement = elemTrs.get(j);
                        String trHtml = trElement.toString();

                        Elements elemTds = trElement.select("td");
                        if (elemTds != null && elemTds.size() >= 10) {
                            TickerInfo tickerInfo = new TickerInfo();

                            // 銘柄名・コード取得
                            String[] ticker = elemTds.get(0).text().split(" ");
                            tickerInfo.name = ticker[0].trim();
                            tickerInfo.code = ticker[1].replaceAll("[^0-9]", "");
                            // 損益額取得
                            int gainLoss = 0;
                            try {
                                gainLoss = (int)Double.parseDouble(elemTds.get(8).text().toString().trim().replaceAll("(\\+|,)", ""));
                            } catch (Exception e) {}
                            tickerInfo.gainLoss = gainLoss;
                            tickerInfoList.add(tickerInfo);
                        }
                    }
                }
            }
        }

        return tickerInfoList;
    }
}
