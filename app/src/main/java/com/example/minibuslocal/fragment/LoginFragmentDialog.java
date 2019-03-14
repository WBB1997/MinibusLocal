package com.example.minibuslocal.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.example.minibuslocal.R;

/**
 * Created by fangju on 2019/1/25
 */
public class LoginFragmentDialog extends DialogFragment {
    public static final int ERROR1 = 1;//用户名或密码错误提示
    public static final int ERROR2 = 2;//5次出错
    private TextView textView1;
    private TextView textView2;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景为透明
        View view = inflater.inflate(R.layout.login_fragment_dialog,container,false);
        textView1 = (TextView)view.findViewById(R.id.login_error_dialog_tv1);
        textView2 = (TextView)view.findViewById(R.id.login_error_dialog_tv2);
        refresh(getArguments().getInt("key"));
        return view;
    }

    /**
     * 更新
     */
    public void refresh(int flag){
        if(flag == ERROR1){
            textView1.setText("您输入的密码错误");
            textView2.setText("请重新输入");
        }else if(flag == ERROR2){
            textView1.setText("您已输入5次错误密码");
            textView2.setText("即将进入锁屏模式");
        }
    }
}
