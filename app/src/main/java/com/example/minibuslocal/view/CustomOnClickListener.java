package com.example.minibuslocal.view;

import android.view.View;

public abstract class CustomOnClickListener implements View.OnClickListener {
    private long lastClickTime;//上一次点击时间
    private long basicTime = 1000L;//基本时间

    public CustomOnClickListener(long basicTime){
        this.basicTime = basicTime;
    }

    @Override
    public void onClick(View v) {
        long clickTime = System.currentTimeMillis();//点击时间戳
        if(clickTime - lastClickTime > basicTime){//两次间隔大于1000
            onSingleClick(v);
            lastClickTime = clickTime;
        }
    }

    protected abstract void onSingleClick(View v);
}
