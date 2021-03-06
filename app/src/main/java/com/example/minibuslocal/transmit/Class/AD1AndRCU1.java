package com.example.minibuslocal.transmit.Class;

import com.example.minibuslocal.activity.MainActivity;
import com.example.minibuslocal.bean.IntegerCommand;
import com.example.minibuslocal.util.ByteUtil;
import com.example.minibuslocal.util.LogUtil;

import java.util.HashMap;
import java.util.Map;

import static com.example.minibuslocal.util.ByteUtil.countBits;

public class AD1AndRCU1 extends BaseClass {
    private static final String TAG = "AD1AndRCU1";
    private HashMap<Integer, MyPair<Integer>> fields = new HashMap<Integer, MyPair<Integer>>() {{
        put(56, new MyPair<>(2, IntegerCommand.SystemStatus, MainActivity.SEND_TO_LOCALHOST)); // EPS警告灯
//        put(56, new MyPair<>(2, IntegerCommand.HAD_Dig_Ord_SystemStatus, MainActivity.SEND_TO_LOCALHOST)); // EPS警告灯
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
            case 56:
                return (int) countBits(bytes, 0, index, 2, ByteUtil.Motorola);
//            case 57:
//                return (int) countBits(bytes, 0, index, 2, ByteUtil.Motorola);
            default:
                LogUtil.d(TAG, "数据下标错误");
                break;
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
