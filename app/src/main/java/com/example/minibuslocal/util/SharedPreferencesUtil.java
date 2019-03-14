package com.example.minibuslocal.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by fangju on 2019/1/20
 */
public class SharedPreferencesUtil {
    private static SharedPreferencesUtil instance;
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;


    private SharedPreferencesUtil(Context mContext){

    }

    public static SharedPreferencesUtil getInstance(Context mContext) {
        if(instance == null){
            instance = new SharedPreferencesUtil(mContext);
        }
        return instance;
    }

    /**
     * 存入数据
     * @param key
     * @param value
     */
    public static void setSP(String key,String value){
        editor.putString(key,value);
        editor.apply();
    }

    /**
     * 获取数据
     * @param key
     * @return
     */
    public static String getSP(String key){
        return preferences.getString(key,"");
    }

    /**
     * 移除数据
     * @param key
     */
    public static void removeSP(String key){
        editor.remove(key);
        editor.apply();
    }

}
