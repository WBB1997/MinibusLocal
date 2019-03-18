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
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.alibaba.fastjson.JSONObject;
import com.example.minibuslocal.R;
import com.example.minibuslocal.view.CustomVideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.minibuslocal.bean.IntegerCommand.HAD_ArrivingSiteRemind;
import static com.example.minibuslocal.bean.IntegerCommand.HAD_CurrentDrivingRoadIDNum;
import static com.example.minibuslocal.bean.IntegerCommand.HAD_NextStationIDNumb;

public class MainCenterFragment extends Fragment {
    private static final String TAG = "MainCenterFragment";
    private ImageView centerFragmentMap;
    private VideoView centerFragmentVideoView;
    private static List<File> files  = new ArrayList<>();
    private int lastProgress = -1;//上一次播放进度
    private int lastStationId = -1;//下一个站点
    private int index = 0;
    static {
        files.add(new File(Environment.getExternalStorageDirectory()+"/minibus/video/","movie1.mp4"));
        files.add(new File(Environment.getExternalStorageDirectory()+"/minibus/video/","movie2.mp4"));
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_center_fragment,container,false);
        centerFragmentMap = (ImageView) view.findViewById(R.id.center_fragment_map);
        centerFragmentVideoView = (CustomVideoView)view.findViewById(R.id.center_fragment_videoView);
        centerFragmentVideoView.setOnCompletionListener(onCompletionListener);
        centerFragmentVideoView.setOnErrorListener(onErrorListener);
//        centerFragmentVideoView.setOnPreparedListener(onPreparedListener);
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
            lastProgress = -1;
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
        Log.d(TAG, "refresh: "+id+":"+data);
        if (id == HAD_ArrivingSiteRemind && data == 2) {//到站提醒
            if(index == 0){
                index = 1;
            }else if(index == 1){
                index = 0;
            }
            if(centerFragmentVideoView.isPlaying()){
                centerFragmentVideoView.suspend();
            }
            initVideoPath(index);
            if(!centerFragmentVideoView.isPlaying()){
                setVisibility(View.INVISIBLE,View.VISIBLE);
                centerFragmentVideoView.start();
            }
        }
    }
}
