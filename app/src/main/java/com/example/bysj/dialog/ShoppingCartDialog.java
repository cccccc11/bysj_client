package com.example.bysj.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.example.bysj.OKHttpUtils;
import com.example.bysj.R;
import com.example.bysj.User;
import com.example.bysj.fragment.userFragment.UserShoppingCartFragment;

import java.io.IOException;
import java.util.Map;

import okhttp3.ResponseBody;

public class ShoppingCartDialog extends Dialog {

    private Context context;
    private Map<String,Object> parameter;
    private TextView deleteTv;

    public ShoppingCartDialog(@NonNull Context context, Map<String,Object> parameter) {

        super(context);
        this.context = context;
        this.parameter = parameter;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_shoppingcart_operation);

        Window dialogWindow = this.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager m = ((Activity) context).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth()); // 宽度设置为屏幕的百分之百

        init();

    }

    private void init(){
        deleteTv = findViewById(R.id.dialog_shoppingCart_operation_delete);

        initDelete();
    }

    private void initDelete(){
        deleteTv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    v.setBackground(context.getDrawable(R.drawable.rectangle_pressed_gray));
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    v.setBackground(context.getDrawable(R.drawable.rectangle));

                    final WaitDialog waitDialog = new WaitDialog(context);
                    waitDialog.show();
                    //删除购物车信息
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            ResponseBody responseBody = null;
                            JSONObject requestJson = new JSONObject();
                            requestJson.put("userId", parameter.get("userId"));
                            requestJson.put("commodityId", parameter.get("commodityId"));
                            try {
                                responseBody = OKHttpUtils.postMessage("/user/shoppingCart/deleteShoppingCart", requestJson.toJSONString());
                            } catch (IOException e) {
                                e.printStackTrace();
                                Looper.prepare();
                                Toast.makeText(context, "连接超时", Toast.LENGTH_SHORT).show();
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
                            if (resultStr == null) {
                                Looper.prepare();
                                Toast.makeText(context, "未知错误", Toast.LENGTH_SHORT).show();
                                waitDialog.dismiss();
                                Looper.loop();
                            }
                            Looper.prepare();
                            if (resultStr.equals("1")) {

                                Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                            }
                            if (resultStr.equals("0")) {
                                Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
                            }
                            waitDialog.dismiss();
                            ShoppingCartDialog.this.dismiss();
                            ((UserShoppingCartFragment)((User)context).shoppingCartFragment).refreshRecycle();
                            Looper.loop();
                        }
                    }).start();
                }
                return true;
            }
        });
    }
}
