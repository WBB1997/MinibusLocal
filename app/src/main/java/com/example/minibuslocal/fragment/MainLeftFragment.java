package com.example.minibuslocal.fragment;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.example.minibuslocal.R;
import com.example.minibuslocal.activity.MainActivity;
import com.example.minibuslocal.test.TimerManager;
import com.example.minibuslocal.view.CustomOnClickListener;

import static com.example.minibuslocal.bean.IntegerCommand.BCM_ACBlowingLevel;
import static com.example.minibuslocal.bean.IntegerCommand.BCM_DemisterStatus;
import static com.example.minibuslocal.bean.IntegerCommand.BCM_Flg_Stat_DangerAlarmLamp;
import static com.example.minibuslocal.bean.IntegerCommand.BCM_Flg_Stat_HighBeam;
import static com.example.minibuslocal.bean.IntegerCommand.BCM_Flg_Stat_LeftTurningLamp;
import static com.example.minibuslocal.bean.IntegerCommand.BCM_Flg_Stat_LowBeam;
import static com.example.minibuslocal.bean.IntegerCommand.BCM_Flg_Stat_RearFogLamp;
import static com.example.minibuslocal.bean.IntegerCommand.BCM_Flg_Stat_RightTurningLamp;
import static com.example.minibuslocal.bean.IntegerCommand.HMI_Dig_Ord_DangerAlarm;
import static com.example.minibuslocal.bean.IntegerCommand.HMI_Dig_Ord_Demister_Control;
import static com.example.minibuslocal.bean.IntegerCommand.HMI_Dig_Ord_FANPWM_Control;
import static com.example.minibuslocal.bean.IntegerCommand.HMI_Dig_Ord_HighBeam;
import static com.example.minibuslocal.bean.IntegerCommand.HMI_Dig_Ord_LeftTurningLamp;
import static com.example.minibuslocal.bean.IntegerCommand.HMI_Dig_Ord_LowBeam;
import static com.example.minibuslocal.bean.IntegerCommand.HMI_Dig_Ord_RearFogLamp;
import static com.example.minibuslocal.bean.IntegerCommand.HMI_Dig_Ord_RightTurningLamp;
import static com.example.minibuslocal.bean.IntegerCommand.HMI_Dig_Ord_air_grade;
import static com.example.minibuslocal.bean.IntegerCommand.HMI_Dig_Ord_air_model;
import static com.example.minibuslocal.transmit.Class.HMI.*;

/**
 * 左边Fragment
 */
public class MainLeftFragment extends Fragment {
    private static final String TAG = "MainLeftFragment";
    private MainActivity activity;//MainActivity
    private ImageView leftFragmentCarCloseDoor;//车门关
    private ImageView leftFragmentCarOpenDoor;//车门开
    private ImageView leftFragmentCarFoglightOpen;//雾灯开
    private ImageView leftFragmentCarLeftlightOpen;//转向灯开
    private ImageView leftFragmentCarLowbeamOpen;//近光灯开
    private ImageView leftFragmentCarHighbeamOpen;//远光灯开
    private ImageButton leftFragmentLowBeam;//近光灯
    private ImageButton leftFragmentHighBeam;//远光灯
    private ImageButton leftFragmentFrontFogLight;//前雾灯
    private ImageButton leftFragmentBackFogLight;//后雾灯
    private ImageButton leftFragmentLeftLight;//左转向灯
    private ImageButton leftFragmentRightLight;//右转向灯
    private Button leftFragmentAuto;//自动调节
    private ImageButton leftFragmentErrorLight;//警示灯
    private ImageButton leftFragmentCoolAirImg;//冷气
    private ImageButton leftFragmentHotAirImg;//暖气
    private ImageButton leftFragmentDeFogImg;//除雾
//    private LinearLayout leftFragmentCoolAir;//冷气布局
//    private LinearLayout leftFragmentHotAir;//暖气布局
//    private LinearLayout leftFragmentDeFog;//除雾布局
    private TextView leftFragmentConditionSize;//风扇大小
    private SeekBar leftFragmentSeekBar;//风扇滑动条
    //发送CAN总线的数据
    private String clazz = "HMI";//所属类名
    private int field = -1;//属性
    private Object o = null;//状态
    private int seekBarIndex = 0;//挡位大小
    private int finalProgress = 0;//最终大小
    private boolean typeFlag = false;//判断是否改变状态
    private TimerManager timerManager = null;

    public MainLeftFragment() {
    }

    public TimerManager getTimerManager() {
        return timerManager;
    }

    public void setTimerManager(TimerManager timerManager) {
        this.timerManager = timerManager;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_left_fragment, container, false);
        leftFragmentCarCloseDoor = view.findViewById(R.id.leftFragment_car_close_door);
        leftFragmentCarOpenDoor = view.findViewById(R.id.leftFragment_car_open_door);
        leftFragmentCarFoglightOpen = view.findViewById(R.id.leftFragment_car_foglight_open);
        leftFragmentCarLeftlightOpen = view.findViewById(R.id.leftFragment_car_leftlight_open);
        leftFragmentCarLowbeamOpen = (ImageView) view.findViewById(R.id.leftFragment_car_lowbeam_open);
        leftFragmentCarHighbeamOpen = (ImageView) view.findViewById(R.id.leftFragment_car_highbeam_open);
        leftFragmentLowBeam = (ImageButton) view.findViewById(R.id.leftFragment_lowBeam);
        leftFragmentLowBeam.setOnClickListener(onClickListener);
        leftFragmentHighBeam = (ImageButton) view.findViewById(R.id.leftFragment_highBeam);
        leftFragmentHighBeam.setOnClickListener(onClickListener);
        leftFragmentFrontFogLight = (ImageButton) view.findViewById(R.id.leftFragment_front_fogLight);
        leftFragmentFrontFogLight.setOnClickListener(onClickListener);
        leftFragmentBackFogLight = (ImageButton) view.findViewById(R.id.leftFragment_back_fogLight);
        leftFragmentBackFogLight.setOnClickListener(onClickListener);
        leftFragmentLeftLight = view.findViewById(R.id.leftFragment_leftLight);
        leftFragmentLeftLight.setOnClickListener(onClickListener);
        leftFragmentRightLight = (ImageButton) view.findViewById(R.id.leftFragment_rightLight);
        leftFragmentRightLight.setOnClickListener(onClickListener);
        leftFragmentAuto = (Button) view.findViewById(R.id.leftFragment_auto);
        leftFragmentErrorLight = (ImageButton) view.findViewById(R.id.leftFragment_errorLight);
        leftFragmentErrorLight.setOnClickListener(onClickListener);
        leftFragmentCoolAirImg = (ImageButton) view.findViewById(R.id.leftFragment_coolAir_img);
        leftFragmentHotAirImg = (ImageButton) view.findViewById(R.id.leftFragment_hotAir_img);
        leftFragmentDeFogImg = (ImageButton) view.findViewById(R.id.leftFragment_deFog_img);
//        leftFragmentCoolAir = (LinearLayout) view.findViewById(R.id.leftFragment_coolAir);
//        leftFragmentCoolAir.setOnClickListener(onClickListener);
//        leftFragmentHotAir = (LinearLayout) view.findViewById(R.id.leftFragment_hotAir);
//        leftFragmentHotAir.setOnClickListener(onClickListener);
//        leftFragmentDeFog = (LinearLayout) view.findViewById(R.id.leftFragment_deFog);
//        leftFragmentDeFog.setOnClickListener(onClickListener);
        leftFragmentConditionSize = (TextView) view.findViewById(R.id.leftFragment_condition_size);
        leftFragmentSeekBar = (SeekBar) view.findViewById(R.id.leftFragment_seekBar);
        leftFragmentSeekBar.setEnabled(false);//默认滑动条无法点击
        leftFragmentSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        //设置滑块颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            leftFragmentSeekBar.getThumb().setColorFilter(Color.parseColor("#9cf8f8"), PorterDuff.Mode.SRC_ATOP);
        }
        return view;
    }

    /**
     * 滑动条事件监听
     */
    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            LogUtil.d(TAG,String.valueOf(progress));
            if (fromUser) {
                if (progress >= 0 && progress < 10) {
                    seekBarIndex = AIR_GRADE_OFF;
                    finalProgress = 0;
                } else if (progress >= 10 && progress < 30) {
                    seekBarIndex = AIR_GRADE_FIRST_GEAR;
                    finalProgress = 20;
                } else if (progress >= 30 && progress < 50) {
                    seekBarIndex = AIR_GRADE_SECOND_GEAR;
                    finalProgress = 40;
                } else if (progress >= 50 && progress < 70) {
                    seekBarIndex = AIR_GRADE_THIRD_GEAR;
                    finalProgress = 60;
                } else if (progress >= 70 && progress < 90) {
                    seekBarIndex = AIR_GRADE_FOURTH_GEAR;
                    finalProgress = 80;
                } else if (progress >= 90 && progress <= 100) {
                    seekBarIndex = AIR_GRADE_FIVE_GEAR;
                    finalProgress = 100;
                }
//                changeTimerFlag();
                leftFragmentConditionSize.setText(String.valueOf(seekBarIndex));
                //发送最终数据至CAN(1-5档)
                activity.sendToCAN(clazz, HMI_Dig_Ord_air_grade, seekBarIndex);
                // 风扇PWM占比控制信号
                activity.sendToCAN(clazz, HMI_Dig_Ord_FANPWM_Control, progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            seekBar.setProgress(finalProgress);
        }
    };

    /**
     * 事件点击监听器
     */
    private CustomOnClickListener onClickListener = new CustomOnClickListener(200) {
        @Override
        public void onSingleClick(View v) {
            switch (v.getId()) {
                case R.id.leftFragment_lowBeam: {//近光灯
                    leftFragmentLowBeam.setActivated(!leftFragmentLowBeam.isActivated());
                    if (leftFragmentLowBeam.isActivated()) {//近光灯开启
                        if (leftFragmentHighBeam.isActivated()) {//远光灯是开的
                            leftFragmentHighBeam.setActivated(false);//关闭远光灯
                            leftFragmentCarHighbeamOpen.setVisibility(View.INVISIBLE);
                        }
                        leftFragmentCarLowbeamOpen.setVisibility(View.VISIBLE);
                    } else {
                        leftFragmentCarLowbeamOpen.setVisibility(View.INVISIBLE);
                    }
                    typeFlag = true;
                    field = HMI_Dig_Ord_LowBeam;
                    o = transInt(leftFragmentLowBeam.isActivated());
                    break;
                }
                case R.id.leftFragment_highBeam: {//远光灯
                    leftFragmentHighBeam.setActivated(!leftFragmentHighBeam.isActivated());
                    if (leftFragmentHighBeam.isActivated()) {//远光灯开启
                        if (leftFragmentLowBeam.isActivated()) {//近光灯是开的
                            leftFragmentLowBeam.setActivated(false);//关闭近光灯
                            leftFragmentCarLowbeamOpen.setVisibility(View.INVISIBLE);
                        }
                        leftFragmentCarHighbeamOpen.setVisibility(View.VISIBLE);
                    } else {
                        leftFragmentCarHighbeamOpen.setVisibility(View.INVISIBLE);
                    }
                    typeFlag = true;
                    field = HMI_Dig_Ord_HighBeam;
                    o = transInt(leftFragmentHighBeam.isActivated());
                    break;
                }
                case R.id.leftFragment_front_fogLight: {//前雾灯
                    leftFragmentFrontFogLight.setActivated(!leftFragmentFrontFogLight.isActivated());
//                    field = HMI.HMI_leftFragmentFrontFogLight;
//                    o = leftFragmentFrontFogLight.isActivated();
                    break;
                }
                case R.id.leftFragment_back_fogLight: {//后雾灯
                    leftFragmentBackFogLight.setActivated(!leftFragmentBackFogLight.isActivated());
                    if (leftFragmentBackFogLight.isActivated()) {
                        leftFragmentCarFoglightOpen.setVisibility(View.VISIBLE);
                    } else {
                        leftFragmentCarFoglightOpen.setVisibility(View.INVISIBLE);
                    }
                    typeFlag = true;
                    field = HMI_Dig_Ord_RearFogLamp;
                    o = transInt(leftFragmentBackFogLight.isActivated());
                    break;
                }
                case R.id.leftFragment_leftLight: {//左转向灯
                    leftFragmentLeftLight.setActivated(!leftFragmentLeftLight.isActivated());
                    if (leftFragmentLeftLight.isActivated()) {//左转向灯开启
                        if (leftFragmentRightLight.isActivated()) {//右转向灯是开的
                            leftFragmentRightLight.setActivated(false);//关闭右转向灯
                        }
                        leftFragmentCarLeftlightOpen.setVisibility(View.VISIBLE);
                    } else {
                        leftFragmentCarLeftlightOpen.setVisibility(View.INVISIBLE);
                    }
//                    if(leftFragmentErrorLight.isActivated()){//如果警示灯是开的关闭它
//                        leftFragmentErrorLight.setActivated(false);
//                        leftFragmentLeftLight.setActivated(true);
//                    }
                    typeFlag = true;
                    field = HMI_Dig_Ord_LeftTurningLamp;
                    o = transInt(leftFragmentLeftLight.isActivated());
                    break;
                }
                case R.id.leftFragment_rightLight: {//右转向灯
                    leftFragmentRightLight.setActivated(!leftFragmentRightLight.isActivated());
                    if (leftFragmentRightLight.isActivated()) {//右转向灯开启
                        if (leftFragmentRightLight.isActivated()) {//左转向灯是开的
                            leftFragmentLeftLight.setActivated(false);//关闭左转向灯
                            leftFragmentCarLeftlightOpen.setVisibility(View.INVISIBLE);
                        }
                    } else {
                    }
//                    if(leftFragmentErrorLight.isActivated()){//如果警示灯是开的关闭它
//                        leftFragmentErrorLight.setActivated(false);
//                    }
                    typeFlag = true;
                    field = HMI_Dig_Ord_RightTurningLamp;
                    o = transInt(leftFragmentRightLight.isActivated());
                    break;
                }
                case R.id.leftFragment_errorLight: {//警示灯
                    leftFragmentErrorLight.setActivated(!leftFragmentErrorLight.isActivated());
//                    if(leftFragmentErrorLight.isActivated()){//警示灯开启
//                        leftFragmentRightLight.setActivated(true);//左转向灯开启
//                        leftFragmentLeftLight.setActivated(true);//右转向灯开启
//                    }else{//警示灯关闭
//                        leftFragmentRightLight.setActivated(false);//左转向灯关闭
//                        leftFragmentLeftLight.setActivated(false);//右转向灯关闭
//                    }
                    typeFlag = true;
                    field = HMI_Dig_Ord_DangerAlarm;
                    o = transInt(leftFragmentErrorLight.isActivated());
                    break;
                }
                case R.id.leftFragment_coolAir_img: {//冷气
                    leftFragmentCoolAirImg.setActivated(!leftFragmentCoolAirImg.isActivated());
                    if (leftFragmentCoolAirImg.isActivated()) {//如果冷气是开的
                        //滑动条可点击
                        leftFragmentSeekBar.setEnabled(true);
//                        leftFragmentCoolAirImg.setActivated(true);
                        //关闭暖气
                        leftFragmentHotAirImg.setActivated(false);
//                        leftFragmentHotAir.setActivated(false);
                    } else {
                        leftFragmentCoolAirImg.setActivated(false);
                    }
                    typeFlag = true;
                    field = HMI_Dig_Ord_air_model;//空调模式
                    if (leftFragmentCoolAirImg.isActivated()) {
                        o = AIR_MODEL_COOL;//制冷模式
                    } else {
                        if (!leftFragmentHotAirImg.isActivated()) {//空调冷气和暖气都关闭
                            o = ARI_MODEL_CLOSE;//关闭
                            //滑动条不可点击
                            seekBarIndex = AIR_GRADE_OFF;//变为OFF档
                            leftFragmentSeekBar.setEnabled(false);
                            leftFragmentSeekBar.setProgress(0);
                            leftFragmentConditionSize.setText("0");
                        }
                    }
                    break;
                }
                case R.id.leftFragment_hotAir_img: {//暖气
                    leftFragmentHotAirImg.setActivated(!leftFragmentHotAirImg.isActivated());
                    if (leftFragmentHotAirImg.isActivated()) {//如果暖气是开的
                        //滑动条可点击
                        leftFragmentSeekBar.setEnabled(true);
                        leftFragmentHotAirImg.setActivated(true);
                        //关闭冷气
                        leftFragmentCoolAirImg.setActivated(false);
//                        leftFragmentCoolAir.setActivated(false);
                    } else {
                        leftFragmentHotAirImg.setActivated(false);
                    }
                    typeFlag = true;
                    field = HMI_Dig_Ord_air_model;//空调模式
                    if (leftFragmentHotAirImg.isActivated()) {
                        o = AIR_MODEL_HEAT;//制热模式
                    } else {
                        if (!leftFragmentCoolAirImg.isActivated()) {//空调冷气和暖气都关闭
                            o = ARI_MODEL_CLOSE;//关闭
                            //滑动条不可点击
                            seekBarIndex = AIR_GRADE_OFF;
                            leftFragmentSeekBar.setEnabled(false);
                            leftFragmentSeekBar.setProgress(0);
                            leftFragmentConditionSize.setText("0");
                        }
                    }
                    break;
                }
                case R.id.leftFragment_deFog_img: {//除雾
                    leftFragmentDeFogImg.setActivated(!leftFragmentDeFogImg.isActivated());
                    if (leftFragmentDeFogImg.isActivated()) {//如果除雾是开的
                        leftFragmentDeFogImg.setActivated(true);
                    } else {
                        leftFragmentDeFogImg.setActivated(false);
                    }
                    typeFlag = true;
                    field = HMI_Dig_Ord_Demister_Control;//除雾控制
                    o = transInt(leftFragmentDeFogImg.isActivated());
                    break;
                }
            }
            if (typeFlag) {//如果按钮被点击（有效）
//                changeTimerFlag();
                activity.sendToCAN(clazz, field, o);
                if (field == HMI_Dig_Ord_air_model) {//如果当前是空调模式
                    activity.sendToCAN(clazz, HMI_Dig_Ord_air_grade, seekBarIndex);//档位
                    if (ARI_MODEL_CLOSE == (int) o) {//空调关闭
                        // 风扇PWM占比控制信号
                        activity.sendToCAN(clazz, HMI_Dig_Ord_FANPWM_Control, 0);
                    }
                }
                typeFlag = false;
            }
        }
    };

    /**
     * 改变定时器状态
     */
    private void changeTimerFlag() {
        if (timerManager != null) {
            timerManager.setPause(false);
        }
    }

    /**
     * 更新布局
     */
    public void refresh(JSONObject object) {
        int id = object.getIntValue("id");
        Object data = object.get("data");
//        LogUtil.d(TAG, "id:" + id);
        switch (id) {
            case BCM_Flg_Stat_LeftTurningLamp: {// 左转
                leftFragmentLeftLight.setActivated((boolean)data);
                if ((boolean)data) {//要求左转开
                    leftFragmentRightLight.setActivated(false);//右转向灯关
//                    leftFragmentErrorLight.setActivated(false);//双闪关
                    leftFragmentCarLeftlightOpen.setVisibility(View.VISIBLE);
                } else {
                    leftFragmentCarLeftlightOpen.setVisibility(View.INVISIBLE);
                }
                break;
            }
            case BCM_Flg_Stat_RightTurningLamp: {// 右转
                leftFragmentRightLight.setActivated((boolean)data);
                if ((boolean)data) {
                    leftFragmentLeftLight.setActivated(false);//左转向灯关
//                    leftFragmentErrorLight.setActivated(false);//双闪关
                } else {
//
                }
                break;
            }
            case BCM_Flg_Stat_HighBeam: {// 远光灯
                leftFragmentHighBeam.setActivated((boolean)data);
                if ((boolean)data) {
                    leftFragmentLowBeam.setActivated(false);//近光灯关
                    leftFragmentCarLowbeamOpen.setVisibility(View.INVISIBLE);
                    leftFragmentCarHighbeamOpen.setVisibility(View.VISIBLE);
                } else {
                    leftFragmentCarHighbeamOpen.setVisibility(View.INVISIBLE);
                }
                break;
            }
            case BCM_Flg_Stat_LowBeam: {// 近光灯
                leftFragmentLowBeam.setActivated((boolean)data);
                if ((boolean)data) {
                    leftFragmentHighBeam.setActivated(false);//远光灯关
                    leftFragmentCarHighbeamOpen.setVisibility(View.INVISIBLE);
                    leftFragmentCarLowbeamOpen.setVisibility(View.VISIBLE);
                } else {
                    leftFragmentCarLowbeamOpen.setVisibility(View.INVISIBLE);
                }
                break;
            }
            case BCM_Flg_Stat_RearFogLamp: {// 后雾灯
                leftFragmentBackFogLight.setActivated((boolean)data);
                if ((boolean)data) {
                    leftFragmentCarFoglightOpen.setVisibility(View.VISIBLE);
                } else {
                    leftFragmentCarFoglightOpen.setVisibility(View.INVISIBLE);
                }
                break;
            }
            case BCM_Flg_Stat_DangerAlarmLamp: {// 双闪
                leftFragmentErrorLight.setActivated((boolean)data);
//                if (data) {//要求双闪开
//                    leftFragmentLeftLight.setActivated(true);
//                    leftFragmentRightLight.setActivated(true);
//                    leftFragmentCarLeftlightOpen.setVisibility(View.VISIBLE);
//                } else {
//                    leftFragmentLeftLight.setActivated(false);
//                    leftFragmentRightLight.setActivated(false);
//                    leftFragmentCarLeftlightOpen.setVisibility(View.INVISIBLE);
//                }
                break;
            }
            case BCM_ACBlowingLevel: {//空调风量档位
                int index = object.getIntValue("data");//接收档位
                if (index != AIR_GRADE_SIX_GEAR) {
                    seekBarIndex = index;//当前档位
                    leftFragmentConditionSize.setText(String.valueOf(seekBarIndex));//设置档位数值
                    leftFragmentSeekBar.setProgress(seekBarIndex * 20);//设置滑动条
                }
                break;
            }
            case BCM_DemisterStatus: {//除雾状态
                leftFragmentDeFogImg.setActivated((boolean)data);
                break;
            }
//            case VCU_ACWorkingStatus:{//空调工作模式信号
//                int flag = (int)data;
//                if(flag == 0){//制冷
//
//                }else if(flag == 1){
//
//                }else if(flag == 2){
//
//                }
//                break;
//            }

        }
    }

    /**
     * 将boolean转换成int
     *
     * @param flag
     * @return
     */
    private int transInt(boolean flag) {
        if (flag) {
            return ON;
        }
        return OFF;
    }
}
