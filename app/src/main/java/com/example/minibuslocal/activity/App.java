package com.example.minibuslocal.activity;

import android.app.Application;
import android.content.SharedPreferences;
import android.media.AudioManager;

import com.alibaba.fastjson.JSONObject;
import com.example.minibuslocal.util.CrashHandler;

import static android_serialport_api.SreialComm.AUDIO_VOLUME;

/**
 * Created by fangju on 2018/12/28
 */
public class App extends Application {
    private static final String TAG = "App";
    private static App instance;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private AudioManager audioManager;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        CrashHandler.getInstance().init(this);//注册本地日志
        audioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);
        preferences = getSharedPreferences("saveData", MODE_PRIVATE);
        editor = getSharedPreferences("saveData", MODE_PRIVATE).edit();
    }

    /**
     * 返回本地密码信息
     *
     * @return
     */
    public synchronized String getPreferences(String key) {
        String userInfo = preferences.getString(key, "");
        return userInfo;
    }

    /**
     * 预存密码
     *
     * @param value
     */
    public synchronized void setPreferences(String key,String value) {
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * 设置系统音量(来自485的信息)
     */
    public void setAudioVolume(JSONObject object) {
        int id = object.getIntValue("id");
        int data = object.getIntValue("data");
        if (id == AUDIO_VOLUME) {//音量
            if (data >= 0) {//音量值大于等于零
                if(data > 1&&data< 26){
                    data = (data*15)/26+1;
                }
                if(data == 26){
                    data = 15;
                }
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, data, AudioManager.FLAG_PLAY_SOUND);
            }
        }
    }
}
