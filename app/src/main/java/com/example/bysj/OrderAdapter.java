package com.example.bysj;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bysj.dialog.OrderDialog;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    List<Map<String,Object>> myOrders;
    Activity activity;

    public OrderAdapter(List<Map<String,Object>> myOrders,Activity activity){
        this.myOrders = myOrders;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_adapter,parent,false);
        return new OrderAdapter.ViewHolder(view,activity);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Map<String,Object> order = myOrders.get(position);
        holder.nameTv.setText(order.get("name").toString());
        holder.numTv.setText(order.get("num").toString());
        holder.endDateTv.setText(order.get("endDate").toString());
        holder.commodityId = (int)order.get("commodityId");

        String state = order.get("state").toString();

        switch (state){
            case "0":{
                holder.stateTv.setText("未开始");
                holder.stateTv.setTextColor(Color.RED);
                break;
            }
            case "1":{
                holder.stateTv.setText("正在团购");
                holder.stateTv.setTextColor(Color.parseColor("#ff6600"));
                break;
            }
            case "2":{
                if (order.get("orderState").equals("0")){
                    holder.stateTv.setText("正在配送");
                    holder.stateTv.setTextColor(Color.GREEN);
                    holder.state = "0";
                }else {
                    holder.stateTv.setText("已配送");
                    holder.stateTv.setTextColor(Color.GREEN);
                    holder.state = "1";
                }
                break;
            }
            case "3":{
                holder.stateTv.setText("已结束");
                holder.stateTv.setTextColor(Color.GRAY);
                break;
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                Object[] photosPath = (Object[]) order.get("photosPath");
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
        return myOrders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView nameTv,numTv,endDateTv,stateTv;
        public ImageView imageView;
        private Activity activity;
        private LinearLayout linearLayout;
        public Integer commodityId;
        public String state = null;

        public ViewHolder (View view,Activity activity)
        {
            super(view);
            nameTv = view.findViewById(R.id.user_order_adapter_name);
            numTv = view.findViewById(R.id.user_order_adapter_num);
            endDateTv = view.findViewById(R.id.user_order_adapter_end_date);
            stateTv = view.findViewById(R.id.user_order_adapter_state_tv);
            imageView = view.findViewById(R.id.user_order_adapter_iv);
            linearLayout = view.findViewById(R.id.user_order_adapter_ll);
            this.activity = activity;

            init();

        }

        private void init(){

            linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Map<String,Object> parameter = new HashMap<>();
                    parameter.put("userId",activity.getSharedPreferences("info", Context.MODE_PRIVATE).getString("id","-1"));
                    parameter.put("commodityId",commodityId);
                    parameter.put("state",state);
                    parameter.put("stateTv",stateTv);
                    if(stateTv.getText().toString().equals("正在团购")){
                        parameter.put("canDelete",true);
                    }else {
                        parameter.put("canDelete",false);
                    }
                    new OrderDialog(activity,parameter).show();
                    return true;
                }
            });
        }
    }

}
