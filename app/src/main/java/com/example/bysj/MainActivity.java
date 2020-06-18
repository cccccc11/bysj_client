package com.example.bysj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //判断是否已经登录

        hasLogin();

    }

    private void hasLogin()
    {
        //存储登录信息
        final SharedPreferences sharedPreferences = getSharedPreferences("info", Context.MODE_PRIVATE);
        if(sharedPreferences.contains("id")){
            if(sharedPreferences.getString("type","none").equals("shop"))
            {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        JSONObject json = new JSONObject();
                        json.put("username",sharedPreferences.getString("id",""));
                        json.put("password",sharedPreferences.getString("password",""));
                        json.put("type","shop");
                        String re ="";
                        try {
                            re = HttpUtils.sendPostMessage("/login/login.do",json.toJSONString());
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this,"连接超时",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        JSONObject object = JSON.parseObject(re);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("name",object.getString("name"));
                        editor.putString("phone",object.getString("phone"));
                        editor.putString("address",object.getString("address"));
                        editor.putString("headURL",object.getString("headURL"));
                        editor.apply();
                        Intent it = new Intent(MainActivity.this,Shop.class);
                        startActivity(it);
                        MainActivity.this.finish();
                        Looper.loop();
                    }
                }).start();
            }
            else {
                //转向user页面
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        JSONObject json = new JSONObject();
                        json.put("username",sharedPreferences.getString("id",""));
                        json.put("password",sharedPreferences.getString("password",""));
                        json.put("type","user");
                        String re ="";
                        try {
                            re = HttpUtils.sendPostMessage("/login/login.do",json.toJSONString());
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this,"连接超时",Toast.LENGTH_SHORT).show();
                        }
                        JSONObject object = JSON.parseObject(re);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("name",object.getString("name"));
                        editor.putString("phone",object.getString("phone"));
                        editor.putString("address",object.getString("address"));
                        editor.putString("headURL",object.getString("headURL"));
                        editor.putString("sex",object.getString("sex"));
                        editor.putString("birthdayYear",object.getString("birthdayYear"));
                        editor.putString("birthdayMonth",object.getString("birthdayMonth"));
                        editor.putString("birthdayDay",object.getString("birthdayDay"));
                        editor.apply();
                        Intent it = new Intent(MainActivity.this,User.class);
                        startActivity(it);
                        MainActivity.this.finish();
                        Looper.loop();
                    }
                }).start();
            }
        }
        else {
            //没有记录则进入登陆界面
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Intent it = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(it);
                    MainActivity.this.finish();
                }
            }).start();
        }

    }
}
