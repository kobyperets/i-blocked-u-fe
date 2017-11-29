package com.salesforce.iblockedu;

import android.util.Log;

//import com.loopj.android.http.AsyncHttpClient;
//import com.loopj.android.http.AsyncHttpResponseHandler;
//import com.loopj.android.http.RequestParams;

/**
 * Created by kperets on 29/11/2017.
 */

public class HttpUtils {
    public static final String BASE_URL = "https://i-blocked-u.herokuapp.com/iblockedu/api/";
    public static final String SIGN_IN_ENDPOINT = "signin";
    public static final String BLOCK_ENDPOINT = "iAmBlocking";
    public static final String WHO_BLOCKS_ENDPOINT = "whoBlocks";
    public static final String GOING_HOME_ENDPOINT = "goingHome";

    public static final String EMAIL_PARAM = "email";
    public static final String LICENSE_PLATE_PARAM = "licensePlate";


    public static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public static String getRequestUrl(String relativeEndpoint, String emailValue, String licensePlateValue) {
        String url = getAbsoluteUrl(relativeEndpoint);
        String params = EMAIL_PARAM + "=" + emailValue;
        if(!licensePlateValue.isEmpty()) { params += "&" + LICENSE_PLATE_PARAM + "=" + licensePlateValue;}
        String urlRequest = url + "?" + params;
        Log.d("HttpUtils", "Formed the url request: " + urlRequest);
        return urlRequest;
    }



    //    private static AsyncHttpClient client = new AsyncHttpClient();

//    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
//        client.get(getAbsoluteUrl(url), params, responseHandler);
//    }
//
//    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
//        client.post(getAbsoluteUrl(url), params, responseHandler);
//    }
//
//    public static void getByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
//        client.get(url, params, responseHandler);
//    }
//
//    public static void postByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
//        client.post(url, params, responseHandler);
//    }

}
