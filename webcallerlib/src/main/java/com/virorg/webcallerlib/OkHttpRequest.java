package com.virorg.webcallerlib;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DroidDev on 18/12/16.
 */

public class OkHttpRequest<T> {
    private String url;
    private String authorization;
    private List<ContentValue> requestParams;
    private List<ContentValue> media;
    private String tag;
    private Class<T> responseClass;
    private ApiCallBack<T> callback;
    private List<ContentValue> listOfHeader;

    public OkHttpRequest(Builder builder) {
        url = builder.url;
        authorization = builder.authorization;
        requestParams = builder.requestParams;
        listOfHeader = builder.listOfHeader;
        media = builder.media;
        tag = builder.tag;
        responseClass = builder.responseClass;
        callback = builder.callback;
    }


    public String getUrl() {
        return url;
    }

    public String getAuthorization() {
        return authorization;
    }

    public List<ContentValue> getRequestParams() {
        return requestParams;
    }

    public List<ContentValue> getMedia() {
        return media;
    }

    public List<ContentValue> getListOfHeader() {
        return listOfHeader;
    }

    public void setListOfHeader(List<ContentValue> listOfHeader) {
        this.listOfHeader = listOfHeader;
    }

    public String getTag() {
        return tag;
    }

    public Class<T> getResponseClass() {
        return responseClass;
    }

    public ApiCallBack<T> getCallback() {
        return callback;
    }

    public static class Builder<T> {

        private String url;
        private String authorization;
        private List<ContentValue> requestParams;
        private List<ContentValue> media;
        private String tag;
        private Class<T> responseClass;
        private ApiCallBack<T> callback;
        private List<ContentValue> listOfHeader;

        public Builder() {
            url = "";
            authorization = "";
            requestParams = new ArrayList<ContentValue>();
            listOfHeader = new ArrayList<ContentValue>();
            media = new ArrayList<ContentValue>();
            tag = "";
            responseClass = null;
            callback = null;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        @Deprecated
        public Builder setAuthorization(String authorization) {
            this.authorization = authorization;
            return this;
        }

        public Builder setRequestParams(List<ContentValue> requestParams) {
            this.requestParams = requestParams;
            return this;
        }

        public Builder setMedia(List<ContentValue> requestParams) {
            this.media = requestParams;
            return this;
        }

        public Builder setTag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder setResponseClass(Class<T> responseClass) {
            this.responseClass = responseClass;
            return this;
        }

        public Builder setCallBack(ApiCallBack<?> callback) {
            this.callback = (ApiCallBack<T>) callback;
            return this;
        }

        public Builder setHeader(List<ContentValue> listOfHeader) {
            this.listOfHeader = listOfHeader;
            return this;
        }

        public OkHttpRequest build() {
            if (url == null) throw new IllegalStateException("url can't be null");
            if (StringUtils.isBlank(tag)) throw new IllegalStateException("tag can't be null");
            return new OkHttpRequest(this);
        }

    }
}
