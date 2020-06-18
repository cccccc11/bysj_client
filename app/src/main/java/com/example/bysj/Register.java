package com.example.bysj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


import java.io.IOException;

import cn.bmob.v3.Bmob;

public class Register extends AppCompatActivity {

    ImageView yzmIV,backIv;
    Button commitBtn;
    EditText usernameEd,passwordEd,passwordAgainEd,YZMEd,phoneEd;
    TextView usernameAlterTv,passwordAlterTv,passwordAgainAlterTv,phoneYZMTv;
    Context context = this;
    //RadioGroup typeRg;
    RadioButton userRb,shopRb;
    boolean hasGetPhoneYZM = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();

    }

    private void init()
    {
        //获取控件
        yzmIV = findViewById(R.id.register_yzm_iv);
        passwordEd = findViewById(R.id.register_password_ed);
        passwordAgainEd = findViewById(R.id.register_passwordAgain_ed);
        commitBtn = findViewById(R.id.register_commit);
        usernameAlterTv = findViewById(R.id.register_username_alert);
        passwordAlterTv = findViewById(R.id.register_password_alert);
        passwordAgainAlterTv = findViewById(R.id.register_passwordAgain_alert);
        YZMEd = findViewById(R.id.register_yzm_ed);
        backIv = findViewById(R.id.register_back);
        //typeRg = findViewById(R.id.register_type_rg);
        userRb = findViewById(R.id.register_type_user_rb);
        shopRb = findViewById(R.id.register_type_shop_rb);
        phoneYZMTv = findViewById(R.id.register_getPhoneYZM_tv);
        phoneEd = findViewById(R.id.register_phone_ed);

        //1.用户名初始化
        initUsername();

       // initYZM();
        initPassword();
        commitInit();
        initBack();
        initRadio();
        initPhone();
    }

    //radio初始化
    private void initRadio(){
        userRb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            userRb.setTextColor(Color.WHITE);
                        }
                    });

                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            userRb.setTextColor(Color.parseColor("#FF6600"));
                        }
                    });
                }
                usernameEd.setText("");
            }
        });
        shopRb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            shopRb.setTextColor(Color.WHITE);
                        }
                    });

                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            shopRb.setTextColor(Color.parseColor("#FF6600"));
                        }
                    });
                }
            }
        });
        usernameEd.setText("");
    }

    //初始化返回按钮
    private void initBack()
    {
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Register.this, LoginActivity.class);
                startActivity(it);
            }
        });
    }

    //获取手机验证码初始化
    private void initPhone(){
        phoneYZMTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasGetPhoneYZM){
                    return;
                }
                final String phone = phoneEd.getText().toString();
                if(phone.equals("")||phone.length()!=11){
                    Toast.makeText(context,"输入正确的手机号",Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bmob.initialize(Register.this,"0baa46673f2a4ff96fb1a259146e8f30");
                        OKHttpUtils.sendSMS(phone,Register.this);
                        hasGetPhoneYZM = true;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                phoneYZMTv.setTextColor(Color.GRAY);

                            }
                        });
                                for(int i = 5; i>0; i--){
                                    try {
                                        final String second = i+"s";
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                phoneYZMTv.setText(second);
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
                                        phoneYZMTv.setTextColor(Color.parseColor("#FF6600"));
                                        phoneYZMTv.setText("获取验证码");
                                    }
                                });
                                hasGetPhoneYZM = false;
                    }
                }).start();
            }
        });
    }

    //初始化用户名
    private void initUsername()
    {
        usernameEd = findViewById(R.id.register_newUserName_ed);
        usernameEd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    usernameAlterTv.setText("");
                }
                else
                {
                    final String username = usernameEd.getText().toString();
                    if(username.equals(""))
                    {
                        usernameAlterTv.setText("用户名不能为空");
                    }
                    else if(username.length()<7)
                    {
                        usernameAlterTv.setText("用户名长度不能小于7位");
                    }
                    else if(username.length()>16)
                    {
                        usernameAlterTv.setText("用户名长度不能大于16位");
                    }
                    else
                    {
                        //判断用户名是否存在
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if(userRb.isChecked())
                                {
                                    final boolean has = hasUsername(username);
                                    if(has)
                                    {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                usernameAlterTv.setText("用户名已存在");
                                            }
                                        });
                                    }
                                }else {
                                    final boolean has = hasShopName(username);
                                    if(has)
                                    {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                usernameAlterTv.setText("团长名已存在");
                                            }
                                        });
                                    }
                                }

                            }
                        }).start();
                    }
                }
            }
        });
    }

    //判断用户名是否存在
    private boolean hasUsername(String username)
    {
        try {
            JSONObject jsonStr = new JSONObject();
            jsonStr.put("id",username);
            String result = HttpUtils.sendPostMessage("/login/hasUser.do",jsonStr.toJSONString());
            JSONObject re = JSON.parseObject(result);
            if(re.getString("result").equals("1"))
            {
                return true;
            }
            else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //判断商户是否存在
    private boolean hasShopName(String shopName)
    {
        try {
            JSONObject jsonStr = new JSONObject();
            jsonStr.put("id",shopName);
            String result = HttpUtils.sendPostMessage("/login/hasShop.do",jsonStr.toJSONString());
            JSONObject re = JSON.parseObject(result);
            if(re.getString("result").equals("1"))
            {
                return true;
            }
            else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    //密码和确认密码框初始化
    private void initPassword()
    {
        passwordEd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    passwordAlterTv.setText("");
                }
                else
                {
                    String password = passwordEd.getText().toString();
                    if(password.equals(""))
                    {
                        passwordAlterTv.setText("密码不能为空");
                    }
                    else if(password.length()<7)
                    {
                        passwordAlterTv.setText("密码长度不能小于7位");
                    }
                    else if(password.length()>32)
                    {
                        passwordAlterTv.setText("密码长度不能大于32位");
                    }
                    else if(password.equals(passwordAgainEd.getText().toString()))
                    {
                        passwordAgainAlterTv.setText("");
                    }
                }
            }
        });

        passwordAgainEd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    passwordAgainAlterTv.setText("");
                }
                else
                {
                    if(!passwordEd.getText().toString().equals(passwordAgainEd.getText().toString()))
                    {
                        passwordAgainAlterTv.setText("两次输入的密码不一致");
                    }
                }
            }
        });

        passwordAgainEd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String now = v.toString();
                if(!now.equals(passwordEd.getText().toString()))
                {
                    passwordAgainAlterTv.setText("两次密码不一致");
                    return false;
                }
                else
                {
                    return true;
                }
            }
        });
    }

    //注册按钮初始化
    private void commitInit()
    {

        //绑定点击事件
        commitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(passwordAgainAlterTv.getText().toString().equals("")
                        &&usernameAlterTv.getText().toString().equals("")
                        &&passwordAlterTv.getText().toString().equals("")))
                {
                    Toast.makeText(Register.this,"请填写正确信息",Toast.LENGTH_SHORT).show();
                    return;
                }
                String userName = usernameEd.getText().toString();
                String password = passwordEd.getText().toString();
                String passwordAgain = passwordAgainEd.getText().toString();
                final String phoneNum = phoneEd.getText().toString();
                final String yzm = YZMEd.getText().toString();
                int type = -1;
                if(shopRb.isChecked())
                {
                    type = 2;
                }
                else
                {
                    type = 1;
                }
                //判断是否有空项目
                if(userName.equals(""))
                {
                    usernameAlterTv.setText("用户名不能为空");
                    return;
                }
                else if(userName.length()<7)
                {
                    usernameAlterTv.setText("用户名长度不能小于7位");
                    return;
                }
                else if(userName.length()>16)
                {
                    usernameAlterTv.setText("用户名长度不能大于16位");
                    return;
                }
                else if(password.equals(""))
                {
                    passwordAlterTv.setText("密码不能为空");
                    return;
                }
                else if(password.length()<7)
                {
                    passwordAlterTv.setText("密码长度不能小于7位");
                    return;
                }
                else if(password.length()>32)
                {
                    passwordAlterTv.setText("密码长度不能大于32位");
                    return;
                }
                else if(passwordAgain.equals(""))
                {
                    passwordAgainAlterTv.setText("确认密码不能为空");
                    return;
                }
                else if(yzm.equals(""))
                {
                    Toast.makeText(Register.this,"验证码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }


                final JSONObject jsonStr = new JSONObject();
                try {
                    jsonStr.put("username",userName);
                    jsonStr.put("password",password);
                    jsonStr.put("YZM",yzm);
                    jsonStr.put("type",type);
                    jsonStr.put("phone",phoneNum);


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                if(!OKHttpUtils.verifySMS(phoneNum,yzm,Register.this)){
                                    Looper.prepare();
                                    Toast.makeText(context,"验证码错误",Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }

                                String message =HttpUtils.sendPostMessage("/login/register.do",jsonStr.toJSONString());
                                final JSONObject re = JSON.parseObject(message);
                                Looper.prepare();
                                //注册成功返回登录界面
                                if(re.getString("state").equals("1"))
                                {
                                    Toast.makeText(Register.this,re.getString("message"),Toast.LENGTH_SHORT).show();
                                    new Handler(new Handler.Callback() {
                                        @Override
                                        public boolean handleMessage(Message msg) {
                                            Intent it = new Intent(context, LoginActivity.class);
                                            startActivity(it);
                                            return false;
                                        }
                                    }).sendEmptyMessageDelayed(0x123,2000);//延时跳转
                                }
                                else
                                {
                                    Toast.makeText(Register.this,re.getString("message"),Toast.LENGTH_SHORT).show();
                                }
                                Looper.loop();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
    }

    private void initYZM()
    {

        //获取验证码
        getYZM();

        //点击验证码时重新更换
        yzmIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getYZM();
            }
        });
    }

    private void getYZM()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Bitmap bitmap = HttpUtils.getYZM();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            yzmIV.setImageBitmap(bitmap);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
