package com.example.bysj;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import okhttp3.ResponseBody;


public class HttpUtils {
    private static URL url;
    private static String encode = "utf-8";
    //private static String ip="http://10.0.2.2:8080";
    //private static String ip="http://192.168.1.4:8080";
    private static String ip = "http://192.168.1.2:8080";
    private static String sessionId = null;

    public static String sendPostMessage(String path,String message) throws Exception {

        ResponseBody re =OKHttpUtils.postMessage(path,message);

//        byte[] data = jsonStr.getBytes();
//        try {
//            url = new URL(ip+path);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//
//            conn.setConnectTimeout(3000);
//            conn.setDoInput(true);
//            conn.setDoOutput(true);
//            conn.setRequestMethod("POST");
//
//            //是否使用缓存
//            conn.setUseCaches(false);
//            //表示设置请求体的类型是文本类型
//            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            conn.setRequestProperty("Content-Length", String.valueOf(data.length));
//
//            if (sessionId !=null)
//            {
//                conn.setRequestProperty("Cookie",sessionId);
//            }
//
//            //获得输出流，向服务器输出数据
//            OutputStream outputStream = conn.getOutputStream();
//            outputStream.write(data,0,data.length);
//            int responseCode = conn.getResponseCode();
//            if(responseCode==HttpURLConnection.HTTP_OK)
//            {
//                if (sessionId==null)
//                {
//                    String cookieval = conn.getHeaderField("Set-Cookie");
//                    sessionId = cookieval.substring(0, cookieval.indexOf(";"));
//                    OKHttpUtils.setSessionID(sessionId);
//                }
//                return changeInputStream(conn.getInputStream());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw e;
//        }
        return re.string();
    }
    private static String changeInputStream(InputStream inputStream)
    {
        //通常叫做内存流，写在内存中的
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        String result = "";
        if(inputStream != null){
            try {
                while((len = inputStream.read(data))!=-1){
                    data.toString();

                    outputStream.write(data, 0, len);
                }
                //result是在服务器端设置的doPost函数中的
                result = new String(outputStream.toByteArray(),encode);
                outputStream.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return result;
    }

    public static Bitmap getYZM() throws IOException {
        try {
            url = new URL(ip+"/YZM/getYZM.do");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            //是否使用缓存
            conn.setUseCaches(false);
            //表示设置请求体的类型是文本类型
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length","");

            if (sessionId !=null)
            {
                conn.setRequestProperty("Cookie",sessionId);
                OKHttpUtils.setSessionID(sessionId);
            }


            //获得输出流，向服务器输出数据
            OutputStream outputStream = conn.getOutputStream();
            byte[] data={};
            outputStream.write(data,0,data.length);
            int responseCode = conn.getResponseCode();
            if(responseCode==HttpURLConnection.HTTP_OK)
            {
                if (sessionId==null)
                {
                    String cookieval = conn.getHeaderField("Set-Cookie");
                    sessionId = cookieval.substring(0, cookieval.indexOf(";"));
                }
                return BitmapFactory.decodeStream(conn.getInputStream());
            }
        } catch (IOException e) {
            throw e;
        }
        return null;
    }

    private static Bitmap bytesToBitmap(InputStream inputStream)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        String result = "";
        if(inputStream != null){
            try {
                while((len = inputStream.read(data))!=-1){
                    data.toString();

                    outputStream.write(data, 0, len);
                }
                //result是在服务器端设置的doPost函数中的
                outputStream.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // png类型
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        return bitmap;
    }
}

