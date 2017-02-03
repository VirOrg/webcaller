package com.virorg.webcallerlib;

/**
 * Created by DroidDev on 15/12/16.
 */

/**
 * that model class contains the request parameters of web call in key value pairs
 */
public class ContentValue {

    private String key;
    private Object value;
    private String stringValue;
    private int intValue;
    private long longValue;
    private double doubleValue;
    private float floatValue;
    private boolean booleanValue;

    public ContentValue(String key, Object value) {
        this.key = key;

        if(value instanceof String){
            stringValue = (String)value;
        }else if(value instanceof Integer){
            intValue = (Integer) value;
        }else if(value instanceof Long){
            longValue = (Long) value;
        }else if(value instanceof Float){
            floatValue = (Float) value;
        }else if(value instanceof Double){
            doubleValue = (Double) value;
        }else if(value instanceof Boolean){
            booleanValue = (Boolean) value;
        }

        this.value = value;
    }
    public ContentValue() {
        this.key = null;
        this.value = null;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getStringValue() {
        return stringValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public long getLongValue() {
        return longValue;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public float getFloatValue() {
        return floatValue;
    }

    public boolean getBooleanValue() {
        return booleanValue;
    }
}

