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
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.bysj.OKHttpUtils;
import com.example.bysj.R;

import java.io.IOException;

import okhttp3.ResponseBody;

public class PasswordChangeDialog extends Dialog {

    private EditText oldEd,newEd,newAgainEd;
    private Button commitBt;
    private Activity activity;
    private String userType;
    public PasswordChangeDialog(@NonNull Context context, Activity activity,String userType) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.activity = activity;
        setContentView(R.layout.change_password_dialog);
        this.userType = userType;
        init();
    }

    private void init(){
        oldEd = findViewById(R.id.shop_info_change_password_oldpassword_ed);
        newEd = findViewById(R.id.shop_info_change_password_newpassword_ed);
        newAgainEd = findViewById(R.id.shop_info_change_password_newpasswordagain_ed);
        commitBt = findViewById(R.id.shop_info_dialog_commit);
        commitBtInit();
    }

    private void commitBtInit(){
        commitBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String old = oldEd.getText().toString();
                final String newPsw = newEd.getText().toString();
                String newPswAgain = newAgainEd.getText().toString();
                final SharedPreferences sharedPreferences = activity.getSharedPreferences("info",Context.MODE_PRIVATE);
                //格式判断
                {
                    if(old.equals("")){
                        Toast.makeText(activity,"旧密码不能为空",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(newPsw.equals("")){
                        Toast.makeText(activity,"新密码不能为空",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(newPswAgain.equals("")){
                        Toast.makeText(activity,"重复密码不能为空",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String pas = sharedPreferences.getString("password","-1");
                    if(!pas.equals(old)){
                        Toast.makeText(activity,"旧密码不一致",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(newPsw.length()<7||newPsw.length()>32){
                        Toast.makeText(activity,"密码长度为7~32之间的字符",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(!newPsw.equals(newPswAgain)){
                        Toast.makeText(activity,"两次输入的新密码不一致",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(old.equals(newPsw)){
                        Toast.makeText(activity,"旧密码和新密码相同",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                final WaitDialog waitDialog = new WaitDialog(activity);
                waitDialog.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ResponseBody responseBody = null;
                        try {
                            responseBody = OKHttpUtils.postMessage("/"+userType+"/info/changePassword.do","{\"newPassword\":\""+newPsw+"\"}");
                        } catch (IOException e) {
                            e.printStackTrace();
                            waitDialog.dismiss();
                            Looper.prepare();
                            Toast.makeText(activity,"连接超时",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                        String re = "-1";
                        if(responseBody!=null)
                        {
                            try {
                                re = responseBody.string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if(re.equals("-1"))
                            {
                                waitDialog.dismiss();
                                Looper.prepare();
                                Toast.makeText(activity,"修改失败",Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }else if(re.equals("1")){
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("password",newPsw);
                                editor.apply();
                                waitDialog.dismiss();
                                Looper.prepare();
                                Toast.makeText(activity,"修改成功",Toast.LENGTH_SHORT).show();
                                PasswordChangeDialog.this.dismiss();
                                Looper.loop();
                            }
                        }
                        else {
                            Looper.prepare();
                            waitDialog.dismiss();
                            Toast.makeText(activity,"修改失败",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                    }
                }).start();

            }
        });
    }
}
