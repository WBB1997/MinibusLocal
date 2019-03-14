package com.example.minibuslocal.util;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by fangju on 2019/1/15
 */
public class PlayAudio {
    private static PlayAudio instance;

    private PlayAudio(){

    }

    public static PlayAudio getInstance() {
        if(instance == null){
            instance = new PlayAudio();
        }
        return instance;
    }

    /**
     * 播放分流音频
     */
    public void playSeparateAudio() {
        File video = new File(Environment.getExternalStorageDirectory(), "video.mp4");
        File out = new File(Environment.getExternalStorageDirectory(), "audio.mp3");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            VideoSeparateUtil.videoToAudio(video.getPath(), out);
        }
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(out.getPath());
            mediaPlayer.prepare();
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
