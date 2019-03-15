package com.example.minibuslocal.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.example.minibuslocal.R;
import com.example.minibuslocal.activity.MainActivity;
import com.example.minibuslocal.util.TimeThread;

import static com.example.minibuslocal.bean.IntegerCommand.BMS_SOC;
import static com.example.minibuslocal.bean.IntegerCommand.HAD_GPSPositioningStatus;
import static com.example.minibuslocal.bean.IntegerCommand.OBU_LocalTime;


/**
 * 上部分Fragment
 */
public class MainTopFragment extends Fragment {
    private MainActivity activity;
    private TextView topFragmentTime;//时间
    private TextView topFragmentBatteryTv;//电池文字信息
    private ImageView topFragmentBatteryIv;//电池图片
    private TimeThread timeThread;//时间线程

    public MainTopFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_top_fragment,container,false);
        topFragmentTime = (TextView)view.findViewById(R.id.topFragment_time);
        topFragmentBatteryTv = (TextView)view.findViewById(R.id.topFragment_battery_tv);
        topFragmentBatteryIv = (ImageView) view.findViewById(R.id.topFragment_battery_iv);
//        timeThread = new TimeThread(activity,topFragmentTime);
//        timeThread.start();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        timeThread.setFlag(false);
    }

    /**
     * 更新界面
     */
    public void refresh(JSONObject object){
        int id = object.getIntValue("id");
        if(id == OBU_LocalTime){//本地时间
            JSONObject data = object.getJSONObject("data");
            topFragmentTime.setText(getTime(data));
        }
        if(id == BMS_SOC){//动力电池剩余电量SOC
            //电量显示
            int battryNum = (int) object.getDoubleValue("data");
            topFragmentBatteryTv.setText(String.valueOf(battryNum)+"%");
            if(battryNum >=0 && battryNum < 17){
                topFragmentBatteryIv.setBackgroundResource(R.drawable.ic_battery_10);
            }else if(battryNum >= 17 && battryNum < 37){
                topFragmentBatteryIv.setBackgroundResource(R.drawable.ic_battery_20);
            }else if(battryNum >=37 && battryNum < 54){
                topFragmentBatteryIv.setBackgroundResource(R.drawable.ic_battery_30);
            }else if(battryNum >= 54 && battryNum < 71){
                topFragmentBatteryIv.setBackgroundResource(R.drawable.ic_battery_40);
            }else if(battryNum >= 71 && battryNum < 90){
                topFragmentBatteryIv.setBackgroundResource(R.drawable.ic_battery_50);
            }else if(battryNum >= 90){
                topFragmentBatteryIv.setBackgroundResource(R.drawable.ic_battery_60);
            }
        }
        if(id == HAD_GPSPositioningStatus){//GPS状态

        }
    }

    /**
     * int转换成时间
     */
    private String getTime(JSONObject object){
        StringBuffer stringBuffer = new StringBuffer();
        int hour = object.getIntValue("hour");
        int minute = object.getIntValue("minute");
        hour %= 24;
        minute %= 60;
        String finalHour = String.format("%02d",hour);
        String finalMinute = String.format("%02d",minute);
        stringBuffer.append(finalHour);
        stringBuffer.append(":");
        stringBuffer.append(finalMinute);
        return stringBuffer.toString();
    }
}
