package com.virorg.okhttpwrapper;


import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

/**
 * Implementation of the OkHttp Stack.
 *
 * @author Vinit Saxena
 */
public class OkHttp {

    private OkHttpClient mClient;
    private static OkHttp okHttp;
    private OkHttp() {
        mClient = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .cache(null)
                .build();
    }

    public OkHttp(OkHttpClient client) {
        mClient = client;
    }

    public OkHttpClient getClient() {
        return mClient;
    }

    public static OkHttp getInstance() {

        if(okHttp!=null){
            return okHttp;
        }else {
            return new OkHttp();
        }
    }

    public static class ContentType{
        public static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        public static MediaType FORM_URLENCODED = MediaType.parse("application/x-www-form-urlencoded");
    }
}
