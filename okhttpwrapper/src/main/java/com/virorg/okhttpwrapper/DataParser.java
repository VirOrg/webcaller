package com.virorg.okhttpwrapper;
import com.google.gson.Gson;

public class DataParser {
    /**
     * method is responsible for parsing json to java POJO based on class type through Gson library
     * @param json json string
     * @param classT class type in which json has to be parsed
     * @param <T>
     * @return java POJO object
     * @throws Exception
     */

    public static <T> T getResponse(String json, Class<T> classT) throws Exception {
        T response = new Gson().fromJson(json, classT);
        return response;
    }
}

