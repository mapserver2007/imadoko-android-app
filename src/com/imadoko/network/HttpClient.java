package com.imadoko.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.net.Uri;
import android.util.Log;

import com.imadoko.util.AppConstants;

/**
 * HttpClient
 * <pre>
 * http://terurou.hateblo.jp/entry/20110702/1309541200
 * </pre>
 * @author Ryuichi Tanaka
 * @since 2014/09/06
 */
public class HttpClient {
    /** ステータスコード */
    private int _statusCode;
    /** レスポンス */
    private String _responseBody;

    /**
     * GETを実行する
     * @param url URL
     * @return HttpClientオブジェクト
     */
    public HttpClient get(String url) {
        Uri.Builder builder = Uri.parse(url).buildUpon();
        HttpGet httpGet = new HttpGet(builder.build().toString());
        request(httpGet);
        return this;
    }

    /**
     * GETを実行する
     * @param url URL
     * @param params パラメータ
     * @return HttpClientオブジェクト
     */
    public HttpClient get(String url, Map<String, String> params) {
        Uri.Builder builder = Uri.parse(url).buildUpon();
        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                builder.appendQueryParameter(param.getKey(), param.getValue());
            }
        }

        HttpGet httpGet = new HttpGet(builder.build().toString());
        request(httpGet);

        return this;
    }

    /**
     * POSTを実行する
     * @param url URL
     * @param params パラメータ
     * @return HttpClientオブジェクト
     */
    public HttpClient post(String url, Map<String, String> params) {
        HttpPost httpPost = new HttpPost(url);

        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                postParams.add(new BasicNameValuePair(param.getKey(), param.getValue()));
            }
        }

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(postParams));
            request(httpPost);
        } catch (UnsupportedEncodingException e) {
            Log.e(AppConstants.TAG_HTTP, e.getMessage());
        }

        return this;
    }

    /**
     * ステータスコードを返却する
     * @return ステータスコード
     */
    public int getStatusCode() {
        return _statusCode;
    }

    /**
     * レスポンスを返却する
     * @return レスポンス
     */
    public String getResponseBody() {
        return _responseBody;
    }

    /**
     * レスポンスを返却する
     * @param request リクエストオブジェクト
     */
    private void request(HttpRequestBase request) {
        DefaultHttpClient httpClient = null;
        String errorMessage = null;
        try {
            SSLSocketFactory socketFactory;
            if (AppConstants.ENV_DEV) {
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);
                socketFactory = new CustomSSLSocketFactory(trustStore);
                socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            } else {
                socketFactory = SSLSocketFactory.getSocketFactory();
                socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            }

            // HTTPスキーマ設定
            SchemeRegistry scheme = new SchemeRegistry();
            scheme.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            scheme.register(new Scheme("https", socketFactory, 443));

            // タイムアウト設定
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setSocketBufferSize(httpParams, 4096);
            HttpConnectionParams.setSoTimeout(httpParams, 5000);
            HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
            HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);
            HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);

            ThreadSafeClientConnManager connManager = new ThreadSafeClientConnManager(httpParams, scheme);
            httpClient = new DefaultHttpClient(connManager, httpParams);
            _responseBody = httpClient.execute(request, createResponse());
        } catch (IllegalStateException e) { // Target host must not be null, or set in parameters
            _statusCode = HttpStatus.SC_NOT_FOUND;
            errorMessage = e.getMessage();
        } catch (ClientProtocolException e) {
            _statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
            errorMessage = e.getMessage();
        } catch (IOException e) {
            _statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
            errorMessage = e.getMessage();
        } catch (Throwable e) { // ステータスコードが200以外
            errorMessage = e.getMessage();
        } finally {
            if (httpClient != null) {
                httpClient.getConnectionManager().shutdown();
            }
            if (errorMessage != null) {
                // 稀にLogオブジェクトの取得に失敗する現象を確認したのでエラー処理を追加する
                try {
                    Log.d(AppConstants.TAG_HTTP, errorMessage);
                } catch (RuntimeException ignore) {}
            }
        }
    }

    /**
     * ResponseHandlerオブジェクトを返却する
     * @return ResponseHandlerオブジェクト
     */
    private ResponseHandler<String> createResponse() {
        return new ResponseHandler<String>() {
            @Override
            public String handleResponse(HttpResponse response)
                    throws ClientProtocolException, IOException {
                int statusCode = response.getStatusLine().getStatusCode();
                _statusCode = statusCode;
                switch (statusCode) {
                case HttpStatus.SC_OK:
                    return EntityUtils.toString(response.getEntity(), "UTF-8");
                default:
                    throw new RuntimeException("cause error.");
                }
            }
        };
    }
}
