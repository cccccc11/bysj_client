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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.bysj.OKHttpUtils;
import com.example.bysj.R;
import com.example.bysj.ShoppingCartAdapter;
import com.example.bysj.UserMainAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;

public class UserShoppingCartFragment extends Fragment {

    private View view;
    private Activity activity;
    private RecyclerView recyclerView;
    private List<Map<String,Object>> commodityList;
    private ImageView emptyShoppingCartIv;

    public UserShoppingCartFragment(Activity activity){
        this.activity = activity;
    }

    public UserShoppingCartFragment(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.user_shopping_cart_fragment, container, false);

        init();

        return view;
    }
    private void init(){

        recyclerViewInit();
    }
    private void recyclerViewInit() {
        recyclerView = view.findViewById(R.id.user_shopping_cart_content);
        emptyShoppingCartIv = view.findViewById(R.id.user_shopping_cart_empty_iv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        refreshRecycle();
    }

    public void refreshRecycle(){
        commodityList = new ArrayList<Map<String, Object>>();
        //从服务器获取数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                ResponseBody responseBody = null;
                JSONObject requestJson = new JSONObject();
                requestJson.put("userId",activity.getSharedPreferences("info", Context.MODE_PRIVATE).getString("id","-1"));
                try {
                    responseBody = OKHttpUtils.postMessage("/user/shoppingCart/getMyShoppingCart.do", requestJson.toJSONString());
                } catch (IOException e) {
                    Looper.prepare();
                    Toast.makeText(activity, "连接超时", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    e.printStackTrace();
                }
                if (responseBody == null) {
                    Looper.prepare();
                    Toast.makeText(activity, "获取失败", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                String re = "";
                try {
                    re = responseBody.string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final JSONArray array = JSON.parseArray(re);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(array.size()==0){
                            emptyShoppingCartIv.setImageDrawable(activity.getDrawable(R.mipmap.shopping_cart_empty));
                        }else {
                            emptyShoppingCartIv.setBackground(null);
                        }
                    }
                });


                for (Object jsonObject : array) {
                    JSONObject json = (JSONObject) jsonObject;
                    Map<String, Object> commodity = new HashMap<String, Object>();
                    commodity.put("id", json.getInteger("id"));
                    commodity.put("endDate", json.getSqlDate("endDate"));
                    commodity.put("numNow", json.getInteger("numNow"));
                    commodity.put("number", json.getInteger("number"));
                    commodity.put("price", json.getDoubleValue("price"));
                    commodity.put("name", json.getString("name"));
                    commodity.put("introduce", json.getString("introduce"));
                    if ((json.get("photosPath")) == null) {
                        commodity.put("photosPath", null);
                    } else {
                        String[] photosPath = new String[((JSONArray) json.get("photosPath")).size()];
                        ((JSONArray) json.get("photosPath")).toArray(photosPath);
                        commodity.put("photosPath", photosPath);
                    }

                    commodity.put("shopId", json.getString("shopId"));
                    commodity.put("startDate", json.getSqlDate("startDate"));
                    commodity.put("state", json.getString("state"));
                    commodity.put("shopName", json.getString("shopName"));


                    commodityList.add(commodity);
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ShoppingCartAdapter userMainAdapter = new ShoppingCartAdapter(commodityList, activity);
                        recyclerView.setAdapter(userMainAdapter);
                    }
                });

            }
        }).start();
    }
}
