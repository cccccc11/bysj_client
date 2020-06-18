package com.example.bysj;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private List<Map<String,Object>> comments;
    private Activity activity;

    public CommentAdapter(){

    }
    public CommentAdapter(Activity activity,List<Map<String,Object>> comments){
        this.activity = activity;
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_adapter,parent,false);
        return new CommentAdapter.ViewHolder(view,activity);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentAdapter.ViewHolder holder, int position) {
        final Map<String,Object> comment = comments.get(position);
        holder.nameTv.setText((String)comment.get("name"));
        holder.contentTv.setText((String)comment.get("content"));

        Date date = (Date) comment.get("date");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        holder.dateTv.setText(calendar.get(Calendar.YEAR)+"."+(calendar.get(Calendar.MONTH)+1)+"."+(calendar.get(Calendar.DATE))+" "+
                                calendar.get(Calendar.HOUR_OF_DAY)+"."+calendar.get(Calendar.MINUTE));

        holder.floorTv.setText("#"+(position+1));

        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                try {
                    bitmap = OKHttpUtils.getHeadPicture((String)comment.get("head"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final Bitmap finalBitmap = bitmap;
                holder.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(finalBitmap ==null){
                            holder.headIv.setImageDrawable(activity.getDrawable(R.mipmap.default_picture));
                        }else
                        {
                            holder.headIv.setImageBitmap(finalBitmap);
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private Activity activity;
        private View view;
        public ImageView headIv;
        public TextView nameTv,contentTv,dateTv,floorTv;

        public ViewHolder (View view, Activity activity){
            super(view);
            this.view = view;
            this.activity = activity;

            init();

        }
        private void init(){
            headIv = view.findViewById(R.id.comment_head_iv);
            nameTv = view.findViewById(R.id.comment_name_tv);
            contentTv = view.findViewById(R.id.comment_content_tv);
            dateTv = view.findViewById(R.id.comment_date_tv);
            floorTv = view.findViewById(R.id.comment_floor_tv);
        }
    }
}
