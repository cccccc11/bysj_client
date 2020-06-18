package com.example.bysj.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.bysj.OKHttpUtils;
import com.example.bysj.R;

import java.io.IOException;

import okhttp3.ResponseBody;

public class SexInputDialog extends Dialog {

    private Activity activity;
    private TextView sexTv;
    private RadioButton maleRb,femaleRb;
    private Button commitBtn;
    private String userType;
    public SexInputDialog(@NonNull Context context, Activity activity, TextView sexTv) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sex_input_dialog);
        this.activity = activity;
        this.sexTv = sexTv;
        if(sexTv.getId()==R.id.user_info_sex_tv){
            userType = "user";
        }else {
            userType = "shop";
        }
        init();

    }

    private void init(){
        maleRb = findViewById(R.id.sex_dialog_male_rb);
        femaleRb = findViewById(R.id.sex_dialog_female_rb);
        commitBtn = findViewById(R.id.sex_dialog_commit_btn);
        initRb();
        initBtn();
    }

    private void initRb(){
        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (buttonView.getId()){
                    case R.id.sex_dialog_male_rb:{
                        if(isChecked)
                        {
                            maleRb.setTextColor(Color.WHITE);

                            maleRb.setBackground(activity.getDrawable(R.drawable.sex_checked));
                        }else {
                            maleRb.setTextColor(Color.parseColor("#ff6600"));
                            maleRb.setBackground(activity.getDrawable(R.drawable.sex_uncheck));
                        }
                        break;
                    }
                    case R.id.sex_dialog_female_rb:{
                        if(isChecked)
                        {
                            femaleRb.setTextColor(Color.WHITE);
                            femaleRb.setBackground(activity.getDrawable(R.drawable.sex_checked));
                        }else {
                            femaleRb.setTextColor(Color.parseColor("#ff6600"));
                            femaleRb.setBackground(activity.getDrawable(R.drawable.sex_uncheck));
                        }
                        break;
                    }
                }
            }
        };
        maleRb.setOnCheckedChangeListener(onCheckedChangeListener);
        femaleRb.setOnCheckedChangeListener(onCheckedChangeListener);
        femaleRb.setChecked(true);
    }

    private void initBtn(){
        commitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final WaitDialog waitDialog = new WaitDialog(activity);
                waitDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Looper.prepare();
                        ResponseBody responseBody = null;
                        if(femaleRb.isChecked()){
                            try {
                                responseBody = OKHttpUtils.postMessage("/user/info/changeSex.do","{\"newSex\":\"female\"}");

                            } catch (IOException e) {
                                Toast.makeText(activity,"连接超时",Toast.LENGTH_SHORT).show();
                                waitDialog.dismiss();
                                e.printStackTrace();
                                return;
                            }
                            try {
                                if(responseBody!=null&&responseBody.string().equals("1")){
                                    Toast.makeText(activity,"修改成功",Toast.LENGTH_SHORT).show();
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            sexTv.setText("女");
                                        }
                                    });
                                    waitDialog.dismiss();
                                }else {
                                    Toast.makeText(activity,"修改失败",Toast.LENGTH_SHORT).show();
                                    waitDialog.dismiss();
                                    return;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }else {
                            try {
                                responseBody = OKHttpUtils.postMessage("/user/info/changeSex.do","{\"newSex\":\"male\"}");
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(activity,"连接超时",Toast.LENGTH_SHORT).show();
                                waitDialog.dismiss();
                                return;
                            }
                            try {
                                if(responseBody!=null&&responseBody.string().equals("1")){
                                    Toast.makeText(activity,"修改成功",Toast.LENGTH_SHORT).show();
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            sexTv.setText("男");
                                        }
                                    });
                                }else {
                                    Toast.makeText(activity,"修改失败",Toast.LENGTH_SHORT).show();
                                    waitDialog.dismiss();
                                    return;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                        waitDialog.dismiss();
                        SexInputDialog.this.dismiss();
                        Looper.loop();
                    }
                }).start();

            }
        });
    }
}
