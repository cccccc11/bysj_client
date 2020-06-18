package com.example.bysj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.bysj.bean.Commodity;
import com.example.bysj.dialog.PayDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;

public class CommodityInfo extends AppCompatActivity {

    private Intent commodity;
    private TextView moneyTv,nameTv,shopNameTv,numberTv,startDateTv,endDateTv,introduceTv,photoNumTv,payTv,shoppingCartTv;
    private EditText numEd;
    private ImageView photoIv,exitIv;
    private Bitmap[] photos;
    private String[] photosPath;
    private int photoSize,photoSizeNow,number,numNow;
    private boolean hasPhoto;
    private LinearLayout numLL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commodity_info);
        commodity = getIntent();
        init();
    }

    private void init(){
        moneyTv = findViewById(R.id.user_main_info_money);
        nameTv = findViewById(R.id.user_main_info_name);
        shopNameTv = findViewById(R.id.user_main_info_shop_name);
        numberTv = findViewById(R.id.user_main_info_number);
        startDateTv = findViewById(R.id.user_main_info_start_date);
        endDateTv = findViewById(R.id.user_main_info_end_date);
        introduceTv = findViewById(R.id.user_main_info_introduce);
        photoIv = findViewById(R.id.user_main_info_photo);
        photoNumTv = findViewById(R.id.user_main_info_photo_num);
        payTv = findViewById(R.id.user_main_info_pay);
        shoppingCartTv = findViewById(R.id.user_main_info_add_shopping_cart);
        numEd = findViewById(R.id.user_main_info_addnums);
        numLL = findViewById(R.id.user_main_info_addnums_ll);

        exitIv = findViewById(R.id.user_main_info_exit_iv);

        moneyTv.setText(commodity.getDoubleExtra("price",-1)+"");
        nameTv.setText(commodity.getStringExtra("name"));
        shopNameTv.setText(commodity.getStringExtra("shopName"));
        number = commodity.getIntExtra("number",-1);
        numNow = commodity.getIntExtra("numNow",-1);
        numberTv.setText(numNow+"/"+number);
        startDateTv.setText(commodity.getStringExtra("startDate"));
        endDateTv.setText(commodity.getStringExtra("endDate"));
        introduceTv.setText(commodity.getStringExtra("introduce"));

        hasPay();

        initPay();
        initPhotos();
        initExit();
        initShoppingCart();
    }

    private void initPhotos(){

        hasPhoto = true;

        photoSizeNow = 1;
        photosPath = commodity.getStringArrayExtra("photosPath");
        if(photosPath==null){
            photoSize = 1;
            photoIv.setImageDrawable(getDrawable(R.mipmap.default_picture));
            hasPhoto = false;
        }else {
            photoSize = photosPath.length;
        }
        if(!hasPhoto){
            return;
        }
        photoNumTv.setText(photoSizeNow+"/"+photoSize);
        photos = new Bitmap[photoSize];
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CommodityInfo.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            photoIv.setImageDrawable(getDrawable(R.mipmap.default_picture));
                        }
                    });
                    photos[0] = OKHttpUtils.getPicture(photosPath[0]);
                    int i = 5;
                    while (photos[0]==null&&i>0){
                        photos[0] = OKHttpUtils.getPicture(photosPath[0]);
                        i--;
                    }
                    if(photos[0]!=null){
                        CommodityInfo.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                photoIv.setImageBitmap(BitmapUtils.changeBitmapSizeByWidthAndHeight(photos[0],300,300));
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                //右滑
                if(e2.getX()>e1.getX()){
                    if(photoSizeNow==1){
                        return false;
                    }else {
                        photoSizeNow--;
                        photoIv.setImageBitmap(BitmapUtils.changeBitmapSizeByWidthAndHeight(photos[photoSizeNow-1],300,300));
                        photoNumTv.setText(photoSizeNow+"/"+photoSize);
                    }
                    return true;
                }

                //左滑
                if(e1.getX()>e2.getX()){
                    if(photoSizeNow==photoSize){
                        return false;
                    }else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final int index = photoSizeNow++;

                                if(photos[index]!=null){

                                    CommodityInfo.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            photoIv.setImageBitmap(BitmapUtils.changeBitmapSizeByWidthAndHeight(photos[index],300,300));
                                            photoNumTv.setText(photoSizeNow+"/"+photoSize);
                                        }
                                    });
                                    return;
                                }else {

                                    CommodityInfo.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            photoIv.setImageDrawable(getDrawable(R.mipmap.default_picture));
                                            photoNumTv.setText(photoSizeNow+"/"+photoSize);
                                        }
                                    });
                                }
                                try {
                                    photos[index] = OKHttpUtils.getPicture(photosPath[index]);
                                    int i = 5;
                                    while (photos[index]==null&&i>0){
                                        photos[index] = OKHttpUtils.getPicture(photosPath[index]);
                                        i--;
                                    }
                                    if(photos[index]!=null){
                                        CommodityInfo.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                photoIv.setImageBitmap(BitmapUtils.changeBitmapSizeByWidthAndHeight(photos[index],300,300));
                                            }
                                        });
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                    return true;
                }
                return false;
            }
        });

        photoIv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    private void initExit(){
        exitIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommodityInfo.this.finish();
            }
        });
    }

    private void hasPay(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonStr = new JSONObject();
                int commodityId = commodity.getIntExtra("id",-1);
                jsonStr.put("commodityId",commodityId);
                String userId = getSharedPreferences("info", Context.MODE_PRIVATE).getString("id","-1");
                if(userId.equals("-1")){
                    Looper.prepare();
                    Toast.makeText(CommodityInfo.this,"获取id错误",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                jsonStr.put("userId",userId);
                ResponseBody responseBody = null;
                try {
                    responseBody = OKHttpUtils.postMessage("/user/hasPay.do",jsonStr.toJSONString());
                } catch (IOException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(CommodityInfo.this, "连接超时", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                if(responseBody == null) {
                    Looper.prepare();
                    Toast.makeText(CommodityInfo.this, "获取结果失败", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                String re = null;
                try {
                    re = responseBody.string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(re==null){
                    Looper.prepare();
                    Toast.makeText(CommodityInfo.this, "获取结果失败", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }else if(re.equals("1")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            payTv.setText("已加入");
                            payTv.setTextColor(Color.parseColor("#CCCCCC"));
                            shoppingCartTv.setText("");
                        }
                    });
                }else if(re.equals("-1")){
                    hasAddToShoppingCart();
                }
            }
        }).start();
    }

    private void initPay(){

        payTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(payTv.getText().equals("已加入")){
                    return;
                }
                Integer addNums = null;
                try{
                    addNums = Integer.parseInt(numEd.getText().toString());
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(addNums==null){
                    Toast.makeText(getApplicationContext(),"输入正确的数量",Toast.LENGTH_SHORT).show();
                    return;
                }
                Integer leftNum = number-numNow;
                if (addNums>leftNum){
                    Toast.makeText(CommodityInfo.this,"库存不足",Toast.LENGTH_SHORT).show();
                    return;
                }
                PayDialog payDialog = new PayDialog(CommodityInfo.this,CommodityInfo.this,payTv,commodity.getIntExtra("id",-1),addNums);
                payDialog.show();
            }
        });
    }

    private void initShoppingCart(){
        shoppingCartTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shoppingCartTv.getText().equals("")||shoppingCartTv.getText().equals("已加入购物车")){
                    return;
                }else {
                    addToShoppingCart();
                }
            }
        });
    }

    private void hasAddToShoppingCart(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonStr = new JSONObject();
                int commodityId = commodity.getIntExtra("id",-1);
                jsonStr.put("commodityId",commodityId);
                String userId = getSharedPreferences("info", Context.MODE_PRIVATE).getString("id","-1");
                if(userId.equals("-1")){
                    Looper.prepare();
                    Toast.makeText(CommodityInfo.this,"获取id错误",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                jsonStr.put("userId",userId);
                ResponseBody responseBody = null;
                try {
                    responseBody = OKHttpUtils.postMessage("/user/hasAddToShoppingCart.do",jsonStr.toJSONString());
                } catch (IOException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(CommodityInfo.this, "连接超时", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                if(responseBody == null) {
                    Looper.prepare();
                    Toast.makeText(CommodityInfo.this, "获取结果失败", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                String re = null;
                try {
                    re = responseBody.string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(re==null){
                    Looper.prepare();
                    Toast.makeText(CommodityInfo.this, "获取结果失败", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }else if(re.equals("1")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            shoppingCartTv.setText("已加入购物车");
                            shoppingCartTv.setTextColor(Color.parseColor("#CCCCCC"));
                        }
                    });
                }
            }
        }).start();
    }

    private void addToShoppingCart(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (shoppingCartTv.getText().equals("已经加入购物车")){
                    return;
                }
                ResponseBody responseBody = null;
                JSONObject jsonStr = new JSONObject();
                jsonStr.put("userId",getSharedPreferences("info",Context.MODE_PRIVATE).getString("id","-1"));
                jsonStr.put("commodityId",commodity.getIntExtra("id",-1));
                Integer addNum = null;
                try{
                     addNum = Integer.parseInt(numEd.getText().toString());
                }catch (Exception e){
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(),"输入正确的数量",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

                jsonStr.put("num",addNum);
                try {
                    responseBody = OKHttpUtils.postMessage("/user/addToShoppingCart.do",jsonStr.toJSONString());
                } catch (IOException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(),"连接超时",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                try {
                    String re = responseBody.string();
                    if(re.equals("-1")){
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(),"加入购物车失败",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),"加入购物车成功",Toast.LENGTH_SHORT).show();
                                shoppingCartTv.setText("已经加入购物车");
                                shoppingCartTv.setTextColor(Color.parseColor("#CCCCCC"));
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RequestCodeUtils.RESULT_CODE_PAY_SUCCESS){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(CommodityInfo.this, "加入团购成功", Toast.LENGTH_SHORT).show();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            payTv.setText("已加入");
                            payTv.setTextColor(Color.parseColor("#CCCCCC"));
                            shoppingCartTv.setText("");
                        }
                    });
                    Looper.loop();
                }
            }).start();

        }
        if (requestCode==RequestCodeUtils.RESULT_CODE_PAY_FAILED){
            Toast.makeText(this, "加入团购失败", Toast.LENGTH_SHORT).show();
            return;
        }
        if (requestCode==RequestCodeUtils.RESULT_CODE_PAY_HASPAY){
            Toast.makeText(this, "已经加入团购了", Toast.LENGTH_SHORT).show();
            return;
        }
        if(requestCode == RequestCodeUtils.RESULT_CODE_PAY_NOTENOUGH){
            Toast.makeText(this, "商品剩余的数量不足", Toast.LENGTH_SHORT).show();

        }
    }
}
