package com.example.bysj.fragment.userFragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.bysj.OKHttpUtils;
import com.example.bysj.OrderAdapter;
import com.example.bysj.R;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;

public class UserOrderFragment extends Fragment {

    private View view;
    private Activity activity;
    private RecyclerView recyclerView;
    private List<Map<String,Object>> myOrders;
    private ImageView emptyOrderIv;

    public UserOrderFragment(Activity activity){
        this.activity = activity;
    }

    public UserOrderFragment(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.user_order_fragment, container, false);

        init();

        return view;
    }

    private void init(){
        recycleViewInit();
    }

    private void recycleViewInit(){
        emptyOrderIv = view.findViewById(R.id.user_order_empty_iv);
        recyclerView = view.findViewById(R.id.user_order_content);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        refreshRecycle();

    }

    public void refreshRecycle(){
        myOrders = new ArrayList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                ResponseBody responseBody = null;
                JSONObject requestJson = new JSONObject();
                String userId  = activity.getSharedPreferences("info", Context.MODE_PRIVATE).getString("id","-1");
                requestJson.put("userId",userId);

                try {
                    responseBody = OKHttpUtils.postMessage("/user/order/getOrders.do",requestJson.toJSONString());
                } catch (IOException e) {
                    Looper.prepare();
                    Toast.makeText(activity,"连接超时",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                String result = null;
                try {
                    result = responseBody.string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(result==null){
                    Looper.prepare();
                    Toast.makeText(activity,"连接超时",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                final JSONArray resultJsonArray = JSONObject.parseArray(result);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(resultJsonArray.size()==0){
                            emptyOrderIv.setImageDrawable(activity.getDrawable(R.mipmap.order_empty));
                        }else {
                            emptyOrderIv.setBackground(null);
                        }
                    }
                });


                for(Object object:resultJsonArray){
                    Map<String,Object> order = new HashMap<>();
                    order.put("commodityId",((JSONObject)object).getInteger("id"));
                    order.put("name",((JSONObject)object).getString("name"));
                    order.put("num",((JSONObject)object).getInteger("num"));
                    order.put("endDate",((JSONObject)object).getString("shopName"));
                    order.put("state",((JSONObject)object).getString("state"));
                    order.put("orderState",((JSONObject)object).getString("orderState"));
                    if((((JSONObject)object).get("photosPath"))==null){
                        order.put("photosPath",null);
                    }else {
                        String[] photosPath = new String[((JSONArray)((JSONObject)object).get("photosPath")).size()];
                        ((JSONArray)((JSONObject)object).get("photosPath")).toArray(photosPath);
                        order.put("photosPath",photosPath);
                    }
                    myOrders.add(order);
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(new OrderAdapter(myOrders,activity));
                    }
                });
            }

        }).start();
    }
}
