package com.example.minibuslocal.transmit.Class;

import com.example.minibuslocal.activity.MainActivity;
import com.example.minibuslocal.bean.IntegerCommand;
import com.example.minibuslocal.util.ByteUtil;
import com.example.minibuslocal.util.LogUtil;

import java.util.HashMap;
import java.util.Map;

import static com.example.minibuslocal.util.ByteUtil.countBits;

public class PCGR1 extends BaseClass {
    private static final String TAG = "PCGR1";
    private HashMap<Integer, MyPair<Integer>> fields = new HashMap<Integer, MyPair<Integer>>(){{
        put(0,new MyPair<>(3, IntegerCommand.PCG_Right_Work_Sts, MainActivity.SEND_TO_SCREEN));
        put(3,new MyPair<>(3, IntegerCommand.PCG_Right_Error_Mode, MainActivity.SEND_TO_LOCALHOST));
        put(6,new MyPair<>(2, IntegerCommand.PCG_Right_Anti_Pinch_Mode, MainActivity.SEND_TO_LOCALHOST));
        put(8,new MyPair<>(8, IntegerCommand.PCG_Right_Open_Count, MainActivity.SEND_TO_LOCALHOST));
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
            case 3:
                return (int) countBits(bytes, 0, index, 3, ByteUtil.Motorola);
            case 6:
                return (int) countBits(bytes, 0, index, 2, ByteUtil.Motorola);
            case 8:
                return countBits(bytes, 0, index, 8, ByteUtil.Motorola);
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
