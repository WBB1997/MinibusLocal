package com.example.minibuslocal.transmit.Class;

import com.alibaba.fastjson.JSONObject;
import com.example.minibuslocal.transmit.Transmit;
import com.example.minibuslocal.util.ByteUtil;

import java.util.HashMap;
import java.util.Map;

import static com.example.minibuslocal.util.ByteUtil.countBits;

public abstract class BaseClass {
    private boolean flag = true;
    public abstract byte[] getBytes();
    public abstract String getTAG();
    public void setFlag(boolean flag){
        this.flag = flag;
    }
    public void setBytes(byte[] bytes){
        String TAG = getTAG();
        int state = getState();
        byte[] Local_bytes = getBytes();

//        LogUtil.d(TAG, "bytes:" + bytesToHex(bytes));
//        LogUtil.d(TAG, "this.bytes:" + bytesToHex(Local_bytes));
        // 如果相同则直接返回
//        if(Arrays.equals(bytes,Local_bytes)) {
//            LogUtil.d(TAG, "数据相同");
//            return;
//        }
        int index;
        int length;
//        LogUtil.d(TAG,ByteUtil.bytesToHex(bytes));
        for (Map.Entry<Integer, MyPair<Integer>> entry : getFields().entrySet()) {
            index = entry.getKey();
            length = entry.getValue().getLength();
//            flag = false;
//            for(int i = index; i < index + length; i++) {
//                if(i / 8 >= Local_bytes.length || i / 8 >= bytes.length){
//                    LogUtil.d(TAG, "信号异常");
//                    return;
//                }
//                if (viewBinary(Local_bytes[i / 8], i % 8) != viewBinary(bytes[i / 8], i % 8))
//                    flag = true;
//            }

            if(flag ||(countBits(Local_bytes,0,index,length,state) != countBits(bytes,0,index,length,state))){
                JSONObject jsonObject = new JSONObject();
                // id
                jsonObject.put("id", entry.getValue().getSecond().first);
                // data
                jsonObject.put("data", getValue(entry, bytes));
                // target
                int target = entry.getValue().getSecond().second;
                // 发回主函数
                Transmit.getInstance().callback(jsonObject, target);
                // debug
//                    LogUtil.d(TAG, jsonObject.toJSONString());
            }
        }
        flag = false;
        System.arraycopy(bytes, 0, getBytes(), 0, bytes.length);
//        LogUtil.d(TAG, "this.bytes:" + bytesToHex(getBytes()));
    }
    public abstract Object getValue(Map.Entry<Integer, MyPair<Integer>> entry, byte[] bytes);
    public abstract HashMap<Integer, MyPair<Integer>> getFields();
    public abstract int getState();
    void setBytes(int Byte_offset, int bit_index, boolean changed){
        ByteUtil.setBit(getBytes(), Byte_offset, bit_index, changed);
    }
}
