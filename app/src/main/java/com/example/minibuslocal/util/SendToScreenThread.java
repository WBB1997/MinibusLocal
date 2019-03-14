package com.example.minibuslocal.util;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 接收can总线的消息，发送至前风挡、左门、侧门
 * Created by fangju on 2018/12/8
 */
public class SendToScreenThread extends Thread {
    private static final String TAG = "SendToScreenThread";
    private String[] hostNames = new String[]{"192.168.1.20","192.168.1.50","192.168.1.40"};//前风挡、左车门、右车门
    private int port = 5556;
    private DatagramSocket dSocket = null;
    private DatagramPacket dPacket = null;
    private byte[] buffer = null;//数据报大小
    private JSONObject object = null;//接收的can总线信息
    private int target = -1;//屏幕编号
    private Context mContext = null;

    public SendToScreenThread(Context mContext, JSONObject object, int target){
        this.mContext = mContext;
        this.object = object;
        this.target = target;
    }

    @Override
    public void run() {
        String message = object.toJSONString();
        buffer = message.getBytes();
        //发送给特定显示屏
        try {
            //根据命令号确定hostName
            InetAddress address = InetAddress.getByName(hostNames[target]);
            dSocket = new DatagramSocket();
            dPacket = new DatagramPacket(buffer,buffer.length,address,port);
            //发送数据
            if(NetWorkUtil.getInstance(mContext).isAvailable()){
                dSocket.send(dPacket);
                LogUtil.d(TAG,"发送成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(dSocket != null){
                dSocket.close();
            }
        }
    }

    public class RetryTimer{
        private Timer timer = null;
        private TimerTask timerTask = null;
        private int delay = 0;
        private int period = 3000;

        public void start(){
            if(timer == null){
                timer = new Timer();
            }
            if(timerTask == null){
                timerTask = new TimerTask() {
                    @Override
                    public void run() {

                    }
                };
            }
            if(timer != null && timerTask != null){
                timer.schedule(timerTask,delay,period);
            }
        }

        public void stop(){
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

}
