package com.example.bysj;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class MyPicture {

    private Drawable drawable;
    private ImageView imageView;
    private String path;

    public MyPicture(Drawable drawable,ImageView imageView,String path)
    {
        this.drawable = drawable;
        this.imageView = imageView;
        this.path = path;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }
}
