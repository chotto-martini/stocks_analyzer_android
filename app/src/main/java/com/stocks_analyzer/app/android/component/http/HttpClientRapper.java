package com.stocks_analyzer.app.android.component.http;

import com.stocks_analyzer.app.android.common.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * HTTPクライアントクラス.
 *
 * @deprecated 非推奨クラスを参照しているため。
 *
 * @author chotto-martini
 * @since 1.0.0 2018/04/24
 */
public class HttpClientRapper {

    /** HttpClient. */
    private DefaultHttpClient mHttpClient = null;

    /**
     * デフォルトコンストラクタ.
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public HttpClientRapper() {
        super();
        mHttpClient = new DefaultHttpClient();
    }

    /**
     * HttpClient#execute(HttpPost)のラップ.
     *
     * @param httpPost HttpPostオブジェクト
     * @return リクエスト結果を返却する。
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public String execute(HttpPost httpPost) {
        String ret = "";
        try {
            ret = mHttpClient.execute(httpPost, new ResponseHandler<String>(){
                @Override
                public String handleResponse(HttpResponse response)
                        throws ClientProtocolException, IOException {

                    Logger.d("レスポンスコード:" + response.getStatusLine().getStatusCode());

                    switch (response.getStatusLine().getStatusCode()) {
                        case HttpStatus.SC_OK:
                        case HttpStatus.SC_MOVED_PERMANENTLY:
                        case HttpStatus.SC_MOVED_TEMPORARILY:
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
        } catch (ClientProtocolException e) {
            Logger.w(e.getMessage(), e);
        } catch (IOException e) {
            Logger.w(e.getMessage(), e);
        }
        return ret;
    }

    /**
     * HttpClient#execute(HttpPost)のラップ.
     *
     * @param httpGet HttpGetオブジェクト
     * @return リクエスト結果を返却する。
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public String execute(HttpGet httpGet) {
        String ret = "";
        try {
            ret = mHttpClient.execute(httpGet, new ResponseHandler<String>(){
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
        } catch (ClientProtocolException e) {
            Logger.w(e.getMessage(), e);
        } catch (IOException e) {
            Logger.w(e.getMessage(), e);
        } catch (Exception e) {
            Logger.w(e.getMessage(), e);
        }
        return ret;
    }

    /**
     * UserAgentを設定する.
     *
     * @author chotto-martini
     * @since 1.0.0 2018/04/24
     */
    public void setUserAgent(){
        this.mHttpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_0 like Mac OS X; en-us) AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0 Mobile/7A341 Safari/528.16");
    }
}
