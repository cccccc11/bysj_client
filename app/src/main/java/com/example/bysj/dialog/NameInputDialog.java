package com.example.bysj.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.example.bysj.OKHttpUtils;
import com.example.bysj.R;

import java.io.IOException;

import okhttp3.ResponseBody;

public class NameInputDialog extends Dialog {

    private Button commitBt;
    private Activity activity;
    private EditText nameEd;
    private TextView nameTv,nameTitleTv;
    private String userType;

    public NameInputDialog(@NonNull Context context, Activity activity, TextView nameTv,TextView nameTitleTv) {

        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.activity = activity;
        this.nameTitleTv = nameTitleTv;
        this.nameTv = nameTv;
        setContentView(R.layout.name_input_dialog);
        if(nameTv.getId()==R.id.user_info_name_tv){
            userType = "user";
        }else {
            userType = "shop";
        }
        init();
    }

    private void init(){
        commitBt = this.findViewById(R.id.shop_info_dialog_commit);
        nameEd = this.findViewById(R.id.shop_info_name_dialog_ed);
        initCommitBt();
    }

    private void initCommitBt(){
        commitBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEd.getText().toString();
                if(name.equals("")){
                    Toast.makeText(activity,"新名字不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
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
                        final String newName = nameEd.getText().toString();

                        ResponseBody responseBody = null;
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("newName",newName);
                            responseBody = OKHttpUtils.postMessage("/"+userType+"/info/changeName.do",jsonObject.toJSONString());
                        } catch (IOException e) {
                            waitDialog.dismiss();
                            Looper.prepare();
                            Toast.makeText(activity,"连接超时",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                        if(responseBody==null){
                            waitDialog.dismiss();
                            Looper.prepare();
                            Toast.makeText(activity,"连接超时",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                            return;
                        }
                        String re = "";
                        try {
                            re = responseBody.string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Looper.prepare();
                        if(re.equals("1"))
                        {
                            Toast.makeText(activity,"修改成功",Toast.LENGTH_SHORT).show();
                            SharedPreferences sharedPreferences = activity.getSharedPreferences("info", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("name",newName);
                            editor.apply();
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    nameTv.setText(newName);
                                    nameTitleTv.setText(newName);
                                }
                            });
                        }
                        else {
                            Toast.makeText(activity,"修改失败",Toast.LENGTH_SHORT).show();
                        }
                        waitDialog.dismiss();
                        NameInputDialog.this.dismiss();
                        Looper.loop();
                    }
                }).start();
            }
        });
    }
}
