package com.virorg.webcallerlib;

import android.content.Context;
import android.support.annotation.StringRes;

/**
 * Created by DroidDev on 31/10/16.
 */
public class StringUtils {
    public static String captalizeFirstLetter(String myString){
        String upperString = myString.substring(0,1).toUpperCase() + myString.substring(1);
        return upperString;
    }

    public static String getString(Context context, @StringRes int id) {
        return context.getResources().getString(id);
    }

    /** method to check whether string is empty on null
     * @param s : string*/
    public static boolean isBlank(String s){

        if(s!=null && !s.equals("")){
            return false;
        }else {
            return true;
        }
    }

    public static String addZeroWhenSingleDigit(int i){

        String digit = i+"";
        if(i<=9){
            digit ="0"+digit;
        }

        return digit;
    }
}
