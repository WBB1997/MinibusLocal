package com.example.minibuslocal.transmit.Class;

import com.example.minibuslocal.activity.MainActivity;
import com.example.minibuslocal.bean.IntegerCommand;
import com.example.minibuslocal.util.ByteUtil;
import com.example.minibuslocal.util.LogUtil;

import java.util.HashMap;
import java.util.Map;

import static com.example.minibuslocal.util.ByteUtil.countBits;

public class MCU1 extends BaseClass {
    private static final String TAG = "MCU1";
    private HashMap<Integer, MyPair<Integer>> fields = new HashMap<Integer, MyPair<Integer>>() {{
        put(48, new MyPair<>(16, IntegerCommand.can_num_MotSpeed, MainActivity.SEND_TO_LOCALHOST)); // 电机当前转速
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
            case 48:
                return countBits(bytes, 0, index, 16, ByteUtil.Intel);
            default:
                LogUtil.d(TAG, "数据下标错误");
                break;
        }
        return null;
    }

    @Override
    public int getState() {
        return ByteUtil.Intel;
    }

    @Override
    public HashMap<Integer, MyPair<Integer>> getFields() {
        return fields;
    }
}
