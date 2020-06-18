package com.example.bysj.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.example.bysj.OKHttpUtils;
import com.example.bysj.R;


import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;

import okhttp3.ResponseBody;

public class UserInfoBirthDayChangeDialog extends Dialog {

    private TextView commit,infoBirthdayTv;
    private DatePicker datePicker;
    private String userId;
    private Context context;



    public UserInfoBirthDayChangeDialog(@NonNull Context context,String userId,TextView infoBirthdayTv) {
        super(context);
        this.userId = userId;
        this.context = context;
        this.infoBirthdayTv = infoBirthdayTv;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.date_pick_dialog);

        init();

    }
    private void init(){
        commit = findViewById(R.id.date_picker_commit);
        datePicker = findViewById(R.id.date_picker);
        initDatePicker();
        initCommit();
    }
    private void initDatePicker(){
        datePicker.setMaxDate(Calendar.getInstance().getTimeInMillis());
    }
    private void initCommit(){
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int year = datePicker.getYear();
                final int month = datePicker.getMonth();
                final int day = datePicker.getDayOfMonth();
                final JSONObject requestJson = new JSONObject();
                requestJson.put("year",year);
                requestJson.put("month",month);
                requestJson.put("day",day);
                requestJson.put("userId",userId);
                final WaitDialog waitDialog = new WaitDialog(context);
                waitDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        ResponseBody responseBody = null;
                        try {
                            responseBody = OKHttpUtils.postMessage("/user/info/changeBirthday.do",requestJson.toJSONString());
                        } catch (IOException e) {
                            e.printStackTrace();
                            Looper.prepare();
                            Toast.makeText(context,"连接超时", Toast.LENGTH_LONG).show();
                            waitDialog.dismiss();
                            Looper.loop();
                        }
                        if(responseBody == null){
                            Looper.prepare();
                            Toast.makeText(context,"未知错误", Toast.LENGTH_LONG).show();
                            waitDialog.dismiss();
                            Looper.loop();
                        }
                        String resultStr = null;
                        try {
                            resultStr = responseBody.string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(resultStr==null){
                            Looper.prepare();
                            Toast.makeText(context,"连接超时", Toast.LENGTH_LONG).show();
                            waitDialog.dismiss();
                            Looper.loop();
                        }
                        if(resultStr.equals("1")){
                            Looper.prepare();
                            Toast.makeText(context,"修改成功", Toast.LENGTH_LONG).show();
                            context.getSharedPreferences("info",Context.MODE_PRIVATE).edit().putString("birthday",year+"-"+(month+1)+"-"+day).commit();
                            waitDialog.dismiss();
                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    infoBirthdayTv.setText(year+"-"+(month+1)+"-"+day);
                                }
                            });

                            UserInfoBirthDayChangeDialog.this.dismiss();
                            Looper.loop();
                        }else if (resultStr.equals("0")){
                            Looper.prepare();
                            Toast.makeText(context,"修改失败", Toast.LENGTH_LONG).show();
                            waitDialog.dismiss();
                            Looper.loop();
                        }

                    }
                }).start();


                return;
            }
        });
    }

}
