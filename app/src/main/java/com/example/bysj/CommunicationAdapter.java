package com.example.bysj;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class CommunicationAdapter extends RecyclerView.Adapter<CommunicationAdapter.ViewHolder> {

    private Activity activity;
    private List<Map<String,Object>> communications;


    static class ViewHolder extends RecyclerView.ViewHolder{

        private Activity activity;
        private RelativeLayout relativeLayout;
        private View view;
        public TextView titleTv,contentTv,nameTv,dateTv,numTv;
        public Integer communicationId;
        public String userId;

        public ViewHolder (View view, Activity activity)
        {
            super(view);
            this.view = view;
            this.activity = activity;
            titleTv = view.findViewById(R.id.user_communication_adapter_title_tv);
            contentTv = view.findViewById(R.id.user_communication_adapter_content_tv);
            nameTv = view.findViewById(R.id.user_communication_adapter_userName_tv);
            dateTv = view.findViewById(R.id.user_communication_adapter_date_tv);
            numTv = view.findViewById(R.id.user_communication_adapter_number_tv);

            init();

        }

        private void init(){
            relativeLayout = view.findViewById(R.id.user_commodity_adapter_rl);
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(activity, CommunicationActivity.class);
                    it.putExtra("userId",userId);
                    it.putExtra("communicationId",communicationId);
                    activity.startActivity(it);
                }
            });
        }
    }

    public CommunicationAdapter(){

    }

    public CommunicationAdapter(Activity activity,List<Map<String,Object>> communications){
        this.activity = activity;
        this.communications = communications;
    }

    @NonNull
    @Override
    public CommunicationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.communication_adapter,parent,false);
        return new CommunicationAdapter.ViewHolder(view,activity);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunicationAdapter.ViewHolder holder, int position) {
        Map<String,Object> communication = communications.get(position);
        holder.titleTv.setText((String)communication.get("title"));
        holder.contentTv.setText((String)communication.get("content"));
        holder.nameTv.setText((String)communication.get("userName"));
        holder.numTv.setText((Integer)communication.get("num")+" 回复");

        Date date = (Date) communication.get("date");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        holder.dateTv.setText((calendar.get(Calendar.YEAR))+"."+(calendar.get(Calendar.MONTH)+1)+"."+(calendar.get(Calendar.DATE)));

        holder.communicationId = (int)communication.get("communicationId");
        holder.userId = (String)communication.get("userId");
    }

    @Override
    public int getItemCount() {
        return communications.size();
    }
}
