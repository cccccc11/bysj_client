package com.example.bysj;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.ResponseBody;

public class ShopGoodsFragment extends Fragment {

    private View view;
    private ImageView addIv;
    private RecyclerView recyclerView;
    public Activity activity;

    private List<MyCommodity> myCommodityList;

    public ShopGoodsFragment(Activity activity){
        this.activity = activity;
    }

    public ShopGoodsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.shop_goods_frgment, container, false);
        init();
        return view;
    }


    //初始化控件
    private void init()
    {
        addIv = view.findViewById(R.id.shop_goods_add_iv);
        recyclerView = view.findViewById(R.id.shop_goods_content);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        initGetAllCommodity();
        initAddIv();
    }

    //初始化recyclerView
    private void initGetAllCommodity(){
        myCommodityList = new ArrayList<MyCommodity>();
        new Thread(new Runnable() {
            @Override
            public void run() {

                SharedPreferences sharedPreferences = activity.getSharedPreferences("info", Context.MODE_PRIVATE);
                String shopId = sharedPreferences.getString("id","-1");
                JSONObject jsonStr = new JSONObject();
                ResponseBody responseBody = null;
                jsonStr.put("shopId",shopId);
                String re ="";
                try {
                    responseBody = OKHttpUtils.postMessage("/shop/goods/getAllCommodity.do",jsonStr.toJSONString());
                    re = responseBody.string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (re==null||re.equals("")){
                    Looper.prepare();
                    Toast.makeText(view.getContext(),"链接超时",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                JSONArray jsonArray = JSON.parseArray(re);
                for(Object o:jsonArray){
                    JSONObject jsonObject = (JSONObject)o;
                    String picturePath ;
                    if((jsonObject.get("photosPath"))==null){
                        picturePath = "-1";
                    }else {
                        Object[] photosPath = ((JSONArray) jsonObject.get("photosPath")).toArray();

                        if(photosPath.length==0){
                            picturePath = "-1";
                        }else {
                            picturePath = photosPath[0].toString();
                        }
                    }
                    MyCommodity myCommodity = new MyCommodity(jsonObject.getInteger("commodityId"),picturePath,
                            jsonObject.getString("name"),
                            jsonObject.getString("state"),
                            jsonObject.getDouble("price"),
                            jsonObject.getInteger("num"),
                            jsonObject.getInteger("numNow"));
                    myCommodityList.add(myCommodity);
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommodityAdapter commodityAdapter = new CommodityAdapter(myCommodityList,activity);
                        recyclerView.setAdapter(commodityAdapter);
                    }
                });
            }
        }).start();

    }

    //获得所有信息
    private void getAllCommodity(){

    }

    //初始化添加控件
    private void initAddIv(){
        addIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getContext(),ShopAdd.class);
                startActivity(it);
            }
        });
    }

    public void refreshRecycle(){
        initGetAllCommodity();
    }
}
