package com.example.bysj.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.bysj.CommunicationActivity;
import com.example.bysj.OKHttpUtils;
import com.example.bysj.R;
import com.example.bysj.User;
import com.example.bysj.fragment.userFragment.UserCommunicationFragment;
import com.example.bysj.fragment.userFragment.UserOrderFragment;

import java.io.IOException;

import okhttp3.ResponseBody;

public class AddCommunicationDialog extends Dialog {

    private TextView addTv;
    private EditText titleEt,contentEt;
    private String userId;
    private Context context;

    public AddCommunicationDialog(@NonNull Context context,String userId) {
        super(context);

        this.context = context;
        this.userId = userId;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_communication);

        Window dialogWindow = this.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager m = ((Activity) context).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth()); // 宽度设置为屏幕的百分之百


        init();

    }

    private void init(){
        addTv = findViewById(R.id.dialog_add_communication_add_tv);
        titleEt = findViewById(R.id.dialog_add_communication_title_et);
        contentEt = findViewById(R.id.dialog_add_communication_content_et);

        initAdd();
    }

    private void initAdd(){
        addTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = titleEt.getText().toString();
                final String content = contentEt.getText().toString();
                if(title.equals("")){
                    Toast.makeText(context,"标题不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(content.equals("")){
                    Toast.makeText(context,"内容不能不空",Toast.LENGTH_SHORT).show();
                    return;
                }
                final WaitDialog waitDialog = new WaitDialog(context);
                waitDialog.show();
                //删除订单
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        ResponseBody responseBody = null;
                        JSONObject requestJson = new JSONObject();
                        requestJson.put("userId",userId);
                        requestJson.put("title",title);
                        requestJson.put("content",content);
                        try {
                            responseBody = OKHttpUtils.postMessage("/user/communication/addCommunication",requestJson.toJSONString());
                        } catch (IOException e) {
                            e.printStackTrace();
                            Looper.prepare();
                            Toast.makeText(context,"连接超时",Toast.LENGTH_SHORT).show();
                            waitDialog.dismiss();
                            Looper.loop();
                        }
                        String resultStr = null;
                        try {
                            resultStr = responseBody.string();
                        } catch (IOException e) {
                            e.printStackTrace();
                            waitDialog.dismiss();
                        }
                        if(resultStr == null){
                            Looper.prepare();
                            Toast.makeText(context,"未知错误",Toast.LENGTH_SHORT).show();
                            waitDialog.dismiss();
                            Looper.loop();
                        }
                        Looper.prepare();
                        JSONObject resultJson = JSON.parseObject(resultStr);
                        if(resultJson.getString("result").equals("1")){
                            Toast.makeText(context,"添加成功",Toast.LENGTH_SHORT).show();
                        }
                        if(resultJson.getString("result").equals("0")){
                            Toast.makeText(context,"添加失败",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        waitDialog.dismiss();
                        AddCommunicationDialog.this.dismiss();
                        ((UserCommunicationFragment)((User)context).communicationFragment).refreshRecycle();
                        Intent it = new Intent(context, CommunicationActivity.class);
                        it.putExtra("userId",userId);
                        it.putExtra("communicationId",resultJson.getInteger("communicationId"));
                        context.startActivity(it);
                        Looper.loop();
                    }
                }).start();
            }
        });
    }
}
