package com.example.minibuslocal.fragment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import com.alibaba.fastjson.JSONObject;
import com.example.minibuslocal.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.minibuslocal.bean.IntegerCommand.HAD_ArrivingSiteRemind;

public class MainCenterFragment extends Fragment {
    private static final String TAG = "MainCenterFragment";
    private ImageView centerFragmentMap;
    private VideoView centerFragmentVideoView;
    private static List<File> files  = new ArrayList<>();
    private int lastProgress = -1;
    static {
        files.add(new File(Environment.getExternalStorageDirectory()+"/Movies/","movie1.avi"));
        files.add(new File(Environment.getExternalStorageDirectory()+"/Movies/","movie2.avi"));
        files.add(new File(Environment.getExternalStorageDirectory()+"/Movies/","movie3.mp4"));
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_center_fragment,container,false);
        centerFragmentMap = (ImageView) view.findViewById(R.id.center_fragment_map);
        centerFragmentVideoView = (VideoView)view.findViewById(R.id.center_fragment_videoView);
        centerFragmentVideoView.setOnCompletionListener(onCompletionListener);
        centerFragmentVideoView.setOnErrorListener(onErrorListener);
        centerFragmentVideoView.setOnPreparedListener(onPreparedListener);
        return view;
    }

    /**
     * 切换显示
     * @param mapVisibility
     * @param videoVisibility
     */
    private void setVisibility(int mapVisibility,int videoVisibility){
        centerFragmentVideoView.setVisibility(videoVisibility);
        centerFragmentMap.setVisibility(mapVisibility);
    }

    /**
     * 加载视频
     * @param index
     */
    private void initVideoPath(int index){
        if(index < files.size() && index >=0){
            File file = files.get(index);
            centerFragmentVideoView.setVideoPath(file.getPath());
        }
    }

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            centerFragmentVideoView.suspend();
            setVisibility(View.VISIBLE,View.INVISIBLE);
            Log.d(TAG, "onCompletion: ");
        }
    };

    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            centerFragmentVideoView.suspend();
            setVisibility(View.VISIBLE,View.INVISIBLE);
            Log.d(TAG, "onError: ");
            return true;
        }
    };

    private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            setVisibility(View.INVISIBLE,View.VISIBLE);
            centerFragmentVideoView.start();
            Log.d(TAG, "onPrepared: ");
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        lastProgress = centerFragmentVideoView.getCurrentPosition();
        centerFragmentVideoView.stopPlayback();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        if(lastProgress >= 0){
            centerFragmentVideoView.seekTo(lastProgress);
            lastProgress = -1;
        }
        Log.d(TAG, "onResume: ");
    }

    /**
     * 更新布局
     * @param object
     */
    public void refresh(JSONObject object) {
        int id = object.getIntValue("id");
        int data = object.getIntValue("data");
        if(id == HAD_ArrivingSiteRemind){//到站提醒
            initVideoPath(2);
        }
    }
}
