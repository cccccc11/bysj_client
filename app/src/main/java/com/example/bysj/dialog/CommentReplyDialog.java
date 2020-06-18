package com.example.bysj.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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

import com.alibaba.fastjson.JSONObject;
import com.example.bysj.CommunicationActivity;
import com.example.bysj.OKHttpUtils;
import com.example.bysj.R;


import java.io.IOException;

import okhttp3.ResponseBody;

public class CommentReplyDialog extends Dialog {

    private TextView commitTv;
    private EditText contentEt;
    private Integer communicationId;
    private String userId;
    private Context context;

    public CommentReplyDialog(@NonNull Context context,Integer communicationId) {
        super(context);
        this.context = context;
        this.communicationId = communicationId;
        this.userId = context.getSharedPreferences("info",Context.MODE_PRIVATE).getString("id","-1");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_comment_reply);

        Window dialogWindow = this.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager m = ((Activity) context).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth()); // 宽度设置为屏幕的百分之百

        init();
    }

    private void init(){
        commitTv = findViewById(R.id.dialog_comment_reply_add_tv);
        contentEt = findViewById(R.id.dialog_comment_reply_content_et);

        initCommit();
    }

    private void initCommit(){
        commitTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String content = contentEt.getText().toString();
                if(content.equals("")){
                    Toast.makeText(context,"请输入内容",Toast.LENGTH_SHORT).show();
                    return;
                }
                final WaitDialog waitDialog = new WaitDialog(context);
                waitDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ResponseBody responseBody = null;
                        JSONObject requestJson = new JSONObject();

                        requestJson.put("userId",userId);
                        requestJson.put("communicationId",communicationId);
                        requestJson.put("content",content);
                        try {
                            responseBody = OKHttpUtils.postMessage("/user/comment/addComment",requestJson.toJSONString());
                        } catch (IOException e) {
                            e.printStackTrace();
                            Looper.prepare();
                            Toast.makeText(context,"连接超时",Toast.LENGTH_SHORT).show();
                            waitDialog.dismiss();
                            Looper.loop();
                        }

                        String result = null;
                        try {
                            result = responseBody.string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (result == null){
                            Looper.prepare();
                            Toast.makeText(context,"未知错误",Toast.LENGTH_SHORT).show();
                            waitDialog.dismiss();
                            Looper.loop();
                        }

                        if(result.equals("1")){
                            Looper.prepare();
                            Toast.makeText(context,"回复成功",Toast.LENGTH_SHORT).show();
                            ((CommunicationActivity)context).refreshRecycle();
                            waitDialog.dismiss();
                            CommentReplyDialog.this.dismiss();
                            Looper.loop();
                        }
                        if (result.equals("0")){
                            Looper.prepare();
                            Toast.makeText(context,"回复失败",Toast.LENGTH_SHORT).show();
                            waitDialog.dismiss();
                            Looper.loop();
                        }

                    }
                }).start();
            }
        });
    }
}
