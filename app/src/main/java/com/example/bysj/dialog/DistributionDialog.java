package com.example.bysj.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Looper;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.example.bysj.OKHttpUtils;
import com.example.bysj.R;
import com.example.bysj.User;
import com.example.bysj.fragment.userFragment.UserOrderFragment;

import java.io.IOException;
import java.util.Map;

import okhttp3.ResponseBody;

public class DistributionDialog extends Dialog {

    private TextView complicationTv;
    private Context context;

    private boolean can = true;
    private Map<String,Object> parameter;

    public DistributionDialog(@NonNull Context context,Map<String,Object> parameter) {
        super(context);

        this.context = context;
        this.parameter = parameter;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_distribution);

        Window dialogWindow = this.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager m = ((Activity) context).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth()); // 宽度设置为屏幕的百分之百

        init();
    }

    private void init(){
        complicationTv = findViewById(R.id.dialog_distribution_complication_tv);

        if(!parameter.get("state").equals("0")){
            can = false;
            complicationTv.setTextColor(Color.GRAY);
        }

        complicationTv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(!can){
                    return false;
                }

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    v.setBackground(context.getDrawable(R.drawable.rectangle_pressed_gray));
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    v.setBackground(context.getDrawable(R.drawable.rectangle));
                    if(!can){
                        return false;
                    }
                    final WaitDialog waitDialog = new WaitDialog(context);
                    waitDialog.show();
                    //删除订单
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            ResponseBody responseBody = null;
                            JSONObject requestJson = new JSONObject();
                            requestJson.put("userId",parameter.get("userId"));
                            requestJson.put("commodityId",parameter.get("commodityId"));
                            requestJson.put("state","1");
                            try {
                                responseBody = OKHttpUtils.postMessage("/shop/goods/updateOrderState",requestJson.toJSONString());
                            } catch (IOException e) {
                                e.printStackTrace();
                                Looper.prepare();
                                Toast.makeText(context,"连接超时",Toast.LENGTH_SHORT).show();
                                waitDialog.dismiss();
                                Looper.loop();
                            }
                            String resultStr = null;
                            try {
                                resultStr = responseBody.string();
                            } catch (IOException e) {
                                e.printStackTrace();
                                waitDialog.dismiss();
                            }
                            if(resultStr == null){
                                Looper.prepare();
                                Toast.makeText(context,"未知错误",Toast.LENGTH_SHORT).show();
                                waitDialog.dismiss();
                                Looper.loop();
                            }
                            Looper.prepare();
                            if(resultStr.equals("1")){
                                Toast.makeText(context,"成功",Toast.LENGTH_SHORT).show();
                                ((Activity)context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((TextView)parameter.get("stateTv")).setTextColor(Color.GREEN);
                                        ((TextView)parameter.get("stateTv")).setText("已配送");
                                    }
                                });
                            }
                            if(resultStr.equals("0")){
                                Toast.makeText(context,"失败",Toast.LENGTH_SHORT).show();
                            }
                            waitDialog.dismiss();
                            DistributionDialog.this.dismiss();
                            Looper.loop();
                        }
                    }).start();
                }
                return true;
            }
        });
    }
}
