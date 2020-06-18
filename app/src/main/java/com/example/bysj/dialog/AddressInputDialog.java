package com.example.bysj.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.bysj.OKHttpUtils;
import com.example.bysj.R;

import java.io.IOException;

import okhttp3.ResponseBody;

public class AddressInputDialog extends Dialog {
    private Button commitBt;
    private Activity activity;
    private EditText addressEd;
    private TextView addressTv;
    private String userType;
    public AddressInputDialog(@NonNull Context context, Activity activity, TextView addressTv) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.addressTv = addressTv;
        this.activity = activity;
        setContentView(R.layout.address_input_dialog);
        if(addressTv.getId()==R.id.user_info_address_tv){
            userType = "user";
        }else {
            userType = "shop";
        }
        init();
    }

    private void init(){
        commitBt = this.findViewById(R.id.shop_info_dialog_commit);
        addressEd = this.findViewById(R.id.shop_info_dialog_address_ed);
        initCommitBt();
    }

    private void initCommitBt(){
        commitBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final WaitDialog waitDialog = new WaitDialog(activity);
                final String newAddress = addressEd.getText().toString();
                if(newAddress.equals("")){
                    Toast.makeText(activity,"新地址不能为空",Toast.LENGTH_SHORT).show();
                    waitDialog.dismiss();
                    return;
                }
                if (newAddress.equals(activity.getSharedPreferences("info",Context.MODE_PRIVATE).getString("address","-1"))) {
                    Toast.makeText(activity,"新地址与当前地址相同",Toast.LENGTH_SHORT).show();
                    waitDialog.dismiss();
                    return;
                }
                waitDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        ResponseBody responseBody = null;
                        try {
                            responseBody = OKHttpUtils.postMessage("/"+userType+"/info/changeAddress.do","{\"newAddress\":\""+newAddress+"\"}");
                        } catch (IOException e) {
                            waitDialog.dismiss();
                            Looper.prepare();
                            Toast.makeText(activity,"连接超时",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                            return;
                        }
                        String re ="-1";
                        try {
                            re = responseBody.string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Looper.prepare();
                        if(re.equals("-1")){
                            waitDialog.dismiss();
                            Toast.makeText(activity,"修改失败",Toast.LENGTH_SHORT).show();
                        }else {
                            waitDialog.dismiss();
                            AddressInputDialog.this.dismiss();
                            Toast.makeText(activity,"修改成功",Toast.LENGTH_SHORT).show();
                            SharedPreferences sharedPreferences = activity.getSharedPreferences("info", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("address",newAddress);
                            editor.apply();
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    addressTv.setText(newAddress);
                                }
                            });
                        }
                        Looper.loop();
                    }
                }).start();
            }
        });
    }
}
