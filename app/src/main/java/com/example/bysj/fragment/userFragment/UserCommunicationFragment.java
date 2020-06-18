package com.example.bysj.fragment.userFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.bysj.CommodityAdapter;
import com.example.bysj.CommunicationActivity;
import com.example.bysj.CommunicationAdapter;
import com.example.bysj.OKHttpUtils;
import com.example.bysj.R;
import com.example.bysj.dialog.AddCommunicationDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;

public class UserCommunicationFragment extends Fragment {

    private View view;
    private Activity activity;
    private RecyclerView recyclerView;
    private List<Map<String,Object>> communications;
    private ImageView addIv;

    public UserCommunicationFragment(Activity activity){
        this.activity = activity;
    }

    public UserCommunicationFragment(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.user_communication_fragment, container, false);

        init();

        return view;
    }

    private void init(){
        recyclerView = view.findViewById(R.id.user_communication_content);
        addIv = view.findViewById(R.id.user_communication_add_iv);

        initRecycle();
        initAdd();
    }

    private void initRecycle(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewInit();
    }

    private void recyclerViewInit(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                communications = new ArrayList<>();

                ResponseBody responseBody = null;


                try {
                    responseBody = OKHttpUtils.postMessage("/user/communication/getAllCommunication","");
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(activity,"连接超时",Toast.LENGTH_SHORT).show();
                }
                String resultStr = null;
                try {
                    resultStr = responseBody.string();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(activity,"未知错误",Toast.LENGTH_SHORT).show();
                }
                if(resultStr == null){
                    Toast.makeText(activity,"未知错误",Toast.LENGTH_SHORT).show();
                }
                JSONArray resultArray = JSON.parseArray(resultStr);
                for(Object result:resultArray){
                    JSONObject resultJson = (JSONObject) result;
                    Map<String,Object> communication = new HashMap<>();
                    communication.put("communicationId",resultJson.getInteger("communicationId"));
                    communication.put("userId",resultJson.getString("userId"));
                    communication.put("date",resultJson.getSqlDate("date"));
                    communication.put("title",resultJson.getString("title"));
                    communication.put("content",resultJson.getString("content"));
                    communication.put("userName",resultJson.getString("userName"));
                    communication.put("num",resultJson.getInteger("num"));
                    communication.put("head",resultJson.getString("head"));
                    communications.add(communication);
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(new CommunicationAdapter(activity,communications));
                    }
                });
            }
        }).start();
    }

    public void refreshRecycle(){
        recyclerViewInit();
    }

    private void initAdd(){
        addIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                new AddCommunicationDialog(activity,activity.getSharedPreferences("info", Context.MODE_PRIVATE).getString("id","-1")).show();
            }
        });
    }
}
