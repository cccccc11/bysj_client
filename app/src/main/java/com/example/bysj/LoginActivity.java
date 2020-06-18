package com.example.bysj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.bmob.v3.Bmob;


public class LoginActivity extends AppCompatActivity {

    Button loginButton;
    EditText usernameEd;
    EditText passwordEd;
    TextView registerEd;
    TextView userType,forgetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    ResponseBody response = OKHttpUtils.postMessage("/login/test.do","hello world");
//                    String re = response.string();
//                    Log.v(re,re);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
        //初始化
        init();



        //OKHttpUtils.sendSMS("15185952174",this);



    }



    private void init()
    {
        forgetPassword = findViewById(R.id.mainForgetPassword);
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(LoginActivity.this,Activity_forget_password.class);
                String userTypeStr = userType.getText().toString();
                int i = userTypeStr.equals("团长登录")?0:1;  //0居民 1团长
                it.putExtra("userType",i);
                startActivity(it);
            }
        });
        //1.初始化用户名和密码
        usernameEdInit();
        passwordEdInit();

        //2.初始化登录按钮
        loginBtnInit();

        //3.初始化登录用户类型转换
        userTypeInit();

        //4.注册功能初始化
        registerInit();

        Bmob.initialize(this, "0baa46673f2a4ff96fb1a259146e8f30");
    }

//    private void permission(){
//        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            // Should we show an explanation?
//            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                // Explain to the user why we need to read the contacts
//            }
//            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
//        }
//    }

    private void usernameEdInit()
    {
        usernameEd = findViewById(R.id.loginUsername);
    }

    private void passwordEdInit()
    {
        passwordEd = findViewById(R.id.loginPassword);
    }

    private void loginBtnInit()
    {
        loginButton = findViewById(R.id.mainLogin);
        loginButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = usernameEd.getText().toString();
                final String password = passwordEd.getText().toString();
                if (username.equals("") || password.equals("")) {
                    Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(username.length()<7)
                {
                    Toast.makeText(LoginActivity.this,"输入的用户名长度小于7",Toast.LENGTH_SHORT).show();
                    return;
                }
                //Toast.makeText(MainActivity.this, username + "  " + password, Toast.LENGTH_SHORT).show();

                //判断用户还是团长进入不同activity
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject json = new JSONObject();
                        String re = null;
                        try {
                            json.put("username",username);
                            json.put("password",password);
                            if(userType.getText().toString().equals("团长登录")){
                                json.put("type","user");//
                            }
                            else {
                                json.put("type","shop");
                            }
                            re= HttpUtils.sendPostMessage("/login/login.do",json.toString());
                        }catch (Exception e) {
                            Looper.prepare();
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "连接超时",Toast.LENGTH_LONG).show();
                            Looper.loop();
                            return;
                        }

                        JSONObject object = JSON.parseObject(re);
                        if(object.getString("result").equals("-1")){
                            Looper.prepare();
                            Toast.makeText(LoginActivity.this, "用户名或密码错误",Toast.LENGTH_LONG).show();
                            Looper.loop();
                        }
                        //进入团长页面
                        if(!userType.getText().toString().equals("团长登录")){


                            //存储登录信息
                            SharedPreferences sharedPreferences = getSharedPreferences("info", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
                            editor.putString("id", username);
                            editor.putString("password",password);
                            editor.putString("type","shop");
                            editor.putString("name",object.getString("name"));
                            editor.putString("phone",object.getString("phone"));
                            editor.putString("address",object.getString("address"));
                            editor.putString("headURL",object.getString("headURL"));
                            editor.apply();//提交修改


                            Intent it = new Intent(LoginActivity.this,Shop.class);
                            startActivity(it);
                            LoginActivity.this.finish();
                        }
                        //进入用户页面
                        else {
                            //存储登录信息
                            SharedPreferences sharedPreferences = getSharedPreferences("info", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
                            editor.putString("id", username);
                            editor.putString("password",password);
                            editor.putString("type","user");
                            editor.putString("name",object.getString("name"));
                            editor.putString("phone",object.getString("phone"));
                            editor.putString("address",object.getString("address"));
                            editor.putString("headURL",object.getString("headURL"));
                            editor.putString("sex",object.getString("sex"));
                            editor.putString("birthdayYear", String.valueOf(object.getIntValue("birthdayYear")));
                            editor.putString("birthdayMonth", String.valueOf(object.getIntValue("birthdayMonth")));
                            editor.putString("birthdayDay", String.valueOf(object.getIntValue("birthdayDay")));
                            editor.putString("birthday",object.getString("birthday"));
                            editor.apply();//提交修改


                            Intent it = new Intent(LoginActivity.this,User.class);
                            startActivity(it);
                            LoginActivity.this.finish();
                        }
                    }
                }).start();

            }
        }));
    }

    private void userTypeInit()
    {
        userType = findViewById(R.id.userType);
        userType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userType.getText().equals("团长登录"))
                {
                    //Toast.makeText(MainActivity.this,"点击了团长登录",Toast.LENGTH_SHORT).show();
                    userType.setText("用户登录");
                    usernameEd.setHint("输入团长名");
                    Drawable drawable= getResources().getDrawable(R.mipmap.shop);
                    usernameEd.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable,null,null,null);
                }
                else if(userType.getText().equals("用户登录"))
                {
                    //Toast.makeText(MainActivity.this,"点击了用户登录",Toast.LENGTH_SHORT).show();
                    userType.setText("团长登录");
                    usernameEd.setHint("输入用户名");
                    Drawable drawable= getResources().getDrawable(R.mipmap.username);
                    usernameEd.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable,null,null,null);
                }
            }
        });
    }

    private void registerInit()
    {
        registerEd = findViewById(R.id.mainRegister);
        registerEd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,Register.class);
                startActivity(intent);
            }
        });
    }
}
