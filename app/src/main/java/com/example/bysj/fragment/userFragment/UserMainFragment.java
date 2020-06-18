package com.example.bysj.fragment.userFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.bysj.ActivityDistribution;
import com.example.bysj.CommodityAdapter;
import com.example.bysj.OKHttpUtils;
import com.example.bysj.R;
import com.example.bysj.RequestCodeUtils;
import com.example.bysj.UserMainAdapter;
import com.example.bysj.bean.Commodity;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;

public class UserMainFragment extends Fragment {

    private View view;
    private Activity activity;
    private RecyclerView recyclerView;
    private List<Map<String,Object>> commodityList;
    private EditText searchEt;
    private ImageView searchIv;

    public UserMainFragment(Activity activity){
        this.activity = activity;
    }

    public UserMainFragment(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.user_main_fragment, container, false);

        init();

        return view;
    }

    private void init(){
        recyclerView = view.findViewById(R.id.user_main_content);
        searchEt = view.findViewById(R.id.user_main_search_ed);
        searchIv = view.findViewById(R.id.user_main_search_iv);


        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewInit();
        searchInit();

    }

    private void recyclerViewInit(){
        commodityList = new ArrayList<Map<String,Object>>();

        //从服务器获取数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                ResponseBody responseBody = null;
                try {
                    responseBody = OKHttpUtils.postMessage("/user/info/getAllCommodities","");
                } catch (IOException e) {
                    Looper.prepare();
                    Toast.makeText(activity,"连接超时",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    e.printStackTrace();
                }
                if(responseBody==null){
                    Looper.prepare();
                    Toast.makeText(activity,"获取失败",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                String re = "";
                try {
                    re = responseBody.string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                JSONArray array = JSON.parseArray(re);
                for(Object jsonObject:array){
                    JSONObject json = (JSONObject) jsonObject;
                    Map<String, Object> commodity = new HashMap<String,Object>();
                    commodity.put("id",json.getInteger("id"));
                    commodity.put("endDate",json.getSqlDate("endDate"));
                    commodity.put("numNow",json.getInteger("numNow"));
                    commodity.put("number",json.getInteger("number"));
                    commodity.put("price",json.getDoubleValue("price"));
                    commodity.put("name",json.getString("name"));
                    commodity.put("introduce",json.getString("introduce"));
                    if((json.get("photosPath"))==null){
                        commodity.put("photosPath",null);
                    }else {
                        String[] photosPath = new String[((JSONArray)json.get("photosPath")).size()];
                        ((JSONArray)json.get("photosPath")).toArray(photosPath);
                        commodity.put("photosPath",photosPath);
                    }

                    commodity.put("shopId",json.getString("shopId"));
                    commodity.put("startDate",json.getSqlDate("startDate"));
                    commodity.put("state",json.getString("state"));
                    commodity.put("shopName",json.getString("shopName"));


                    commodityList.add(commodity);
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UserMainAdapter userMainAdapter = new UserMainAdapter(commodityList,activity);
                        recyclerView.setAdapter(userMainAdapter);
                    }
                });
            }
        }).start();


    }

    private void searchInit(){
        searchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String searchStr = searchEt.getText().toString();
                if (searchStr.equals("")){
                    recyclerViewInit();
                    return;
                }

                //从服务器获取数据
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ResponseBody responseBody = null;
                        try {
                            responseBody = OKHttpUtils.postMessage("/user/main/search",searchStr);
                        } catch (IOException e) {
                            Looper.prepare();
                            Toast.makeText(activity,"连接超时",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                            e.printStackTrace();
                        }
                        if(responseBody==null){
                            Looper.prepare();
                            Toast.makeText(activity,"获取失败",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                        String re = "";
                        try {
                            re = responseBody.string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        JSONArray array = JSON.parseArray(re);
                        commodityList = new ArrayList<>();
                        for(Object jsonObject:array){
                            JSONObject json = (JSONObject) jsonObject;
                            Map<String, Object> commodity = new HashMap<String,Object>();
                            commodity.put("id",json.getInteger("id"));
                            commodity.put("endDate",json.getSqlDate("endDate"));
                            commodity.put("numNow",json.getInteger("numNow"));
                            commodity.put("number",json.getInteger("number"));
                            commodity.put("price",json.getDoubleValue("price"));
                            commodity.put("name",json.getString("name"));
                            commodity.put("introduce",json.getString("introduce"));
                            if((json.get("photosPath"))==null){
                                commodity.put("photosPath",null);
                            }else {
                                String[] photosPath = new String[((JSONArray)json.get("photosPath")).size()];
                                ((JSONArray)json.get("photosPath")).toArray(photosPath);
                                commodity.put("photosPath",photosPath);
                            }

                            commodity.put("shopId",json.getString("shopId"));
                            commodity.put("startDate",json.getSqlDate("startDate"));
                            commodity.put("state",json.getString("state"));
                            commodity.put("shopName",json.getString("shopName"));


                            commodityList.add(commodity);
                        }
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                UserMainAdapter userMainAdapter = new UserMainAdapter(commodityList,activity);
                                recyclerView.setAdapter(userMainAdapter);
                            }
                        });
                    }
                }).start();

            }
        });
    }

    public void refreshRecycle(){
        recyclerViewInit();
    }


}
