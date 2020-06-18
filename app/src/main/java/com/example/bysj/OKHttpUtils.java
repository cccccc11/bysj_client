package com.example.bysj;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OKHttpUtils {
    //private static String ip="http://192.168.1.8:8080";

    private static String ip = "http://192.168.124.4:8080";
    private static String sessionId ="";
    public static void setSessionID(String sessionid){
        sessionId = sessionid;
    }
    private OKHttpUtils(){}
    public static ResponseBody uploadPicture(String url, File file,String uuid) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file",file.getName(),
                        RequestBody.create(MediaType.parse("multipart/form-data"), file))
                .build();
        Request request = new Request.Builder()
                .addHeader("uuid",uuid)
                .addHeader("Connection", "close")
                .addHeader("Cookie",sessionId)
                .url(ip+url)
                .post(requestBody)
                .build();
        Response response = okHttpClient.newCall(request).execute();

        sessionId = getSessionId(response);

        if(!response.isSuccessful()){
            throw new IOException("Unexpected code " + response);
        }
        return response.body();
    }
    public static ResponseBody postMessage(String url,String jsonStr) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("cookie",sessionId)
                .addHeader("Connection", "close")
                .url(ip+url)
                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"),"jsonStr="+jsonStr))
                .build();
        Response response = okHttpClient.newCall(request).execute();
        sessionId = getSessionId(response);
        if(!response.isSuccessful()){
            throw new IOException("Unexpected code " + response);
        }
        return response.body();
    }
    public static Bitmap getPicture(String path) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("cookie",sessionId)
                .addHeader("Connection", "close")
                .url(ip+"/pictures/commodities/"+path)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        sessionId = getSessionId(response);
        if(!response.isSuccessful()){
            return null;
        }
        InputStream inputStream = response.body().byteStream();//得到图片的流
        return BitmapFactory.decodeStream(inputStream);
    }
    public static Bitmap getHeadPicture(String path) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("cookie",sessionId)
                .addHeader("Connection", "close")
                .url(ip+"/pictures/head/"+path)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        sessionId = getSessionId(response);
        if(!response.isSuccessful()){
            return null;
        }
        InputStream inputStream = response.body().byteStream();//得到图片的流
        return BitmapFactory.decodeStream(inputStream);
    }
    private static String getSessionId(Response response){
        if(!sessionId.equals("")){
            return sessionId;
        }
        //获取session
        List<String> cookies = response.headers().values("Set-Cookie");

        String session = cookies.get(0);

        return session.substring(0,session.indexOf(";"));
    }
    public static void sendSMS(String phone, final Activity activity){

        BmobSMS.requestSMSCode(phone, "", new QueryListener<Integer>() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null) {
                    Toast.makeText(activity,"发送验证码成功",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity,"发送验证码失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public static boolean verifySMS(String phone,String smsCode,Activity activity) throws TimeoutException {
        final Boolean[] b = {null};

        BmobSMS.verifySmsCode(phone, smsCode, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null) {
                    //成功
                    b[0] = Boolean.TRUE;
                }
                else {
                    //失败
                    b[0] = Boolean.FALSE;
                }
            }
        });
        for(int i = 0;i<10;i++){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(b[0]!=null) {
                return b[0];
            }
        }
        throw new TimeoutException();
    }


}
