package com.example.bysj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.bysj.dialog.CommentReplyDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;

public class CommunicationActivity extends AppCompatActivity {

    private ImageView exitIv;
    private TextView replyTv,titleTv;
    private Integer communicationId;
    private String userId;
    private Intent intent;
    private RecyclerView recyclerView;
    private List<Map<String,Object>> comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication);
        intent = getIntent();
        userId = intent.getStringExtra("userId");
        communicationId = intent.getIntExtra("communicationId",-1);
        init();
    }


    private void init(){
        exitIv = findViewById(R.id.communication_exit);
        replyTv = findViewById(R.id.communication_reply_tv);
        titleTv = findViewById(R.id.communication_title_tv);
        recyclerView = findViewById(R.id.communication_content);

        initExit();
        initRecycle();
        initReply();
    }

    private void initExit(){
        exitIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommunicationActivity.this.finish();
            }
        });
    }

    private void initReply(){
        replyTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CommentReplyDialog(CommunicationActivity.this,communicationId).show();
            }
        });
    }

    private void initRecycle(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        refreshRecycle();

    }

    //获取第一楼的信息
    private void initMain(){
                ResponseBody responseBody = null;
                Map<String,Object> main = new HashMap<>();
                final JSONObject requestJson = new JSONObject();
                requestJson.put("userId",userId);
                requestJson.put("communicationId",communicationId);
                try {
                    responseBody = OKHttpUtils.postMessage("/user/communication/getCommunication",requestJson.toJSONString());
                } catch (IOException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(CommunicationActivity.this,"连接超时",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                if(responseBody == null){
                    Looper.prepare();
                    Toast.makeText(CommunicationActivity.this,"未知错误",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                String resultStr = null;
                try {
                    resultStr = responseBody.string();
                } catch (IOException e) {
                    Looper.prepare();
                    Toast.makeText(CommunicationActivity.this,"未知错误",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                if(resultStr==null){
                    Looper.prepare();
                    Toast.makeText(CommunicationActivity.this,"未知错误",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                final JSONObject resultJson = JSON.parseObject(resultStr);
                CommunicationActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        titleTv.setText(resultJson.getString("title"));
                    }
                });
                main.put("date",resultJson.getSqlDate("date"));
                main.put("content",resultJson.getString("content"));
                main.put("name",resultJson.getString("name"));
                main.put("head",resultJson.getString("head"));
                comments.add(main);
    }

    //获取所有评论
    private void getAllComments(){
                ResponseBody responseBody = null;
                JSONObject requestJson = new JSONObject();
                requestJson.put("communicationId",communicationId);
                try {
                    responseBody = OKHttpUtils.postMessage("/user/communication/getAllComments",requestJson.toJSONString());
                } catch (IOException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(CommunicationActivity.this,"连接超时",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                if(responseBody == null){
                    Looper.prepare();
                    Toast.makeText(CommunicationActivity.this,"未知错误",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                String resultStr = null;
                try {
                    resultStr = responseBody.string();
                } catch (IOException e) {
                    Looper.prepare();
                    Toast.makeText(CommunicationActivity.this,"未知错误",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                if(resultStr==null){
                    Looper.prepare();
                    Toast.makeText(CommunicationActivity.this,"未知错误",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

                JSONArray resultArray = JSON.parseArray(resultStr);
                for(Object result:resultArray){
                    JSONObject resultJson = (JSONObject)result;
                    Map<String,Object> comment = new HashMap<>();
                    comment.put("date",resultJson.getSqlDate("date"));
                    comment.put("content",resultJson.getString("content"));
                    comment.put("name",resultJson.getString("name"));
                    comment.put("head",resultJson.getString("head"));
                    comments.add(comment);
                }
            }

    public void refreshRecycle(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                comments = new ArrayList<>();
                initMain();
                getAllComments();
                CommunicationActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(new CommentAdapter(CommunicationActivity.this,comments));
                    }
                });
            }
        }).start();
    }

}
