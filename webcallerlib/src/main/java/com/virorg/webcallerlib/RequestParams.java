package com.virorg.webcallerlib;

/**
 * Created by DroidDev on 15/12/16.
 */

/**
 * that model class contains the request parameters of web call in key value pairs
 */
public class RequestParams {

    private String key;
    private String value;

    public RequestParams(String key, String value) {
        this.key = key;
        this.value = value;
    }
    public RequestParams() {
        this.key = null;
        this.value = null;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
