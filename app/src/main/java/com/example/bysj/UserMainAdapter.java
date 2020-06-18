package com.example.bysj;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.alibaba.fastjson.JSONArray;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class UserMainAdapter extends RecyclerView.Adapter<UserMainAdapter.ViewHolder>{

    private List<Map<String,Object>> commodityList;
    private Activity activity;

    static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView nameTv,priceTv,numTv,endDateTv;
        public ImageView imageView;
        private Activity activity;
        private LinearLayout linearLayout;
        public Map<String,Object> commodity;
        View view;
        public String state;
        public ViewHolder (View view,Activity activity)
        {
            super(view);
            this.activity = activity;
            this.view = view;
            nameTv = view.findViewById(R.id.user_main_adapter_name);
            priceTv = view.findViewById(R.id.user_main_adapter_price);
            numTv = view.findViewById(R.id.user_main_adapter_num);
            endDateTv = view.findViewById(R.id.user_main_adapter_end_date);
            imageView = view.findViewById(R.id.user_main_adapter_iv);
            linearLayout = view.findViewById(R.id.user_main_adapter_ll);
            init();
        }

        //初始化控件
        private void init(){
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (state.equals("2")){
                        Toast.makeText(activity,"团购已结束",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent it = new Intent(view.getContext(),CommodityInfo.class);
                    it.putExtra("id",(Integer) commodity.get("id"));
                    it.putExtra("shopId", (String) commodity.get("shopId"));
                    it.putExtra("name", (String) commodity.get("name"));
                    it.putExtra("number", (Integer) commodity.get("number"));
                    it.putExtra("numNow", (Integer) commodity.get("numNow"));
                    it.putExtra("startDate",commodity.get("startDate").toString());
                    it.putExtra("endDate",commodity.get("endDate").toString());
                    it.putExtra("state",commodity.get("state").toString());
                    it.putExtra("introduce",commodity.get("introduce").toString());
                    it.putExtra("price", (Double) commodity.get("price"));

                    it.putExtra("photosPath", (String[]) commodity.get("photosPath"));
                    it.putExtra("shopName",commodity.get("shopName").toString());
                    view.getContext().startActivity(it);
                }
            });
        }

    }

    public UserMainAdapter(List<Map<String,Object>> commodityList,Activity activity){
        this.commodityList = commodityList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_main_adapter,parent,false);
        return new UserMainAdapter.ViewHolder(view,activity);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Map<String,Object> commodity = commodityList.get(position);
        holder.commodity = commodity;
        holder.nameTv.setText(commodity.get("name")+"");
        holder.priceTv.setText(commodity.get("price")+"");
        holder.numTv.setText(commodity.get("numNow")+"/"+commodity.get("number"));

        if(commodity.get("state").equals("2")){
            holder.state = "2";
            holder.endDateTv.setText("已结束");
            holder.endDateTv.setTextColor(Color.GRAY);
        }
        if (commodity.get("state").equals("1")){
            holder.state = "1";
            holder.endDateTv.setText(commodity.get("endDate").toString());
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Object[] photosPath = (Object[]) commodity.get("photosPath");
                if(photosPath==null||photosPath.length==0){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.imageView.setImageDrawable(activity.getDrawable(R.mipmap.default_picture));
                        }
                    });
                    return;
                }
                Bitmap bitmap = null;
                try {
                    bitmap = OKHttpUtils.getPicture((String) photosPath[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final Bitmap finalBitmap = bitmap;
                holder.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(finalBitmap ==null)
                            {
                                holder.imageView.setImageDrawable(activity.getDrawable(R.mipmap.default_picture));

                                return;
                            }
                            holder.imageView.setImageBitmap(finalBitmap);
                        }
                    });
            }
        }).start();

    }

    @Override
    public int getItemCount() {
        return commodityList.size();
    }



}
