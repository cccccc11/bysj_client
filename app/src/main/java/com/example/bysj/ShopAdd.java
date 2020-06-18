package com.example.bysj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.bysj.dialog.ShopDatePickDialog;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ShopAdd extends AppCompatActivity {

    private ImageView backIv,saveIv;
    private TextView startDateTv,endDateTv,nameAlterTv,numberAlterTv,priceAlterTv;
    private DatePickerDialog datePickerDialog;
    public Date startDate,endDate;
    private EditText nameEt,numberEt,priceEt,introduceEt;
    private RecyclerView recyclerView;
    private GridLayout picturesLL;

    private List<MyPicture> myPictures = new ArrayList<>();

    private static int PICTURE_WIDTH = 250;
    private static int PICTURE_HEIGHT= 250;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_add);

        //初始化
        init();
    }

    private void init(){
        backIv = findViewById(R.id.shop_goods_add_title_back);
        saveIv = findViewById(R.id.shop_goods_add_save);
        startDateTv = findViewById(R.id.shop_goods_add_date_start);
        endDateTv = findViewById(R.id.shop_goods_add_date_end);
        nameEt = findViewById(R.id.shop_goods_add_name_ed);
        numberEt = findViewById(R.id.shop_goods_add_number_ed);
        priceEt = findViewById(R.id.shop_goods_add_price_ed);
        introduceEt = findViewById(R.id.shop_goods_add_introduce_ed);
        nameAlterTv = findViewById(R.id.shop_goods_add_name_alter);
        numberAlterTv = findViewById(R.id.shop_goods_add_number_alter);
        priceAlterTv = findViewById(R.id.shop_goods_add_price_alter);
        //recyclerView = findViewById(R.id.shop_goods_add_picture_rlv);
        picturesLL = findViewById(R.id.shop_goods_add_pictures_ll);




        initImageView();
        initPickDate();
        initEdit();
        initPictures();


    }



    //初始化添加图片
    private void initPictures(){
//        myPictures.add(new MyPicture(getDrawable(R.mipmap.shop_add)));
//        PictureAdapter pictureAdapter = new PictureAdapter(myPictures,this);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setAdapter(pictureAdapter);
        //myPictures.add(new MyPicture(getDrawable(R.mipmap.shop_add)));
        ImageView imageView = new ImageView(this);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!PermissionUtils.hasStoragePermissions(ShopAdd.this)){
                    PermissionUtils.verifyStoragePermissions(ShopAdd.this);
                    PermissionUtils.verifyCameraPermissions(ShopAdd.this);
                    return;
                }
                if(!PermissionUtils.hasCameraPermissions(ShopAdd.this)){
                    PermissionUtils.verifyCameraPermissions(ShopAdd.this);
                    PermissionUtils.verifyStoragePermissions(ShopAdd.this);
                    return;
                }
                Matisse
                        .from(ShopAdd.this)
                        //选择视频和图片
                        //自定义选择选择的类型
                        .choose(MimeType.of(MimeType.JPEG))
                        //是否只显示选择的类型的缩略图，就不会把所有图片视频都放在一起，而是需要什么展示什么
                        //这两行要连用 是否在选择图片中展示照相 和适配安卓7.0 FileProvider
                        .capture(true)
                        .captureStrategy(new CaptureStrategy(true,"fileprovider"))
                        //有序选择图片 123456...
                        .countable(true)
                        //最大选择数量为9
                        .maxSelectable(9)
                        //选择方向
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                        //界面中缩略图的质量
                        .thumbnailScale(0.8f)
                        //黑色主题
                        .theme(R.style.Matisse_Dracula)
                        //Glide加载方式
                        .imageEngine(new GlideEngine())
                        .forResult(RequestCodeUtils.REQUEST_CODE_ADD_PICTURE);

            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(PICTURE_WIDTH,PICTURE_HEIGHT);
        params.setMargins(10,10,10,10);
        imageView.setLayoutParams(params);
        imageView.setImageDrawable(getDrawable(R.mipmap.shop_add));
        picturesLL.addView(imageView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodeUtils.REQUEST_CODE_ADD_PICTURE && resultCode == RESULT_OK) {
            List<Uri> result = Matisse.obtainResult(data);
            if(result.size()+myPictures.size()>8)
            {
                Toast.makeText(ShopAdd.this,"最多支持上传8张照片",Toast.LENGTH_SHORT).show();
                return;
            }
            for(Uri uri:result)
            {
                //myPictures.add(new MyPicture(Drawable.createFromPath(uri.toString())));
                //myPictures.add(new MyPicture(getDrawable(R.mipmap.shop_add)));
                String path = null;
                Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
                if (cursor == null) {
                    return;
                }
                if (cursor.moveToFirst()) {
                    try {
                        path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                cursor.close();
//                Bitmap bitmap = BitmapFactory.decodeFile(path);
//                bitmap.setWidth(50);
//                bitmap.setHeight(50);

                final ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(PICTURE_WIDTH,PICTURE_HEIGHT);
                params.setMargins(10,10,10,10);
                imageView.setLayoutParams(params);
                imageView.setImageDrawable(Drawable.createFromPath(path));
                myPictures.add(new MyPicture(Drawable.createFromPath(path),imageView,path));
                picturesLL.addView(imageView);
                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        for(MyPicture myPicture:myPictures)
                        {
                            if(myPicture.getImageView()==v)
                            {
//                                myPictures.remove(myPicture);
//                                picturesLL = new GridLayout(ShopAdd.this);
//                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(PICTURE_WIDTH,PICTURE_HEIGHT);
//                                ImageView imageView1 = new ImageView(ShopAdd.this);
//                                params.setMargins(10,10,10,10);
//                                imageView1.setLayoutParams(params);
//                                imageView1.setImageDrawable(getDrawable(R.mipmap.shop_add));
//                                picturesLL.addView(imageView1);
//                                for(MyPicture myPicture1:myPictures)
//                                {
//                                    final ImageView imageView = new ImageView(ShopAdd.this);
//                                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(PICTURE_WIDTH,PICTURE_HEIGHT);
//                                    params.setMargins(10,10,10,10);
//                                    imageView.setLayoutParams(params);
//                                    imageView.setImageDrawable(myPicture1.getDrawable());
//                                    picturesLL.addView(imageView);
//                                }
                                picturesLL.removeView(v);
                                myPictures.remove(myPicture);
                                return true;
                            }
                        }
                        return false;
                    }
                });
            }
//            PictureAdapter pictureAdapter = new PictureAdapter(myPictures,this);
//            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//            recyclerView.setLayoutManager(layoutManager);
//            recyclerView.setAdapter(pictureAdapter);

        }
    }


    //初始化退回和保存键
    private void initImageView(){
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(ShopAdd.this,Shop.class);
                startActivity(it);
            }
        });
        saveIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEt.getText().toString();
                if(name.equals(""))
                {
                    nameAlterTv.setText("名称不能为空");
                    return;
                }

                if(numberEt.getText().toString().equals("")||Integer.parseInt(numberEt.getText().toString())<1)
                {
                    numberAlterTv.setText("输入正确的数量");
                    return;
                }
                Integer number = Integer.parseInt(numberEt.getText().toString());

                if(priceEt.getText().toString().equals("")||Double.parseDouble(priceEt.getText().toString())<0.0)
                {
                    priceAlterTv.setText("输入正确价格");
                    return;
                }
                Double price = Double.parseDouble(priceEt.getText().toString());
                String introduce = introduceEt.getText().toString();
                java.sql.Date startDateSql,endDateSql =null;
                if(startDate != null)
                {
                    startDateSql = new java.sql.Date(startDate.getTime());
                }else {
                    Toast.makeText(ShopAdd.this,"选择开始时间",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(endDate != null)
                {
                    endDateSql = new java.sql.Date(endDate.getTime());
                }
                else {
                    Toast.makeText(ShopAdd.this,"选择结束时间",Toast.LENGTH_SHORT).show();
                    return;
                }
                JSONObject json = new JSONObject();
                json.put("name",name);
                json.put("number",number);
                json.put("price",price);
                json.put("introduce",introduce);
                json.put("startDate",startDateSql);
                json.put("endDate",endDateSql);
                final UUID uuid = UUID.randomUUID();
                json.put("path", uuid.toString());
                if (startDate.after(new Date()))
                {
                    json.put("state","0");
                }
                else {
                    json.put("state","1");
                }
                final String jsonStr = json.toJSONString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String re ="";
                        try {
                            for(MyPicture myPicture:myPictures){
                                final File file = new File(myPicture.getPath());
                                OKHttpUtils.uploadPicture("/shop/goods/uploadPicture.do",file,uuid.toString());
                            }
                            re = HttpUtils.sendPostMessage("/shop/goods/add.do",jsonStr);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        JSONObject result = JSON.parseObject(re);
                        if(result.getString("result").equals("1"))
                        {
                            Looper.prepare();
                            Toast.makeText(ShopAdd.this,result.getString("message")+",即将返回主界面",Toast.LENGTH_SHORT).show();
                            new Handler(new Handler.Callback() {
                                @Override
                                public boolean handleMessage(Message msg) {
                                    Intent it = new Intent(ShopAdd.this,Shop.class);
                                    startActivity(it);
                                    return true;
                                }
                            }).sendEmptyMessageDelayed(0x123,2000);//延时跳转
                            Looper.loop();
                        }else {
                            Looper.prepare();
                            Toast.makeText(ShopAdd.this,result.getString("message"),Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }).start();



                //Toast.makeText(ShopAdd.this,name+"\n"+number+"\n"+price+"\n"+introduce+"\n"+startDate.toString()+"\n"+endDate,Toast.LENGTH_SHORT).show();



            }
        });
    }

    //初始化选择日期
    private void initPickDate()
    {
        startDate = null;
        endDate = null;
        startDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                ShopDatePickDialog shopDatePickDialog = new ShopDatePickDialog(ShopAdd.this,ShopAdd.this,startDateTv,ShopAdd.this);
                shopDatePickDialog.show();
            }
        });
        endDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShopDatePickDialog shopDatePickDialog = new ShopDatePickDialog(ShopAdd.this,ShopAdd.this,endDateTv,ShopAdd.this);
                shopDatePickDialog.show();
            }
        });
    }


    //初始化编辑框
    private void initEdit() {
        View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    int id = v.getId();
                    switch (id) {
                        case R.id.shop_goods_add_name_ed: {
                            nameAlterTv.setText("");
                            break;
                        }
                        case R.id.shop_goods_add_number_ed: {
                            numberAlterTv.setText("");
                            break;
                        }
                        case R.id.shop_goods_add_price_ed: {
                            priceAlterTv.setText("");
                            break;
                        }
                    }
                }
            }
        };
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!nameEt.getText().toString().equals("")){
                    nameAlterTv.setText("");
                }
                if(!numberEt.getText().toString().equals("")){
                    numberAlterTv.setText("");
                }
                if(!priceEt.getText().toString().equals("")){
                    priceAlterTv.setText("");
                }
            }
        };

        nameEt.setOnFocusChangeListener(focusChangeListener);
        nameEt.addTextChangedListener(textWatcher);
        numberEt.setOnFocusChangeListener(focusChangeListener);
        numberEt.addTextChangedListener(textWatcher);
        priceEt.setOnFocusChangeListener(focusChangeListener);
        priceEt.addTextChangedListener(textWatcher);
    }
}
