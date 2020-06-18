package com.example.bysj;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bysj.bean.Commodity;

import java.io.IOException;
import java.util.List;

public class CommodityAdapter extends RecyclerView.Adapter<CommodityAdapter.ViewHolder> {


    private List<MyCommodity> myCommodityList;
    private Activity activity;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView nameTv,priceTv,numTv,numNowTv,stateTv;
        View view;
        Activity activity;
        public Integer commodityId,position;
        private LinearLayout linearLayout;
        public List<MyCommodity> myCommodityList;
        public ViewHolder (View view, final Activity activity)
        {
            super(view);
            this.view = view;
            this.activity = activity;
            linearLayout = view.findViewById(R.id.shop_commodity_ll);
            imageView = view.findViewById(R.id.shop_commodity_picture_iv);
            nameTv = view.findViewById(R.id.shop_commodity_name_tv);
            priceTv = view.findViewById(R.id.shop_commodity_price_tv);
//            numNowTv = view.findViewById(R.id.shop_commodity_numNow_tv);
            numTv = view.findViewById(R.id.shop_commodity_num_tv);
            stateTv = view.findViewById(R.id.shop_commodity_state_tv);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyCommodity commodity = myCommodityList.get(position);
                    if(commodity.getState().equals("2")){
                        Intent it = new Intent(activity,ActivityDistribution.class);
                        it.putExtra("commodityId",commodity.getId());
                        it.putExtra("headURL",commodity.getPicturePath());
                        it.putExtra("num",commodity.getNum());
                        it.putExtra("numNow",commodity.getNumNow());
                        it.putExtra("name",commodity.getName());

                        activity.startActivityForResult(it,RequestCodeUtils.REQUEST_CODE_FRESH);
                    }
                }
            });
        }


    }

    public  CommodityAdapter (List<MyCommodity> list,Activity activity){
        this.activity = activity;
        myCommodityList = list;
    }

    @NonNull
    @Override
    public CommodityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_commodity_info,parent,false);
        return new ViewHolder(view,activity);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommodityAdapter.ViewHolder holder, int position) {
        final MyCommodity myCommodity = myCommodityList.get(position);
        holder.position = position;
        holder.commodityId = myCommodity.getId();
        holder.nameTv.setText(myCommodity.getName());
        holder.priceTv.setText(String.valueOf(myCommodity.getPrice()));
        holder.myCommodityList = myCommodityList;
        //holder.numNowTv.setText(String.valueOf(myCommodity.getNumNow()));
        holder.numTv.setText(myCommodity.getNumNow() +"/"+myCommodity.getNum());

        String pictureURL = myCommodity.getPicturePath();

        if(pictureURL.equals("-1"))
        {
            holder.imageView.setImageDrawable(activity.getDrawable(R.mipmap.default_picture));
        }
        else {
            holder.imageView.setImageDrawable(activity.getDrawable(R.mipmap.default_picture));
            //从url中获取图片
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final Bitmap bitmap = OKHttpUtils.getPicture(myCommodity.getPicturePath());
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(bitmap==null)
                                {
                                    holder.imageView.setImageDrawable(activity.getDrawable(R.mipmap.default_picture));
                                }
                                else {
                                    holder.imageView.setImageBitmap(bitmap);
                                }
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }


        if(myCommodity.getState().equals("0")){
            holder.stateTv.setText("未开始");
            holder.stateTv.setTextColor(Color.RED);
        }
        else if(myCommodity.getState().equals("1")){
            holder.stateTv.setText("团购中");
            holder.stateTv.setTextColor(Color.GREEN);
        }
        else if(myCommodity.getState().equals("2")){
            holder.stateTv.setText("需要配送");
            holder.stateTv.setTextColor(Color.YELLOW);
        }else if(myCommodity.getState().equals("3")){
            holder.stateTv.setText("团购结束");
            holder.stateTv.setTextColor(Color.GRAY);
        }
    }

    @Override
    public int getItemCount() {
        return myCommodityList.size();
    }
}
