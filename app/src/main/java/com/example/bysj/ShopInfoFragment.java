package com.example.bysj;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.bysj.dialog.AddressInputDialog;
import com.example.bysj.dialog.NameInputDialog;
import com.example.bysj.dialog.PasswordChangeDialog;
import com.example.bysj.dialog.PhoneInputDialog;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;

public class ShopInfoFragment extends Fragment {

    private View view;
    private TextView phoneTv,addressTv,nameTv,nameTitleTv,idTv,changePswTv;
    private ImageView headIv,nameIv,phoneIv,addressIv;
    private Activity activity;

    private String headURL,id,name,password,address,phone;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.shop_info_fragment, container, false);

        //初始化控件
        init();

        return view;
    }

    public ShopInfoFragment(Activity activity){
        this.activity = activity;
    }

    public ShopInfoFragment(){

    }

    //初始化控件
    private void init(){
        phoneTv = view.findViewById(R.id.shop_info_phone_tv);
        addressTv = view.findViewById(R.id.shop_info_address_tv);
        headIv = view.findViewById(R.id.shop_info_head_iv);
        nameTv = view.findViewById(R.id.shop_info_name_tv);
        nameTitleTv = view.findViewById(R.id.shop_info_title_name_tv);
        idTv = view.findViewById(R.id.shop_info_id_tv);
        nameIv = view.findViewById(R.id.shop_info_name_iv);
        phoneIv = view.findViewById(R.id.shop_info_phone_iv);
        addressIv = view.findViewById(R.id.shop_info_address_iv);
        changePswTv = view.findViewById(R.id.shop_info_change_password);

        getInfo();
        initHead();
        initChange();
        initChangePassword();
    }

    //初始化修改密码
    private void initChangePassword(){
        changePswTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PasswordChangeDialog(activity,activity,"shop").show();
            }
        });
    }



    //获取商铺信息
    private void getInfo(){
        final SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("id","-1");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                ResponseBody responseBody;
//                try {
//                    responseBody = OKHttpUtils.postMessage("",id+"");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Looper.prepare();
//                    Toast.makeText(view.getContext(),"连接超时",Toast.LENGTH_SHORT).show();
//                    Looper.loop();
//                    return;
//                }
//                String re = "";
//                try {
//                    re = responseBody.string();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                JSONObject object = JSON.parseObject(re);
//                name = object.getString("name");
//                password = object.getString("password");
//                headURL = object.getString("headURL");
//                phone = object.getString("phone");
//                address = object.getString("address");
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString("name",name);
//                editor.putString("password",password);
//                editor.putString("headURL",headURL);
//                editor.putString("phone",phone);
//                editor.putString("address",address);
//                editor.apply();
//
//            }
//        }).start();
        headURL = sharedPreferences.getString("headURL","-1");
        if(!headURL.equals("-1")){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = null;
                    try {
                        bitmap = OKHttpUtils.getHeadPicture(headURL);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                    if(bitmap==null) {
                        return;
                    }
                    Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888,true);
                    mutableBitmap.setWidth(150);
                    mutableBitmap.setWidth(150);
                    final Bitmap finalBitmap = mutableBitmap;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            headIv.setImageBitmap(finalBitmap);
                        }
                    });
                }
            }).start();
        }

        idTv.setText(sharedPreferences.getString("id",""));
        nameTv.setText(sharedPreferences.getString("name",""));
        nameTitleTv.setText(sharedPreferences.getString("name",""));
        phoneTv.setText(sharedPreferences.getString("phone",""));
        addressTv.setText(sharedPreferences.getString("address",""));
    }

    //初始化头像
    private void initHead(){
        headIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Matisse
                        .from(activity)
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
                        .maxSelectable(1)
                        //选择方向
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                        //界面中缩略图的质量
                        .thumbnailScale(0.8f)
                        //黑色主题
                        .theme(R.style.Matisse_Zhihu)
                        //Glide加载方式
                        .imageEngine(new GlideEngine())
                        .forResult(RequestCodeUtils.REQUEST_CODE_ADD_HEAD);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RequestCodeUtils.REQUEST_CODE_ADD_HEAD && resultCode == Activity.RESULT_OK){

            List<Uri> result = Matisse.obtainResult(data);
            String path = null;
            Cursor cursor = activity.getContentResolver().query(result.get(0), null, null, null, null);
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
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            bitmap = BitmapUtils.changeBitmapSize(bitmap);

            final Bitmap finalBitmap = bitmap;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        OKHttpUtils.uploadPicture("/shop/goods/uploadHead.do", BitmapUtils.getFile(finalBitmap),"");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Looper.prepare();
                        Toast.makeText(activity,"连接超时",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            headIv.setImageBitmap(finalBitmap);
                        }
                    });
                }
            }).start();



        }
    }

    //初始化修改按钮
    private void initChange(){
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.shop_info_name_iv:{
                        NameInputDialog inputDialog = new NameInputDialog(activity,activity,nameTv,nameTitleTv);
                        inputDialog.show();
                        break;
                    }
                    case R.id.shop_info_address_iv:{
                        AddressInputDialog addressInputDialog = new AddressInputDialog(activity,activity,addressTv);
                        addressInputDialog.show();
                        break;
                    }
                    case R.id.shop_info_phone_iv:{
                        PhoneInputDialog phoneInputDialog = new PhoneInputDialog(activity,activity,phoneTv);
                        phoneInputDialog.show();
                        break;
                    }
                }
            }
        };
        nameIv.setOnClickListener(onClickListener);
        phoneIv.setOnClickListener(onClickListener);
        addressIv.setOnClickListener(onClickListener);
    }
}
