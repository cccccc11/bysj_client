package com.example.bysj.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Looper;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.example.bysj.CommodityInfo;
import com.example.bysj.OKHttpUtils;
import com.example.bysj.R;
import com.example.bysj.RequestCodeUtils;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;

public class PayDialog extends Dialog {

    private int password[];
    private TextView pswList[];
    private int now;
    private TextView passwordTv1,passwordTv2,passwordTv3,passwordTv4,passwordTv5,passwordTv6,
                     input1,input2,input3,input4,input5,input6,input7,input8,input9,input0,input_delete,
                     mainPayTv;
    private Context context;
    private Integer commodityId;
    private Activity activity;
    private Integer addNum;

//

    public PayDialog(@NonNull Context context,Activity activity,TextView mainPayTv,Integer commodityId,Integer addNum) {
        super(context);
        this.context = context;
        this.activity = activity;
        this.addNum = addNum;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pay_dialog);

        Window dialogWindow = this.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager m = ((Activity) context).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth()); // 宽度设置为屏幕的百分之百


        this.mainPayTv = mainPayTv;
        this.commodityId = commodityId;

        init();

    }

    private void init(){

        pswList = new TextView[6];
        passwordTv1 = findViewById(R.id.pay_password_1);
        pswList[0] = passwordTv1;

        passwordTv2 = findViewById(R.id.pay_password_2);
        pswList[1] = passwordTv2;

        passwordTv3 = findViewById(R.id.pay_password_3);
        pswList[2] = passwordTv3;

        passwordTv4 = findViewById(R.id.pay_password_4);
        pswList[3] = passwordTv4;

        passwordTv5 = findViewById(R.id.pay_password_5);
        pswList[4] = passwordTv5;

        passwordTv6 = findViewById(R.id.pay_password_6);
        pswList[5] = passwordTv6;

        input1 = findViewById(R.id.pay_input_1);
        input2 = findViewById(R.id.pay_input_2);
        input3 = findViewById(R.id.pay_input_3);
        input4 = findViewById(R.id.pay_input_4);
        input5 = findViewById(R.id.pay_input_5);
        input6 = findViewById(R.id.pay_input_6);
        input7 = findViewById(R.id.pay_input_7);
        input8 = findViewById(R.id.pay_input_8);
        input9 = findViewById(R.id.pay_input_9);
        input0 = findViewById(R.id.pay_input_0);
        input_delete = findViewById(R.id.pay_input_delete);

        password = new int[6];
        now = -1;

        initInput();
    }


    private void initInput(){
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int number = -1;
                switch (v.getId()){
                    case R.id.pay_input_0:{
                        number = 0;
                        break;
                    }
                    case R.id.pay_input_1:{
                        number = 1;
                        break;
                    }
                    case R.id.pay_input_2:{
                        number = 2;
                        break;
                    }
                    case R.id.pay_input_3:{
                        number = 3;
                        break;
                    }
                    case R.id.pay_input_4:{
                        number = 4;
                        break;
                    }
                    case R.id.pay_input_5:{
                        number = 5;
                        break;
                    }
                    case R.id.pay_input_6:{
                        number = 6;
                        break;
                    }
                    case R.id.pay_input_7:{
                        number = 7;
                        break;
                    }
                    case R.id.pay_input_8:{
                        number = 8;
                        break;
                    }
                    case R.id.pay_input_9:{
                        number = 9;
                        break;
                    }
                    case R.id.pay_input_delete:{
                        break;
                    }
                }

                if(number!=-1){
                    now++;
                    pswList[now].setText("0");
                    password[now] = number;
                    if(now==5){

                        String psw = password[0]+""+password[1]+""+password[2]+""+password[3]+""+password[4]+""+password[5]+"";

                        if(psw.equals("000000")){
                            paySuccessful();
                        }else {
                            Toast.makeText(context,"支付密码错误",Toast.LENGTH_SHORT).show();
                            for(TextView view:pswList){
                                view.setText("");
                            }
                            now=-1;
                        }
                    }
                }else {
                    if(now>=0){
                        pswList[now].setText("");
                        now--;
                    }
                }
            }
        };
        input0.setOnClickListener(onClickListener);
        input1.setOnClickListener(onClickListener);
        input2.setOnClickListener(onClickListener);
        input3.setOnClickListener(onClickListener);
        input4.setOnClickListener(onClickListener);
        input5.setOnClickListener(onClickListener);
        input6.setOnClickListener(onClickListener);
        input7.setOnClickListener(onClickListener);
        input8.setOnClickListener(onClickListener);
        input9.setOnClickListener(onClickListener);
        input_delete.setOnClickListener(onClickListener);
    }

    private void paySuccessful(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonStr = new JSONObject();
                jsonStr.put("commodityId",commodityId);
                String userId = context.getSharedPreferences("info",Context.MODE_PRIVATE).getString("id","-1");
                if(userId.equals("-1")){
                    Looper.prepare();
                    Toast.makeText(context,"获取id错误",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                jsonStr.put("userId",userId);
                jsonStr.put("num",addNum);
                ResponseBody responseBody = null;
                try {
                    responseBody = OKHttpUtils.postMessage("/user/pay.do",jsonStr.toJSONString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(responseBody == null) {
                    Looper.prepare();
                    Toast.makeText(context, "获取结果失败", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                    String re = null;
                    try {
                        re = responseBody.string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                if(re==null){
                    ((CommodityInfo)activity).onActivityResult(0, RequestCodeUtils.RESULT_CODE_PAY_FAILED,null);
                }else if(re.equals("haspay")){
                    ((CommodityInfo)activity).onActivityResult(0, RequestCodeUtils.RESULT_CODE_PAY_HASPAY,null);
                }else if(re.equals("-1")){
                    ((CommodityInfo)activity).onActivityResult(0, RequestCodeUtils.RESULT_CODE_PAY_FAILED,null);
                }else if(re.equals("notEnough")){
                    ((CommodityInfo)activity).onActivityResult(0, RequestCodeUtils.RESULT_CODE_PAY_NOTENOUGH,null);
                }
                else {
                    ((CommodityInfo)activity).onActivityResult(0, RequestCodeUtils.RESULT_CODE_PAY_SUCCESS,null);
                    dismiss();
                }
                }
        }).start();



    }



}
