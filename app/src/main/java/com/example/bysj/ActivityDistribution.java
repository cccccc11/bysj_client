package com.example.bysj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.bysj.dialog.WaitDialog;
import com.example.bysj.fragment.userFragment.UserMainFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;

public class ActivityDistribution extends AppCompatActivity {

    private Intent it;
    private String name,headURL;
    private Integer commodityId,num,numNow;

    private ImageView headIv,exitIv;
    private TextView nameTv,numTv,finishTv;
    private RecyclerView recyclerView;

    private List<Map<String,Object>> usersInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distribution);

        it = getIntent();
        name = it.getStringExtra("name");
        headURL = it.getStringExtra("headURL");
        commodityId = it.getIntExtra("commodityId",-1);
        num = it.getIntExtra("num",-1);
        numNow = it.getIntExtra("numNow",-1);

        init();

    }

    private void init(){
        headIv = findViewById(R.id.distribution_head_iv);
        nameTv = findViewById(R.id.distribution_commodity_name_tv);
        numTv = findViewById(R.id.distribution_commodity_num_tv);
        recyclerView = findViewById(R.id.distribution_commodity_content);
        exitIv = findViewById(R.id.distribution_commodity_exit);
        finishTv = findViewById(R.id.distribution_commodity_finish);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        initInfo();
        initUserInfo();
        initExit();
        initFinish();

    }

    private void initInfo(){
        if(headURL.equals("-1"))
        {
            headIv.setImageDrawable(ActivityDistribution.this.getDrawable(R.mipmap.default_picture));
        }
        else {
            headIv.setImageDrawable(ActivityDistribution.this.getDrawable(R.mipmap.default_picture));
            //从url中获取图片
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final Bitmap bitmap = OKHttpUtils.getPicture(headURL);
                        ActivityDistribution.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (bitmap == null) {
                                    headIv.setImageDrawable(ActivityDistribution.this.getDrawable(R.mipmap.default_picture));
                                } else {
                                    headIv.setImageBitmap(bitmap);
                                }
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        nameTv.setText(name);
        numTv.setText(numNow+" / "+num);
    }

    private void initUserInfo(){
        usersInfo = new ArrayList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                ResponseBody responseBody = null;
                try {
                    responseBody = OKHttpUtils.postMessage("/shop/goods/getAllOrdersByCommodityId",commodityId+"");
                } catch (IOException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(ActivityDistribution.this,"连接超时",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                String resultStr = null;
                try {
                    resultStr = responseBody.string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (resultStr == null){
                    Looper.prepare();
                    Toast.makeText(ActivityDistribution.this,"网络问题",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

                JSONArray resultArray = JSON.parseArray(resultStr);
                for(Object result:resultArray){
                    JSONObject resultJson = (JSONObject) result;
                    Map<String,Object> order = new HashMap<>();
                    order.put("userId",resultJson.getString("userId"));
                    order.put("commodityId",commodityId);
                    order.put("name",resultJson.getString("name"));
                    order.put("address",resultJson.getString("address"));
                    order.put("phone",resultJson.getString("phone"));
                    order.put("state",resultJson.getString("state"));
                    order.put("num",resultJson.getInteger("num"));
                    usersInfo.add(order);
                }
                ActivityDistribution.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(new AdapterGoods(ActivityDistribution.this,usersInfo));
                    }
                });
            }
        }).start();


    }

    private void initExit(){
        exitIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityDistribution.this.finish();
            }
        });
    }

    private void initFinish(){
        finishTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final WaitDialog waitDialog = new WaitDialog(ActivityDistribution.this);
                waitDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ResponseBody responseBody = null;
                        JSONObject requestJson = new JSONObject();
                        requestJson.put("commodityId",commodityId);
                        try {
                            responseBody = OKHttpUtils.postMessage("/shop/goods/finishOrder",requestJson.toJSONString());
                        } catch (IOException e) {
                            e.printStackTrace();
                            Looper.prepare();
                            Toast.makeText(ActivityDistribution.this,"连接超时",Toast.LENGTH_SHORT).show();
                            waitDialog.dismiss();
                            Looper.loop();
                        }

                        String resultStr = null;
                        try {
                            resultStr = responseBody.string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(resultStr == null){
                            Looper.prepare();
                            Toast.makeText(ActivityDistribution.this,"服务器错误",Toast.LENGTH_SHORT).show();
                            waitDialog.dismiss();
                            Looper.loop();
                        }
                        if(resultStr.equals("1")){
                            Looper.prepare();
                            waitDialog.dismiss();
                            Toast.makeText(ActivityDistribution.this,"完成配送成功，即将回到上一页面",Toast.LENGTH_SHORT).show();
                            new Handler(new Handler.Callback() {
                                @Override
                                public boolean handleMessage(Message msg) {
                                    ActivityDistribution.this.setResult(RESULT_OK);
                                    ActivityDistribution.this.finish();

                                    return true;
                                }
                            }).sendEmptyMessageDelayed(0x123,2000);//延时跳转
                            Looper.loop();
                        }else {
                            Looper.prepare();
                            Toast.makeText(ActivityDistribution.this,"完成配送失败,还有未配送人员",Toast.LENGTH_SHORT).show();
                            waitDialog.dismiss();
                            Looper.loop();
                        }
                    }
                }).start();

            }
        });
    }
}
