package com.example.minibuslocal.transmit.Class;


import com.example.minibuslocal.activity.MainActivity;
import com.example.minibuslocal.bean.IntegerCommand;
import com.example.minibuslocal.util.ByteUtil;
import com.example.minibuslocal.util.LogUtil;

import java.util.HashMap;
import java.util.Map;

import static com.example.minibuslocal.util.ByteUtil.countBits;


public class HAD6 extends BaseClass {
    private static final String TAG = "HAD6";
    private HashMap<Integer, MyPair<Integer>> fields = new HashMap<Integer, MyPair<Integer>>(){{
        put(0,new MyPair<>(2, IntegerCommand.HAD_PedestrianAvoidanceRemind, MainActivity.SEND_TO_FRONTSCREEN));
        put(2,new MyPair<>(2, IntegerCommand.HAD_EmergencyParkingRemind, MainActivity.SEND_TO_FRONTSCREEN));
        put(4,new MyPair<>(2, IntegerCommand.HAD_StartingSitedepartureRemind, MainActivity.SEND_TO_FRONTSCREEN));
        put(6,new MyPair<>(2, IntegerCommand.HAD_ArrivingSiteRemind, MainActivity.SEND_TO_SCREEN));
    }};
    private byte[] bytes = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    @Override
    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public String getTAG() {
        return TAG;
    }

    @Override
    public void setBytes(byte[] bytes) {
        super.setBytes(bytes);
    }

    @Override
    public Object getValue(Map.Entry<Integer, MyPair<Integer>> entry, byte[] bytes) {
        int index = entry.getKey();
        switch (index) {
            case 0:
            case 2:
            case 4:
            case 6:
                return (int) countBits(bytes, 0, index, 2, ByteUtil.Motorola);
            default:
                LogUtil.d(TAG, "数据下标错误");
        }
        return null;
    }

    @Override
    public int getState() {
        return ByteUtil.Motorola;
    }

    @Override
    public HashMap<Integer, MyPair<Integer>> getFields() {
        return fields;
    }
}
