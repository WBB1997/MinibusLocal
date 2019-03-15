package com.example.minibuslocal.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSONObject;
import com.example.minibuslocal.R;
import com.example.minibuslocal.fragment.MainCenterFragment;
import com.example.minibuslocal.fragment.MainLeftFragment;
import com.example.minibuslocal.fragment.MainLowBatteryFragment;
import com.example.minibuslocal.fragment.MainRightFragment1;
import com.example.minibuslocal.fragment.MainRightFragment2;
import com.example.minibuslocal.fragment.MainTopFragment;
import com.example.minibuslocal.service.MusicService;
import com.example.minibuslocal.test.TimerManager;
import com.example.minibuslocal.transmit.Transmit;
import com.example.minibuslocal.util.ActivityCollector;
import com.example.minibuslocal.util.LogUtil;
import com.example.minibuslocal.util.MyHandler;
import com.example.minibuslocal.util.SendToScreenThread;
import com.example.minibuslocal.util.ToastUtil;
import com.example.minibuslocal.view.CustomOnClickListener;

import java.util.HashMap;
import java.util.Map;

import android_serialport_api.SreialComm;

import static com.example.minibuslocal.bean.IntegerCommand.*;
import static com.example.minibuslocal.transmit.Class.HMI.AIR_GRADE_OFF;
import static com.example.minibuslocal.transmit.Class.HMI.AIR_GRADE_SIX_GEAR;
import static com.example.minibuslocal.transmit.Class.HMI.AIR_MODEL_AWAIT;
import static com.example.minibuslocal.transmit.Class.HMI.DRIVE_MODEL_AUTO;
import static com.example.minibuslocal.transmit.Class.HMI.DRIVE_MODEL_AUTO_AWAIT;
import static com.example.minibuslocal.transmit.Class.HMI.DRIVE_MODEL_REMOTE;
import static com.example.minibuslocal.transmit.Class.HMI.Ord_Alam_OFF;
import static com.example.minibuslocal.transmit.Class.HMI.Ord_Alam_ON;
import static com.example.minibuslocal.transmit.Class.HMI.Ord_Alam_POINTLESS;
import static com.example.minibuslocal.transmit.Class.HMI.Ord_SystemRuningStatus_ONINPUT;
import static com.example.minibuslocal.transmit.Class.HMI.POINTLESS;
import static com.example.minibuslocal.transmit.Class.HMI.eBooster_Warning_OFF;


public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    public final static int SEND_TO_FRONTSCREEN = 0;//前风挡
    public final static int SEND_TO_RIGHTSCREEN = 1;//右车门
    public final static int SEND_TO_LEFTSCREEN = 2;//左车门
    public final static int SEND_TO_LOCALHOST = 3;//主控屏
    public final static int SEND_TO_SCREEN = 4;//发送给前风挡和左右屏
    public final static int LOCALHOST_SCREEN_TOP = 0;//主控屏上部分
    public final static int LOCALHOST_SCREEN_LEFT = 1;//主控屏左边部分
    public final static int LOCALHOST_SCREEN_CENTER = 2;//主控屏中间部分
    public final static int LOCALHOST_SCREEN_RIGHT = 3;//主控屏右边部分
    public final static int LOCALHOST_SCREEN_OTHER = 4;//主控屏其他部分
    private final int MIN_BATTERY = 20;//低电量触发值
    private final int MIN_SPEED = 5;//低速报警值
    private final int REQUEST_CODE = 1;//请求码
    private Context mContext;//上下文
    private FragmentManager fragmentManager;//Fragment管理器对象
    private FragmentTransaction transaction;//Fragment事务对象
    private boolean autoDriveModel = false;//默认关闭自动驾驶模式
    private MainTopFragment topFragment;//上部Fragment(时间、电量)
    private MainLeftFragment leftFragment;//左部Fragment（灯光、）
    private MainCenterFragment centerFragment;//中间部分Fragment(地图)
    private MainRightFragment1 rightFragment1;
    private MainRightFragment2 rightFragment2;//右边Fragment(车速、)
    private MainLowBatteryFragment lowBatteryFragment;//低电量报警
    private RelativeLayout floatBtn;//悬浮按钮
    private Thread canThread;//处理CAN总线的子线程
    private Thread sreialThread;//处理485的子线程
    private SreialComm sreialComm;//串口
    private MusicService.MusicBinder musicBinder;//音乐服务
    private TimerManager timerManager;//定时发送模拟数据（只模拟）
    public static boolean target = false;//默认没跳转
    private int currentDriveModel = DRIVE_MODEL_AUTO_AWAIT;//当前驾驶状态默认为待定
    private int clickDriveModel = DRIVE_MODEL_AUTO_AWAIT;//点击驾驶模式
    private MainRightFragment2.ReadSpeedTimer readSpeedTimer;
    private boolean loginFlag = false;//是否登陆成功
    private int lastStationId = 0;//下一站点id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogUtil.d(TAG, "onCreate");
        mContext = MainActivity.this;
        hideBottomUIMenu();
        boolean isShow = getIntent().getBooleanExtra("isShow", false);
        //初始化布局
        viewInit(isShow);
        if (!isShow) {//非锁屏状态
            //初始化相关类
            classInit();
            //申请相关权限
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
                }, 1);
            } else {
                //有权限的话什么都不做
                initMusic();
            }
            reboot();//发送初始化数据
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        playMusic();
//        LogUtil.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        LogUtil.d(TAG, "onPause");
        pauseMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //中断CAN总线的子线程
        if (canThread != null) {
            canThread.interrupt();
        }
        //关闭485串口
        if (sreialComm != null) {
            sreialComm.close();
        }
        //中断485线程
        if (sreialThread != null) {
            sreialThread.interrupt();
        }
        //关闭定时读取总里程
        if (readSpeedTimer != null) {
            readSpeedTimer.stopTimer();
        }
        //关闭播放器
        if (musicBinder != null) {
            musicBinder.close();
            unbindService(connection);
        }
        //关闭定时发送
//        if(timerManager != null){
//            timerManager.stopTimer();
//        }
        LogUtil.d(TAG, "onDestroy");
    }

    /**
     * 从另一个页面携带数据跳转至本页面
     *
     * @param mContext
     * @param isShow
     * @param loginFlag
     */
    public static void actionStart(Context mContext, boolean isShow, boolean loginFlag) {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra("isShow", isShow);
        intent.putExtra("flag", loginFlag);
//        Log.d(TAG, "actionStart: " + loginFlag);
        mContext.startActivity(intent);
    }

    /**
     * 初始化相关类
     */
    private void classInit() {
        //打开CAN监听
        canThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Transmit.getInstance().setHandler(mContext, handler);
            }
        });
        canThread.start();
        //打开re485
        sreialThread = new Thread(new Runnable() {
            @Override
            public void run() {
                sreialComm = new SreialComm(handler);
                sreialComm.receive();
            }
        });
//        sreialThread.start();
    }

    /**
     * 播放音乐
     */
    private void initMusic() {
        Intent mediaServiceIntent = new Intent(MainActivity.this, MusicService.class);
        bindService(mediaServiceIntent, connection, BIND_AUTO_CREATE);
        handler.post(new Runnable() {
            @Override
            public void run() {
                JSONObject object = new JSONObject();
                object.put("id", 1);
                object.put("data", 1);
                App.getInstance().setAudioVolume(object);
            }
        });
    }

    /**
     * 播放站点音乐
     */
    private void playStationMusic(final JSONObject object) {
        int id = object.getIntValue("id");
        int data = object.getIntValue("data");
        if (id == HAD_CurrentDrivingRoadIDNum) {//当前行驶路线ID
            musicBinder.setLoRouteNum(data);
        } else if (id == HAD_NextStationIDNumb) {//下一个站点ID
            lastStationId = data;
        } else if (id == HAD_ArrivingSiteRemind) {//到站提醒
            musicBinder.prepareData(data, lastStationId);
        } else if (id == HAD_StartingSitedepartureRemind) {//起始站出发提醒
            if (data == 2) {//起始站出发提醒
                lastStationId = 1;//下一站为1
                musicBinder.prepareData(3, lastStationId);
            }
        }
    }

    /**
     * 播放音乐
     */
    private void playMusic() {
        if (musicBinder != null) {
            musicBinder.play();
        }
    }

    /**
     * 暂停音乐
     */
    private void pauseMusic() {
        if (musicBinder != null) {
            musicBinder.pause();
        }
    }


    /**
     * 初始化控件
     */
    private void viewInit(boolean isShow) {
        floatBtn = (RelativeLayout) findViewById(R.id.floatBtn_layout);
        floatBtn.setOnClickListener(onClickListener);
        //初始化右边Fragment
        fragmentManager = getSupportFragmentManager();
        topFragment = (MainTopFragment) fragmentManager.findFragmentById(R.id.top_fragment);
        leftFragment = (MainLeftFragment) fragmentManager.findFragmentById(R.id.left_fragment);
        centerFragment = (MainCenterFragment) fragmentManager.findFragmentById(R.id.center_fragment);
        lowBatteryFragment = new MainLowBatteryFragment();
        rightFragment1 = new MainRightFragment1();
        rightFragment2 = new MainRightFragment2();
        transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.right_fragment, rightFragment1);//右边
        transaction.add(R.id.right_fragment, rightFragment2).hide(rightFragment2);
        transaction.add(R.id.lowBattery_fragment, lowBatteryFragment).hide(lowBatteryFragment);//加入低电量报警并隐藏
        transaction.commit();
        if (isShow) {//锁屏
            showShadeDialog();
        }
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        if(timerManager != null){
//            timerManager.setPause(false);
//        }
        return super.onTouchEvent(event);
    }

    /**
     * 接收CAN总线的信息，判断处理
     */
    @SuppressLint("HandlerLeak")
    private MyHandler handler = new MyHandler(mContext) {
        @Override
        public void handleMessage(Message msg) {
            JSONObject object = (JSONObject) msg.obj;//CAN总线的数据
            LogUtil.d(TAG, object.toJSONString());
            switch (msg.what) {
                case SEND_TO_FRONTSCREEN: {//前风挡
                    new SendToScreenThread(mContext, object, SEND_TO_FRONTSCREEN).start();
                    playStationMusic(object);
//                    LogUtil.d(TAG, "发送信息给前风挡");
                    break;
                }
                case SEND_TO_RIGHTSCREEN: {//右车门
                    new SendToScreenThread(mContext, object, SEND_TO_RIGHTSCREEN).start();
//                    LogUtil.d(TAG, "发送信息给右车门");
                    break;
                }
                case SEND_TO_LEFTSCREEN: {//左车门
                    new SendToScreenThread(mContext, object, SEND_TO_LEFTSCREEN).start();
                    playStationMusic(object);
//                    centerFragment.refresh(object);
//                    LogUtil.d(TAG, "发送信息给左车门");
                    break;
                }
                case SEND_TO_LOCALHOST: {//主控屏
                    //改变主控屏的控件状态
                    int screenId = whatFragment(object);
                    if (screenId == LOCALHOST_SCREEN_TOP) {//上部Fragment
                        topFragment.refresh(object);
                        int id = object.getIntValue("id");
                        if (id == BMS_SOC) {
                            int battery = object.getIntValue("data");
                            if (battery <= MIN_BATTERY) {//低电量报警
                                showLowBatteryFragment(true);
                            } else {
                                showLowBatteryFragment(false);
                            }
                        }
                    } else if (screenId == LOCALHOST_SCREEN_LEFT) {//左边Fragment
                        leftFragment.refresh(object);
                    } else if (screenId == LOCALHOST_SCREEN_CENTER) {//中间Fragment
                        centerFragment.refresh(object);
                    } else if (screenId == LOCALHOST_SCREEN_RIGHT) {//右边Fragment
                        if (autoDriveModel) {//自动驾驶模式开启即处理数据
                            int id = object.getIntValue("id");
                            if (id == Wheel_Speed_ABS) {//速度
                                int speed = (int) object.getDoubleValue("data");
                                if (speed <= MIN_SPEED) {//低速
                                    //发送低速报警消息
                                    sendToCAN("HMI", HMI_Dig_Ord_Alam, Ord_Alam_ON);
                                } else {
                                    sendToCAN("HMI", HMI_Dig_Ord_Alam, Ord_Alam_OFF);
                                }
                            }
                            rightFragment2.refresh(object);
                        }
                    } else if (screenId == LOCALHOST_SCREEN_OTHER) {
                        refresh(object);
                    }
                    break;
                }
                case SEND_TO_SCREEN: {//发送给前风挡、左右车门
                    new SendToScreenThread(mContext, object, SEND_TO_FRONTSCREEN).start();
                    new SendToScreenThread(mContext, object, SEND_TO_LEFTSCREEN).start();
                    new SendToScreenThread(mContext, object, SEND_TO_RIGHTSCREEN).start();
                    playStationMusic(object);
//                    LogUtil.d(TAG, "都发");
                    break;
                }
                default:
                    break;
            }
        }
    };

    /**
     * 处理主控屏其他命令
     *
     * @param object
     */
    private void refresh(JSONObject object) {
        int id = object.getIntValue("id");
        int data = object.getIntValue("data");
        String msg = "";
        boolean changeSu = false;
        switch (id) {
            case SystemStatus: {//RCU系统运行状态信号
                changeSu = true;
                if (data == 0) {//自动驾驶正常
                    msg = "自动驾驶正常";
                    currentDriveModel = DRIVE_MODEL_AUTO;//当前为自动驾驶
                } else if (data == 1) {//自动驾驶故障
                    msg = "自动驾驶故障";
                    currentDriveModel = DRIVE_MODEL_AUTO;//当前为自动驾驶
                } else if (data == 2) {//远程驾驶正常
                    msg = "远程驾驶正常";
                    currentDriveModel = DRIVE_MODEL_REMOTE;//当前为远程驾驶
                } else if (data == 3) {//远程驾驶故障
                    msg = "远程驾驶故障";
                    currentDriveModel = DRIVE_MODEL_REMOTE;//当前为远程驾驶
                }
                break;
            }
            case OBU_Dig_Ord_SystemStatus: {//OBU系统运行状态信号
                if (data == 0) {
                    msg = "无输入";
                } else if (data == 1) {
                    msg = "OBU系统运行正常";
                } else if (data == 2) {
                    msg = "OBU系统故障";
                } else if (data == 3) {
                    msg = "预留";
                }
                break;
            }
        }
        if (data == 1 || data == 3) {
            ToastUtil.getInstance(mContext).showLengthToast(msg);
        }
        rightFragment1.changeBtnColor(currentDriveModel);
        if (changeSu) {
            autoDriveModel = true;//驾驶模式打开
            showFragment(rightFragment1, false);//切换界面
            showFragment(rightFragment2, true);//切换界面
            floatBtn.setVisibility(View.VISIBLE);//按钮显示
            if (readSpeedTimer == null) {
                readSpeedTimer = rightFragment2.getReadSpeedTimer();
                readSpeedTimer.startTimer();
            }
        }
        LogUtil.d(TAG, msg);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initMusic();
//                    ToastUtil.getInstance(mContext).showShortToast("权限开启成功");
                } else {
                    ActivityCollector.finshAll();
                }
            }
        }
    }

    /**
     *
     */
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicBinder = (MusicService.MusicBinder) service;
            //模拟定时发送
//            timerManager = new TimerManager(handler);
//            timerManager.startTimer();
//            leftFragment.setTimerManager(timerManager);
            LogUtil.d(TAG, "connection-->onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /**
     * 锁屏状态
     */
    private void showShadeDialog() {
        if (canThread != null) {
            canThread.interrupt();
        }
        Dialog dialog = new Dialog(mContext, R.style.activity_translucent);
        dialog.setContentView(R.layout.shade_dialog_layout);
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * 点击事件监听器
     */
    private CustomOnClickListener onClickListener = new CustomOnClickListener(200) {
        @SuppressLint("RestrictedApi")
        @Override
        public void onSingleClick(View v) {
            switch (v.getId()) {
                case R.id.floatBtn_layout: {//悬浮按钮(退出各种模式)
                    showFragment(rightFragment2, false);
                    showFragment(rightFragment1, true);
                    floatBtn.setVisibility(View.INVISIBLE);
                    rightFragment1.changeBtnColor(currentDriveModel);
                    break;
                }
            }
        }
    };


    /**
     * 返回键监听
     */
    @Override
    public void onBackPressed() {
        ActivityCollector.finshAll();
    }

    /**
     * 处理各个Fragment传过来的数据
     *
     * @param clazz 类名
     * @param field 字段名
     * @param o     对象
     */
    public void sendToCAN(final String clazz, final int field, final Object o) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Transmit.getInstance().hostToCAN(clazz, field, o);
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        Log.d(TAG, "requestCode: " + requestCode + "----" + "resultCode:" + resultCode);
        switch (requestCode) {
            case REQUEST_CODE: {
                if (resultCode == RESULT_OK) {//确定
                    Boolean isShow = data.getBooleanExtra("isShow", false);
                    Boolean loginFlag = data.getBooleanExtra("loginFlag", false);
                    if (target) {//如果页面跳转
                        target = false;
                        playMusic();
                        if (loginFlag) {//登陆成功
                            String clazz = "HMI";
                            int field = HMI_Dig_Ord_Driver_model;
                            autoDriveModel = true;//驾驶模式打开
                            Transmit.getInstance().setADAndRCUFlag(true);
                            Log.d(TAG, "onActivityResult: " + clickDriveModel);
                            sendToCAN(clazz, field, clickDriveModel);//发送数据
//                            LogUtil.d(TAG,"登陆成功");
                        } else {//登陆失败
                            if (isShow) {
                                showShadeDialog();
                            }
//                            LogUtil.d(TAG,"登陆失败");
                        }
                    }
                }
                break;
            }
        }
    }

    /**
     * 处理Fragment的消息
     *
     * @param flag 判断哪个按钮
     */
    @SuppressLint("RestrictedApi")
    public void handleFragmentMsg(int flag) {
        if (flag == DRIVE_MODEL_AUTO || flag == DRIVE_MODEL_REMOTE) {
            target = true;//跳转
            clickDriveModel = flag;
            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.putExtra("isFirst", false);
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    /**
     * 替换Fragment
     *
     * @param fragment
     */
    private void replaceFragment(Fragment fragment) {
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.right_fragment, fragment);
        transaction.commit();
    }

    /**
     * 隐藏Fragment
     *
     * @param fragment
     */
    private void showFragment(Fragment fragment, boolean flag) {
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        if (flag) {
            transaction.show(fragment);
        } else {
            transaction.hide(fragment);
        }
        transaction.commitAllowingStateLoss();//不保存状态
    }

    /**
     * 显示和隐藏低电量报警Fragment
     */
    private void showLowBatteryFragment(boolean show) {
        try {
            fragmentManager = getSupportFragmentManager();
            transaction = fragmentManager.beginTransaction();
            if (show) {
                transaction.show(lowBatteryFragment);
            } else {
                transaction.hide(lowBatteryFragment);
            }
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断CAN总线的消息显示在哪个部分
     *
     * @param object
     * @return
     */
    private int whatFragment(JSONObject object) {
        int id = object.getIntValue("id");
        switch (id) {
            //上部Fragment
            case OBU_LocalTime://本地时间
            case BMS_SOC://动力电池剩余电量SOC
            case HAD_GPSPositioningStatus://GPS状态
                return LOCALHOST_SCREEN_TOP;
            //左边Fragment
            case BCM_Flg_Stat_LeftTurningLamp://左转向状态信号
            case BCM_Flg_Stat_RightTurningLamp://右转向状态信号
            case BCM_Flg_Stat_HighBeam://远光灯状态信号
            case BCM_Flg_Stat_LowBeam://近光灯状态信号
            case BCM_Flg_Stat_RearFogLamp://后雾灯状态信号
            case BCM_Flg_Stat_DangerAlarmLamp://危险报警灯控制（双闪）状态信号
            case BCM_ACBlowingLevel://空调风量档位
            case BCM_DemisterStatus://除雾状态
//            case VCU_ACWorkingStatus://空调工作模式信号
            case BCM_InsideTemp://车内温度
//            case BCM_OutsideTemp://车外温度
                return LOCALHOST_SCREEN_LEFT;
            //右边Fragment
            case can_num_PackAverageTemp://电池包平均温度
//            case can_RemainKm://剩余里程数
            case Wheel_Speed_ABS://车速信号
                return LOCALHOST_SCREEN_RIGHT;
//            //中间Fragment
//            case HAD_GPSLongitude://经度
//                return LOCALHOST_SCREEN_CENTER;
            case SystemStatus://RCU主控请求状态反馈
                return LOCALHOST_SCREEN_OTHER;
            default:
                return -1;
        }
    }

    /**
     * 程序启动就发送数据
     */
    private void reboot() {
        final Map<Integer, Integer> map = new HashMap<>();
        map.put(HMI_Dig_Ord_HighBeam, POINTLESS);//远光灯
        map.put(HMI_Dig_Ord_LowBeam, POINTLESS);//近光灯
        map.put(HMI_Dig_Ord_LeftTurningLamp, POINTLESS);//左转向灯
        map.put(HMI_Dig_Ord_RightTurningLamp, POINTLESS);//右转向灯
        map.put(HMI_Dig_Ord_RearFogLamp, POINTLESS);//后雾灯
        map.put(HMI_Dig_Ord_DangerAlarm, POINTLESS);//警示灯
        map.put(HMI_Dig_Ord_air_model, AIR_MODEL_AWAIT);//制冷，制热
        map.put(HMI_Dig_Ord_Alam, Ord_Alam_POINTLESS);//低速报警
        map.put(HMI_Dig_Ord_Driver_model, DRIVE_MODEL_AUTO_AWAIT);//驾驶模式
        map.put(HMI_Dig_Ord_DoorLock, POINTLESS);//门锁控制
        map.put(HMI_Dig_Ord_air_grade, AIR_GRADE_SIX_GEAR);//空调档位
        map.put(HMI_Dig_Ord_eBooster_Warning, eBooster_Warning_OFF);//制动液面报警
        map.put(HMI_Dig_Ord_FANPWM_Control, AIR_GRADE_OFF);//风扇PWM占空比控制信号
        map.put(HMI_Dig_Ord_Demister_Control, POINTLESS);//除雾控制
        map.put(HMI_Dig_Ord_TotalOdmeter, 0);//总里程
        map.put(HMI_Dig_Ord_SystemRuningStatus, Ord_SystemRuningStatus_ONINPUT);//HMI控制器运行状态
        new Thread(new Runnable() {
            @Override
            public void run() {
                Transmit.getInstance().Can_init(map);
            }
        }).start();
        LogUtil.d(TAG, "初始化");
    }
}