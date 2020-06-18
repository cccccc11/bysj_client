package com.example.bysj.fragment.userFragment;

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

import com.example.bysj.BitmapUtils;
import com.example.bysj.LoginActivity;
import com.example.bysj.OKHttpUtils;
import com.example.bysj.R;
import com.example.bysj.RequestCodeUtils;
import com.example.bysj.dialog.AddressInputDialog;
import com.example.bysj.dialog.ShopDatePickDialog;
import com.example.bysj.dialog.NameInputDialog;
import com.example.bysj.dialog.PasswordChangeDialog;
import com.example.bysj.dialog.PhoneInputDialog;
import com.example.bysj.dialog.SexInputDialog;
import com.example.bysj.dialog.UserInfoBirthDayChangeDialog;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.IOException;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class UserInfoFragment extends Fragment {

    private View view;
    private Activity activity;
    private ImageView phoneIv,addressIv,birthdayIv,sexIv,nameIv,headIv;
    private TextView phoneTv,addressTv,birthdayTv,sexTv,nameTv,nameTitleTv,changePasswordTv,idTv,exit;

    public UserInfoFragment(Activity activity){
        this.activity = activity;
    }

    public UserInfoFragment(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.user_info_fragment, container, false);

        init();

        return view;
    }

    private void init(){
        phoneIv = view.findViewById(R.id.user_info_phone_iv);
        addressIv = view.findViewById(R.id.user_info_address_iv);
        birthdayIv = view.findViewById(R.id.user_info_birthday_iv);
        sexIv = view.findViewById(R.id.user_info_sex_iv);
        phoneTv = view.findViewById(R.id.user_info_phone_tv);
        addressTv = view.findViewById(R.id.user_info_address_tv);
        birthdayTv = view.findViewById(R.id.user_info_birthday_tv);
        sexTv = view.findViewById(R.id.user_info_sex_tv);
        exit = view.findViewById(R.id.user_info_exit);
        nameIv = view.findViewById(R.id.user_info_name_iv);
        nameTv = view.findViewById(R.id.user_info_name_tv);
        nameTitleTv = view.findViewById(R.id.user_info_name_title_tv);
        changePasswordTv = view.findViewById(R.id.user_info_change_password_tv);
        idTv = view.findViewById(R.id.user_info_id_tv);
        headIv = view.findViewById(R.id.user_info_head_iv);
        infoInit();
        initExit();
        changeInfoInit();
        changePasswordInit();
        initHeadIv();
    }

    private void initExit(){
        exit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Intent it = new Intent(activity, LoginActivity.class);
                startActivity(it);
                SharedPreferences sharedPreferences = activity.getSharedPreferences("info",Context.MODE_PRIVATE);
                sharedPreferences.edit().clear().apply();
                activity.finish();
            }
        });
    }

    private void changeInfoInit(){
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.user_info_address_iv:{
                        AddressInputDialog addressInputDialog = new AddressInputDialog(activity,activity,addressTv);
                        addressInputDialog.show();
                        break;
                    }
                    case R.id.user_info_birthday_iv:{
                        String userId = activity.getSharedPreferences("info",Context.MODE_PRIVATE).getString("id","-1");
                        UserInfoBirthDayChangeDialog userInfoBirthDayChangeDialog = new UserInfoBirthDayChangeDialog(activity,userId,birthdayTv);
                        userInfoBirthDayChangeDialog.show();
                        break;
                    }
                    case R.id.user_info_phone_iv:{
                        PhoneInputDialog phoneInputDialog = new PhoneInputDialog(activity,activity,phoneTv);
                        phoneInputDialog.show();
                        break;
                    }
                    case R.id.user_info_sex_iv:{
                        SexInputDialog sexInputDialog = new SexInputDialog(activity,activity,sexTv);
                        sexInputDialog.show();
                        break;
                    }
                    case R.id.user_info_name_iv:{
                        NameInputDialog nameInputDialog = new NameInputDialog(activity,activity,nameTv,nameTitleTv);
                        nameInputDialog.show();
//                        AlertDialog.Builder alterDialog = new AlertDialog.Builder(activity);
//                        alterDialog.setView(R.layout.name_input_dialog);
//                        alterDialog.show();
                        break;
                    }
                }
            }
        };
        addressIv.setOnClickListener(onClickListener);
        birthdayIv.setOnClickListener(onClickListener);
        phoneIv.setOnClickListener(onClickListener);
        sexIv.setOnClickListener(onClickListener);
        nameIv.setOnClickListener(onClickListener);
    }

    private void changePasswordInit(){
        changePasswordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PasswordChangeDialog passwordChangeDialog = new PasswordChangeDialog(activity,activity,"user");
                passwordChangeDialog.show();
            }
        });
    }

    private void infoInit(){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("info",Context.MODE_PRIVATE);

        idTv.setText(sharedPreferences.getString("id",""));
        nameTv.setText(sharedPreferences.getString("name",""));
        nameTitleTv.setText(sharedPreferences.getString("name",""));
        phoneTv.setText(sharedPreferences.getString("phone",""));
        addressTv.setText(sharedPreferences.getString("address",""));
        birthdayTv.setText(sharedPreferences.getString("birthday",""));

        if(sharedPreferences.getString("sex","").equals("male")){
            sexTv.setText("男");
        }else {
            sexTv.setText("女");
        }

        final String headURL = sharedPreferences.getString("headURL","");
        if(!headURL.equals("")){

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final Bitmap bitmap = OKHttpUtils.getHeadPicture(headURL);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                headIv.setImageBitmap(BitmapUtils.changeBitmapSize(bitmap));
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }



    }

    private void initHeadIv(){
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
        if(requestCode==RequestCodeUtils.REQUEST_CODE_ADD_HEAD && resultCode == RESULT_OK){

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
                            OKHttpUtils.uploadPicture("/user/info/uploadHead.do", BitmapUtils.getFile(finalBitmap),"");
                        } catch (IOException e) {
                            e.printStackTrace();
                            Looper.prepare();
                            Toast.makeText(activity,"连接超时",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String id  = activity.getSharedPreferences("info",Context.MODE_PRIVATE).getString("id","");
                                activity.getSharedPreferences("info",Context.MODE_PRIVATE).edit().putString("user"+id+".jpg","").apply();
                                headIv.setImageBitmap(finalBitmap);
                            }
                        });
                    }
                }).start();
        }
    }

}
