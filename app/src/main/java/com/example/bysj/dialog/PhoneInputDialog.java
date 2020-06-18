package com.example.bysj.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.bysj.OKHttpUtils;
import com.example.bysj.R;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import okhttp3.ResponseBody;

public class PhoneInputDialog extends Dialog {
    private Button commitBt;
    private Activity activity;
    private EditText newPhoneEd,yzmEd;
    private TextView phoneTv,yzmTv;
    private boolean hasGetPhoneYZM = false;
    private String userType;
    public PhoneInputDialog(@NonNull Context context, Activity activity, TextView phoneTv) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.activity = activity;
        this.phoneTv = phoneTv;
        setContentView(R.layout.phone_input_dialog);
        if(phoneTv.getId()==R.id.user_info_phone_tv){
            userType = "user";
        }else {
            userType = "shop";
        }
        init();
    }

    private void init(){
        commitBt = this.findViewById(R.id.shop_info_dialog_commit);
        newPhoneEd = this.findViewById(R.id.shop_info_dialog_phone_ed);
        yzmEd = this.findViewById(R.id.shop_info_dialog_yzm_ed);
        yzmTv = this.findViewById(R.id.shop_info_dialog_yzm_tv);
        initCommitBt();
        initGetYZM();
    }

    private void initCommitBt(){
        commitBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final WaitDialog waitDialog = new WaitDialog(activity);
                waitDialog.show();
                final String code = yzmEd.getText().toString();
                final String newPhone = newPhoneEd.getText().toString();
                final String oldPhone = activity.getSharedPreferences("info",Context.MODE_PRIVATE).getString("phone","-");

                if(code.equals("")){
                    Toast.makeText(activity,"验证码不能为空",Toast.LENGTH_SHORT).show();
                    waitDialog.dismiss();
                    return;
                }
                if(newPhone.equals("")){
                    Toast.makeText(activity,"新手机号码不能为空",Toast.LENGTH_SHORT).show();
                    waitDialog.dismiss();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bmob.initialize(activity,"0baa46673f2a4ff96fb1a259146e8f30");
                        boolean i = false;
                        try {
                            i = OKHttpUtils.verifySMS(oldPhone,code,activity);
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                            Looper.prepare();
                            Toast.makeText(activity,"连接超时",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                        if(!i){
                            waitDialog.dismiss();
                            Looper.prepare();
                            Toast.makeText(activity,"验证码错误",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }else {
                            ResponseBody responseBody = null;
                            try {
                                responseBody = OKHttpUtils.postMessage("/"+userType+"/info/changePhone.do","{\"newPhone\":"+"\""+newPhone+"\"}");
                            } catch (IOException e) {
                                e.printStackTrace();
                                waitDialog.dismiss();
                                Looper.prepare();
                                Toast.makeText(activity,"连接超时",Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                            if(responseBody==null){
                                waitDialog.dismiss();
                                Looper.prepare();
                                Toast.makeText(activity,"修改失败",Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                            String re = "-1";
                            try {
                                re = responseBody.string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if(re.equals("-1")){
                                waitDialog.dismiss();
                                Looper.prepare();
                                Toast.makeText(activity,"修改失败",Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                            else {
                                waitDialog.dismiss();
                                PhoneInputDialog.this.dismiss();
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        phoneTv.setText(newPhone);
                                    }
                                });
                                SharedPreferences sharedPreferences = activity.getSharedPreferences("info",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("phone",newPhone);
                                editor.apply();
                                Looper.prepare();
                                Toast.makeText(activity,"修改成功",Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        }
                    }
                }).start();
            }
        });
    }

    private void initGetYZM(){
        yzmTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasGetPhoneYZM){
                    return;
                }
                final String phone = activity.getSharedPreferences("info",Context.MODE_PRIVATE).getString("phone","-1");
//                if(phone.equals(phoneTv.getText().toString())){
//                    Toast.makeText(activity,"该手机号码与现在号码相同",Toast.LENGTH_SHORT).show();
//                    return;
//                }
                if(phone.equals("")||phone.length()!=11){
                    Toast.makeText(activity,"输入正确的手机号",Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        Bmob.initialize(activity,"0baa46673f2a4ff96fb1a259146e8f30");
                        OKHttpUtils.sendSMS(phone,activity);
                        hasGetPhoneYZM = true;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                yzmTv.setTextColor(Color.GRAY);
                            }
                        });

                        for(int i = 5; i>0; i--){
                            try {
                                final String second = i+"s";
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        yzmTv.setText(second);
                                    }
                                });

                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                yzmTv.setTextColor(Color.parseColor("#FF6600"));
                                yzmTv.setText("获取验证码");
                            }
                        });

                        hasGetPhoneYZM = false;
                        Looper.loop();
                    }
                }).start();
            }
        });
    }
}
