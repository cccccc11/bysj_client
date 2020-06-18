package com.example.bysj;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.util.List;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder> {

    private List<MyPicture> myPicturesList;
    private Activity activity;
    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView pictureImage;
        Activity activity;
        public ViewHolder (final View view, final Activity activity)
        {
            super(view);
            this.activity = activity;
            pictureImage = view.findViewById(R.id.myPicture);
            pictureImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!PermissionUtils.hasStoragePermissions(activity)){
                        PermissionUtils.verifyStoragePermissions(activity);
                        return;
                    }
                    if(!PermissionUtils.hasCameraPermissions(activity)){
                        PermissionUtils.verifyCameraPermissions(activity);
                        return;
                    }
                    Matisse
                            .from(activity)
                            //选择视频和图片
                            //自定义选择选择的类型
                            .choose(MimeType.of(MimeType.JPEG))
                            //是否只显示选择的类型的缩略图，就不会把所有图片视频都放在一起，而是需要什么展示什么
                            //这两行要连用 是否在选择图片中展示照相 和适配安卓7.0 FileProvider
                            .capture(true)
                            .captureStrategy(new CaptureStrategy(true,"fileprovider"))
                            //有序选择图片 123456...
                            .countable(true)
                            //最大选择数量为9
                            .maxSelectable(9)
                            //选择方向
                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                            //界面中缩略图的质量
                            .thumbnailScale(0.8f)
                            //黑色主题
                            .theme(R.style.Matisse_Dracula)
                            //Glide加载方式
                            .imageEngine(new GlideEngine())
                            .forResult(RequestCodeUtils.REQUEST_CODE_ADD_PICTURE);

                }
            });
        }

    }

    public  PictureAdapter (List <MyPicture> list,Activity activity){
        myPicturesList = list;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_goods_picture,parent,false);
        ViewHolder holder = new ViewHolder(view,activity);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        MyPicture myPicture = myPicturesList.get(position);
        holder.pictureImage.setImageDrawable(myPicture.getDrawable());
    }

    @Override
    public int getItemCount(){
        return myPicturesList.size();
    }
}
