package com.example.minibuslocal.test;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by fangju on 2019/1/25
 */
public class TouchTimer {
    private static final String TAG = "TouchTimer";
    private TimerManager timerManager = null;
    private Timer timer = null;
    private TimerTask timerTask = null;
    private int delay = 0;
    private int period = 30000;//30s
    private boolean isStop = false;

    public TouchTimer(TimerManager timerManager){
        this.timerManager = timerManager;
    }

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }

    /**
     * 开启定时器
     */
    public void startTimer(){
        isStop = false;
        if(timer == null){
            timer = new Timer();
        }
        if(timerTask == null){
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    //
                    if(!timerManager.isPause()){//如果还是假的话
                        timerManager.setPause(true);//重新开始发送
                        stopTimer();
                        isStop = true;//触摸定时器停止了
                        Log.d(TAG, "run: "+"");
                    }
                }
            };
        }
        if(timer != null&&timerTask !=null){
            timer.schedule(timerTask,delay,period);
        }else{
            if(isStop){//定时器停止了
                startTimer();
            }
        }
    }

    /**
     * 关闭定时器
     */
    public void stopTimer(){
        isStop = true;
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        if(timerTask != null){
            timerTask.cancel();
            timerTask = null;
        }
    }
}
