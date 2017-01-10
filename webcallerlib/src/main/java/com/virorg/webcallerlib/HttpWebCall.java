package com.virorg.webcallerlib;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Vinit on 15/12/16.
 */

public class HttpWebCall extends HttpRequest {

    private OkHttp okHttp;
    private static HttpWebCall httpWebCall;
    private Context mContext;
    private MediaType defaultMediaType = OkHttp.ContentType.JSON;
    private boolean isAsynchronous = true;
    private Map<String, RequestBundle> request;

    private HttpWebCall(Context context) {
        mContext = context;
        okHttp = OkHttp.getInstance();
        request = new HashMap<String, RequestBundle>();
    }

    public static HttpWebCall getInstance(Context context) {

        if (httpWebCall == null) {
            httpWebCall = new HttpWebCall(context);
            return httpWebCall;
        } else {
            return httpWebCall;
        }
    }


    public void setMediaType(MediaType mediaType) {
        defaultMediaType = mediaType;
    }

    public void setCallType(boolean isAsynchronous) {
        this.isAsynchronous = isAsynchronous;
    }

    public <T> void postRequest(String url
            , String authorization
            , List<RequestParams> requestParams
            , String tag
            , Class<T> responseClass
            , ApiCallBack<T> callback) {

        RequestBody requestBody = null;
        if (defaultMediaType.equals(OkHttp.ContentType.JSON)) {
            try {
                JSONObject json = new JSONObject();
                for (RequestParams requestParam : requestParams) {

                    String stringValue = "";
                    int intValue = 0;
                    float floatValue = 0.0f;
                    long longValue = 0;
                    double doubleValue = 0.0;
                    boolean booleanValue = false;

                    if (requestParam.getValue() instanceof String) {
                        stringValue = (String) requestParam.getValue();
                        if (stringValue.contains("[")) {
                            json.put(requestParam.getKey(), new JSONArray(stringValue));
                        } else {
                            json.put(requestParam.getKey(), stringValue);
                        }
                    } else if (requestParam.getValue() instanceof Integer) {
                        intValue = requestParam.getIntValue();
                        json.put(requestParam.getKey(), intValue);

                    } else if (requestParam.getValue() instanceof Long) {
                        longValue = requestParam.getLongValue();
                        json.put(requestParam.getKey(), longValue);
                    } else if (requestParam.getValue() instanceof Float) {
                        floatValue = requestParam.getFloatValue();
                        json.put(requestParam.getKey(), floatValue);
                    } else if (requestParam.getValue() instanceof Double) {
                        doubleValue = requestParam.getDoubleValue();
                        json.put(requestParam.getKey(), doubleValue);
                    } else if (requestParam.getValue() instanceof Boolean) {
                        booleanValue = requestParam.getBooleanValue();
                        json.put(requestParam.getKey(), booleanValue);
                    }
                }
                requestBody = RequestBody.create(OkHttp.ContentType.JSON, json.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (defaultMediaType.equals(OkHttp.ContentType.FORM_URLENCODED)) {

            FormBody.Builder builder = new FormBody.Builder();
            for (RequestParams requestParam : requestParams) {
                if (((String) requestParam.getValue()).contains("[")) {
                    try {
                        JSONArray jsonArray = new JSONArray((String) requestParam.getValue());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            builder.add(requestParam.getKey() + "[]", jsonArray.get(i).toString());

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    builder.add(requestParam.getKey(), (String) requestParam.getValue());
//                    requestBody = new FormBody.Builder()
//                            .add(requestParam.getKey(), requestParam.getValue())
//                            .build();
                }

                requestBody = builder.build();

                /*requestBody = new FormBody.Builder()
                        .add(requestParam.getKey(), (String) requestParam.getValue())
                        .build();*/
            }
        }

        Request.Builder builder = new Request.Builder();
        if (!StringUtils.isBlank(authorization))
            builder.header("Authorization", authorization);
        builder.url(url);
        builder.tag(tag);
        builder.post(requestBody);
        Request request = builder.build();

        if (isAsynchronous) {
            doNetworkCallAsynchronous(okHttp.getClient(), request, responseClass, callback);
        } else {
            try {
                doNetworkCallSynchronous(okHttp.getClient(), request, responseClass, callback);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public <T> void postRequest(boolean isAsynchronous
            , OkHttpRequest<T> okHttpRequest) {

        RequestBody requestBody = null;
        if (defaultMediaType.equals(OkHttp.ContentType.JSON)) {
            try {
                JSONObject json = new JSONObject();
                for (RequestParams requestParam : okHttpRequest.getRequestParams()) {


                    String stringValue = "";
                    int intValue = 0;
                    float floatValue = 0.0f;
                    long longValue = 0;
                    double doubleValue = 0.0;
                    boolean booleanValue = false;

                    if (requestParam.getValue() instanceof String) {
                        stringValue = (String) requestParam.getValue();
                        if (stringValue.contains("[")) {
                            json.put(requestParam.getKey(), new JSONArray(stringValue));
                        } else {
                            json.put(requestParam.getKey(), stringValue);
                        }
                    } else if (requestParam.getValue() instanceof Integer) {
                        intValue = requestParam.getIntValue();
                        json.put(requestParam.getKey(), intValue);

                    } else if (requestParam.getValue() instanceof Long) {
                        longValue = requestParam.getLongValue();
                        json.put(requestParam.getKey(), longValue);
                    } else if (requestParam.getValue() instanceof Float) {
                        floatValue = requestParam.getFloatValue();
                        json.put(requestParam.getKey(), floatValue);
                    } else if (requestParam.getValue() instanceof Double) {
                        doubleValue = requestParam.getDoubleValue();
                        json.put(requestParam.getKey(), doubleValue);
                    } else if (requestParam.getValue() instanceof Boolean) {
                        booleanValue = requestParam.getBooleanValue();
                        json.put(requestParam.getKey(), booleanValue);
                    }

                   /* if (requestParam.getValue().contains("[")) {
                        json.put(requestParam.getKey(), new JSONArray(requestParam.getValue()));
                    } else {
                        json.put(requestParam.getKey(), requestParam.getValue());
                    }*/
                }
                requestBody = RequestBody.create(OkHttp.ContentType.JSON, json.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (defaultMediaType.equals(OkHttp.ContentType.FORM_URLENCODED)) {
            FormBody.Builder builder = new FormBody.Builder();
            for (RequestParams requestParam : okHttpRequest.getRequestParams()) {
                if (((String) requestParam.getValue()).contains("[")) {
                    try {
                        JSONArray jsonArray = new JSONArray((String) requestParam.getValue());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            builder.add(requestParam.getKey() + "[]", jsonArray.get(i).toString());

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    builder.add(requestParam.getKey(), (String) requestParam.getValue());
//                    requestBody = new FormBody.Builder()
//                            .add(requestParam.getKey(), requestParam.getValue())
//                            .build();
                }


            }
            requestBody = builder.build();
        }

        Request.Builder builder = new Request.Builder();
        if (!StringUtils.isBlank(okHttpRequest.getAuthorization()))
            builder.header("Authorization", okHttpRequest.getAuthorization());
        builder.url(okHttpRequest.getUrl());
        builder.tag(okHttpRequest.getTag());
        builder.post(requestBody);
        Request request = builder.build();
        addWebCall(okHttpRequest.getTag(), new RequestBundle(okHttpRequest, request));
        if (isAsynchronous) {
            doNetworkCallAsynchronous(okHttp.getClient(), request, okHttpRequest.getResponseClass(), okHttpRequest.getCallback());
        } else {
            try {
                doNetworkCallSynchronous(okHttp.getClient(), request, okHttpRequest.getResponseClass(), okHttpRequest.getCallback());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public <T> void getRequest(String url
            , String authorization
            , List<RequestParams> requestParams
            , String tag
            , Class<T> responseClass
            , ApiCallBack<T> callback) {


        StringBuilder completeUrl = new StringBuilder(url);
        if (requestParams != null && requestParams.size() > 0)
            completeUrl.append("?");
        String prefix = "";
        for (RequestParams requestParam : requestParams) {
            completeUrl.append(prefix);
            prefix = "&";
            completeUrl.append(requestParam.getKey() + "=" + requestParam.getValue());
        }

        Request.Builder builder = new Request.Builder();
        if (!StringUtils.isBlank(authorization))
            builder.header("Authorization", authorization);
        builder.url(url);
        builder.tag(tag);
        Request request = builder.build();

        if (isAsynchronous) {
            doNetworkCallAsynchronous(okHttp.getClient(), request, responseClass, callback);
        } else {
            try {
                doNetworkCallSynchronous(okHttp.getClient(), request, responseClass, callback);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public <T> void getRequest(boolean isAsynchronous
            , OkHttpRequest<T> okHttpRequest) {


        StringBuilder completeUrl = new StringBuilder(okHttpRequest.getUrl());
        completeUrl.append("?");
        for (RequestParams requestParam : okHttpRequest.getRequestParams()) {
            completeUrl.append(requestParam.getKey() + "=" + requestParam.getValue() + "&");
        }

        Request.Builder builder = new Request.Builder();
        if (!StringUtils.isBlank(okHttpRequest.getAuthorization()))
            builder.header("Authorization", okHttpRequest.getAuthorization());
        String url = completeUrl.toString();
        int lastIndex = url.lastIndexOf("&");
        if (lastIndex > 0) {
            url = url.substring(0, lastIndex);
        }
        builder.url(url);
        builder.tag(okHttpRequest.getTag());
        Request request = builder.build();
        addWebCall(okHttpRequest.getTag(), new RequestBundle(okHttpRequest, request));
        if (isAsynchronous) {
            doNetworkCallAsynchronous(okHttp.getClient(), request, okHttpRequest.getResponseClass(), okHttpRequest.getCallback());
        } else {
            try {
                doNetworkCallSynchronous(okHttp.getClient(), request, okHttpRequest.getResponseClass(), okHttpRequest.getCallback());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public <T> void deleteRequest(boolean isAsynchronous
            , OkHttpRequest<T> okHttpRequest) {


        StringBuilder completeUrl = new StringBuilder(okHttpRequest.getUrl());
        completeUrl.append("?");
        for (RequestParams requestParam : okHttpRequest.getRequestParams()) {
            completeUrl.append(requestParam.getKey() + "=" + requestParam.getValue() + "&");
        }

        Request.Builder builder = new Request.Builder();
        if (!StringUtils.isBlank(okHttpRequest.getAuthorization()))
            builder.header("Authorization", okHttpRequest.getAuthorization());
        String url = completeUrl.toString();
        int lastIndex = url.lastIndexOf("&");
        if (lastIndex > 0) {
            url = url.substring(0, lastIndex);
        }
        builder.url(url);
        builder.tag(okHttpRequest.getTag());
        builder.delete();
        Request request = builder.build();
        addWebCall(okHttpRequest.getTag(), new RequestBundle(okHttpRequest, request));
        if (isAsynchronous) {
            doNetworkCallAsynchronous(okHttp.getClient(), request, okHttpRequest.getResponseClass(), okHttpRequest.getCallback());
        } else {
            try {
                doNetworkCallSynchronous(okHttp.getClient(), request, okHttpRequest.getResponseClass(), okHttpRequest.getCallback());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public <T> void multipartRequest(String url
            , String authorization
            , List<RequestParams> requestParams
            , List<RequestParams> media
            , String tag
            , Class<T> responseClass
            , ApiCallBack<T> callback) {


        MultipartBody.Builder multiPartBuilder = new MultipartBody.Builder();
        multiPartBuilder.setType(MultipartBody.FORM);
        for (RequestParams requestParam : requestParams) {
            multiPartBuilder.addFormDataPart(requestParam.getKey(), (String) requestParam.getValue());
        }

        for (RequestParams requestParam : media) {
            multiPartBuilder.addFormDataPart(requestParam.getKey()
                    , getMediaName((String) requestParam.getValue())
                    , RequestBody.create(MediaType.parse(ContentType.autoDetect((String) requestParam.getValue()))
                            , new File((String) requestParam.getValue())));
        }

        RequestBody requestBody = multiPartBuilder.build();


        Request.Builder builder = new Request.Builder();
        if (!StringUtils.isBlank(authorization))
            builder.header("Authorization", authorization);
        builder.url(url);
        builder.tag(tag);
        builder.post(requestBody);
        Request request = builder.build();


        doNetworkCallAsynchronous(okHttp.getClient(), request, responseClass, callback);
    }

    public <T> void multipartRequest(boolean isAsynchronous
            , OkHttpRequest<T> okHttpRequest) {

        MultipartBody.Builder multiPartBuilder = new MultipartBody.Builder();
        multiPartBuilder.setType(MultipartBody.FORM);
        for (RequestParams requestParam : okHttpRequest.getRequestParams()) {
            multiPartBuilder.addFormDataPart(requestParam.getKey(), (String) requestParam.getValue());
        }

        for (RequestParams requestParam : okHttpRequest.getMedia()) {
            multiPartBuilder.addFormDataPart(requestParam.getKey()
                    , getMediaName((String) requestParam.getValue())
                    , RequestBody.create(MediaType.parse(ContentType.autoDetect((String) requestParam.getValue()))
                            , new File((String) requestParam.getValue())));
        }

        RequestBody requestBody = multiPartBuilder.build();


        Request.Builder builder = new Request.Builder();
        if (!StringUtils.isBlank(okHttpRequest.getAuthorization()))
            builder.header("Authorization", okHttpRequest.getAuthorization());
        builder.url(okHttpRequest.getUrl());
        builder.tag(okHttpRequest.getTag());
        builder.post(requestBody);
        Request request = builder.build();
        addWebCall(okHttpRequest.getTag(), new RequestBundle(okHttpRequest, request));

        doNetworkCallAsynchronous(okHttp.getClient(), request, okHttpRequest.getResponseClass(), okHttpRequest.getCallback());
    }


    private String getMediaName(String absPath) {
        String[] array = absPath.split("/");
        String mediaName = array[array.length - 1];

        return mediaName;
    }

    private <T> void doNetworkCallAsynchronous(OkHttpClient client,
                                               Request request,
                                               final Class<T> responseClass
            , final ApiCallBack<T> callback
    ) {

        callback.showProgress();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.hideProgress();
                callback.onFail(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                deleteWebCall(call.request().tag().toString());
                callback.hideProgress();
                String responseJson = response.body().string();
                try {
                    callback.onSuccess(responseClass == null ? null : DataParser.getResponse(responseJson, responseClass), responseJson);
                } catch (Exception e) {
                    callback.onSuccess(responseClass == null ? null : null, responseJson);
                    e.printStackTrace();
                }
            }
        });
    }

    public <T> void doNetworkCallAsynchronous(RequestBundle<T> requestBundle) {
        doNetworkCallAsynchronous(okHttp.getClient()
                , requestBundle.getRequest()
                , requestBundle.getOkHttpRequest().getResponseClass()
                , requestBundle.getOkHttpRequest().getCallback());
    }

    private <T> void doNetworkCallSynchronous(OkHttpClient client,
                                              Request request,
                                              final Class<T> responseClass
            , final ApiCallBack<T> callback) throws IOException {
        Response response = null;
        try {
            response = client.newCall(request).execute();

            String responseJson = response != null ? response.body().string() : "";
            isAsynchronous = true;
            deleteWebCall(request.tag().toString());
//            return DataParser.getResponse(responseJson, responseClass);

            try {
                callback.onSuccess(responseClass == null ? null : DataParser.getResponse(responseJson, responseClass), responseJson);
            } catch (Exception e) {
                callback.onSuccess(responseClass == null ? null : null, responseJson);
            }
        } catch (IOException e) {
            e.printStackTrace();
            callback.onFail(e);
        }
//        return null;
    }


    public void addWebCall(String tag, RequestBundle requestBundle) {
        if (!request.containsKey(tag)) {
            request.put(tag, requestBundle);
        } else {
            request.remove(tag);
            request.put(tag, requestBundle);
        }
    }

    public void deleteWebCall(String tag) {
        if (!request.containsKey(tag)) {
            request.remove(tag);
        }
    }

    public void cancelCalls(String tag) {
        for (Call call : okHttp.getClient().dispatcher().queuedCalls()) {
            if (call.request().tag().equals(tag))
                call.cancel();
        }
        for (Call call : okHttp.getClient().dispatcher().runningCalls()) {
            if (call.request().tag().equals(tag))
                call.cancel();
        }
    }

    public void cancelAllCall() {
        okHttp.getClient().dispatcher().cancelAll();
    }

    public RequestBundle getRequestBundle(String tag) {
        if (request.isEmpty()) throw new IllegalStateException("request stack is empty");
        RequestBundle requestBundle = request.get(tag);
        return requestBundle;
    }

    public static class RequestBundle<T> {
        private OkHttpRequest<T> okHttpRequest;
        private Request request;

        public RequestBundle(OkHttpRequest<T> okHttpRequest, Request request) {

            this.okHttpRequest = okHttpRequest;
            this.request = request;
        }

        public OkHttpRequest<T> getOkHttpRequest() {
            return okHttpRequest;
        }

        public void setOkHttpRequest(OkHttpRequest<T> okHttpRequest) {
            this.okHttpRequest = okHttpRequest;
        }

        public Request getRequest() {
            return request;
        }

        public void setRequest(Request request) {
            this.request = request;
        }
    }
}