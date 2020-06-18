package com.example.bysj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import cn.bmob.v3.Bmob;
import okhttp3.ResponseBody;

public class Activity_forget_password extends AppCompatActivity {

    ImageView exit;
    RelativeLayout inputRl,yzmRl,passwordRl;
    Button usernameBt,yzmBt,passwordBt;
    EditText usernameEd,yzmEd,psdEd,psdAgainEd;
    TextView yzmTv,alterTv;
    String phone,username;
    int userType;
    boolean hasGetPhoneYZM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        Intent it = getIntent();
        userType = it.getIntExtra("userType",-1);
        init();
    }

    private void init(){
        inputRl = findViewById(R.id.forget_password_input_username_rl);
        yzmRl = findViewById(R.id.forget_password_yzm_rl);
        passwordRl = findViewById(R.id.forget_password_new_password_rl);
        usernameBt = findViewById(R.id.forget_password_username_commit_btn);
        yzmBt = findViewById(R.id.forget_password_password_yzm_commit);
        passwordBt = findViewById(R.id.forget_password_password_commit_btn);
        usernameEd = findViewById(R.id.forget_password_username_ed);
        yzmEd = findViewById(R.id.forget_password_yzm_ed);
        psdEd = findViewById(R.id.forget_password_new_password);
        psdAgainEd = findViewById(R.id.forget_password_again_password);
        yzmTv = findViewById(R.id.forget_password_yzm);
        alterTv = findViewById(R.id.forget_password_phone_alter_tv);
        exit = findViewById(R.id.forget_password_exit);

        inputInit();
        yzmInit();
        psdInit();
        exitInit();
    }

    private void inputInit(){
        usernameBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameEd.getText().toString();
                if(username.equals("")){
                    Toast.makeText(Activity_forget_password.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(username.length()<7||username.length()>32){
                    Toast.makeText(Activity_forget_password.this,"用户名长度为7~32",Toast.LENGTH_SHORT).show();
                    return;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ResponseBody responseBody = null;
                        JSONObject requestJSON = new JSONObject();
                        requestJSON.put("username",username);
                        requestJSON.put("userType",userType);
                        try {
                            responseBody = OKHttpUtils.postMessage("/login/forget_password_getPhone",requestJSON.toJSONString());
                        } catch (IOException e) {
                            e.printStackTrace();
                            Looper.prepare();
                            Toast.makeText(Activity_forget_password.this,"连接超时",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                        String resultStr = null;
                        try {
                            resultStr = responseBody.string();
                        } catch (IOException e) {
                            Looper.prepare();
                            Toast.makeText(Activity_forget_password.this,"连接超时",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                        phone = resultStr;
                        if(phone.equals("-1")){
                            Looper.prepare();
                            Toast.makeText(Activity_forget_password.this,"没有该用户",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                        Activity_forget_password.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                alterTv.setText("你的手机号码为"+phone.charAt(0)+phone.charAt(1)+phone.charAt(2)+"xxxx"+
                                        phone.charAt(7)+phone.charAt(8)+phone.charAt(9)+phone.charAt(10)+",点击获取验证码");
                                inputRl.setVisibility(View.INVISIBLE);
                                yzmRl.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }).start();

            }
        });
    }

    private void yzmInit(){
        hasGetPhoneYZM = false;
        yzmTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasGetPhoneYZM){
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bmob.initialize(Activity_forget_password.this,"0baa46673f2a4ff96fb1a259146e8f30");
                        OKHttpUtils.sendSMS(phone,Activity_forget_password.this);
                        hasGetPhoneYZM = true;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                yzmTv.setTextColor(Color.GRAY);

                            }
                        });
                        for(int i = 5; i>0; i--){
                            try {
                                final String second = i+"s";
                                runOnUiThread(new Runnable() {
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                yzmTv.setTextColor(Color.parseColor("#FF6600"));
                                yzmTv.setText("获取验证码");
                            }
                        });
                        hasGetPhoneYZM = false;
                    }
                }).start();
            }
        });
        yzmBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String yzm = yzmEd.getText().toString();
                if(yzm.equals("")){
                    Looper.prepare();
                    Toast.makeText(Activity_forget_password.this,"验证码不能为空",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(!OKHttpUtils.verifySMS(phone,yzm,Activity_forget_password.this)){
                                Looper.prepare();
                                Toast.makeText(Activity_forget_password.this,"验证码错误",Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                        Activity_forget_password.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                yzmRl.setVisibility(View.INVISIBLE);
                                passwordRl.setVisibility(View.VISIBLE);
                            }
                        });

                    }
                }).start();
            }
        });
    }

    private void psdInit(){
        passwordBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String psd,psd1;
                psd = psdEd.getText().toString();
                psd1 = psdAgainEd.getText().toString();
                if(psd.equals("")){
                    Looper.prepare();
                    Toast.makeText(Activity_forget_password.this,"新密码不能为空",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                if(psd.length()<7||psd.length()>32){
                    Looper.prepare();
                    Toast.makeText(Activity_forget_password.this,"密码长度为7~32",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                if(!psd.equals(psd1)){
                    Looper.prepare();
                    Toast.makeText(Activity_forget_password.this,"两次密码不能为空",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }



                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ResponseBody responseBody = null;
                        JSONObject requestJSON = new JSONObject();
                        requestJSON.put("username",username);
                        requestJSON.put("password",psd);
                        requestJSON.put("userType",userType);
                        try {
                            responseBody = OKHttpUtils.postMessage("/login/changePassword",requestJSON.toJSONString());
                        } catch (IOException e) {
                            Looper.prepare();
                            Toast.makeText(Activity_forget_password.this,"连接超时",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                        String resultStr = null;
                        try {
                            resultStr = responseBody.string();
                        } catch (IOException e) {
                            Looper.prepare();
                            Toast.makeText(Activity_forget_password.this,"连接超时",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                        if(resultStr.equals("1")){
                            Looper.prepare();
                            Toast.makeText(Activity_forget_password.this,"修改成功，即将返回上一界面",Toast.LENGTH_SHORT).show();
                            new Handler(new Handler.Callback() {
                                @Override
                                public boolean handleMessage(Message msg) {
                                    Intent it = new Intent(Activity_forget_password.this, LoginActivity.class);
                                    startActivity(it);
                                    Activity_forget_password.this.finish();
                                    return false;
                                }
                            }).sendEmptyMessageDelayed(0x123,2000);//延时跳转
                            Looper.loop();
                        }
                        else {
                            Looper.prepare();
                            Toast.makeText(Activity_forget_password.this,"修改失败",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }).start();

            }
        });
    }

    private void exitInit(){
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_forget_password.this.finish();
            }
        });
    }

}
