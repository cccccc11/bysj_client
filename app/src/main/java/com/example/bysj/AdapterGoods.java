package com.example.bysj;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bysj.dialog.DistributionDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterGoods extends RecyclerView.Adapter<AdapterGoods.ViewHolder> {

    private Activity activity;

    private List<Map<String,Object>> usersInfo;

    static class ViewHolder extends RecyclerView.ViewHolder{

        private Activity activity;
        private View view;

        private TextView nameTv,phoneTv,addressTv,numTv,stateTv;
        private RelativeLayout relativeLayout;

        public Map<String,Object> info;

        public ViewHolder (View view, Activity activity)
        {
            super(view);
            this.activity = activity;
            this.view = view;

            init();
        }

        private void init(){
            nameTv = view.findViewById(R.id.adapter_distribution_name_tv);
            phoneTv = view.findViewById(R.id.adapter_distribution_phone_tv);
            addressTv = view.findViewById(R.id.adapter_distribution_address_tv);
            numTv = view.findViewById(R.id.adapter_distribution_num_tv);
            stateTv = view.findViewById(R.id.adapter_distribution_state_tv);
            relativeLayout = view.findViewById(R.id.adapter_distribution_ll);

//            relativeLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Map<String,Object> parameter = new HashMap<>();
//                    parameter.put("userId",info.get("userId"));
//                    parameter.put("state",info.get("state"));
//                    parameter.put("commodityId",info.get("commodityId"));
//                    parameter.put("stateTv",stateTv);
//                    new DistributionDialog(activity,parameter).show();
//                }
//            });
        }

        public void initInfo(){
            nameTv.setText((String)info.get("name"));
            phoneTv.setText((String)info.get("phone"));
            addressTv.setText((String)info.get("address"));
            numTv.setText(info.get("num")+"");
            String state = (String)info.get("state");
            if(state.equals("0")){
                stateTv.setText("未配送");
                stateTv.setTextColor(Color.RED);
            }else {
                stateTv.setText("已配送");
                stateTv.setTextColor(Color.GREEN);
            }
        }
    }

    public AdapterGoods(){

    }
    public AdapterGoods(Activity activity,List<Map<String,Object>> usersInfo){
        this.activity = activity;
        this.usersInfo = usersInfo;
    }

    @NonNull
    @Override
    public AdapterGoods.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_distribution,parent,false);
        return new AdapterGoods.ViewHolder(view,activity);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterGoods.ViewHolder holder, int position) {
        holder.info = usersInfo.get(position);
        holder.initInfo();
    }

    @Override
    public int getItemCount() {
        return usersInfo.size();
    }
}
