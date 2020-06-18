package com.example.bysj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihu.matisse.Matisse;

import java.util.List;


public class Shop extends AppCompatActivity {

    RadioButton shopRb,goodsRb;
    Fragment shopGoodsFragment,shopInfoFragment;
    TextView titleTv,exitTv;
    ImageView shop_info_head_iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        init();
    }

    //初始化
    private void init(){
        shopRb = findViewById(R.id.shop_shop_rb);
        goodsRb = findViewById(R.id.shop_goods_rb);
        titleTv = findViewById(R.id.shop_title);
        exitTv = findViewById(R.id.shop_exit);
        shop_info_head_iv = findViewById(R.id.shop_info_head_iv);
        shopGoodsFragment = new ShopGoodsFragment(this);
        shopInfoFragment = new ShopInfoFragment(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.shop_content,shopGoodsFragment).commit();

        initRadio();
        exitInit();
    }

    private void exitInit(){
        exitTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Shop.this, LoginActivity.class);
                startActivity(it);
                SharedPreferences sharedPreferences = Shop.this.getSharedPreferences("info", Context.MODE_PRIVATE);
                sharedPreferences.edit().clear().apply();
                Shop.this.finish();
            }
        });
    }

    //底部radio初始化
    private void initRadio()
    {
        goodsRb.setChecked(true);

        //个人信息部分
        shopRb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    Drawable drawable= getResources().getDrawable(R.mipmap.shop_checked);
                    shopRb.setCompoundDrawablesWithIntrinsicBounds(null,drawable,null,null);

                    getSupportFragmentManager().beginTransaction().hide(shopGoodsFragment);
                    getSupportFragmentManager().beginTransaction().hide(shopInfoFragment);
                    getSupportFragmentManager().beginTransaction().replace(R.id.shop_content,shopInfoFragment).commit();
                    titleTv.setText("我的信息");
//                    getSupportFragmentManager().beginTransaction().show(shopInfoFragment);
                }
                else {
                    Drawable drawable= getResources().getDrawable(R.mipmap.shop_unchecked);
                    shopRb.setCompoundDrawablesWithIntrinsicBounds(null,drawable,null,null);
                }
            }
        });

        //商品信息部分
        goodsRb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    Drawable drawable= getResources().getDrawable(R.mipmap.goods_checked);
                    goodsRb.setCompoundDrawablesWithIntrinsicBounds(null,drawable,null,null);

                    getSupportFragmentManager().beginTransaction().hide(shopGoodsFragment);
                    getSupportFragmentManager().beginTransaction().hide(shopInfoFragment);
                    getSupportFragmentManager().beginTransaction().replace(R.id.shop_content,shopGoodsFragment).commit();

                    titleTv.setText("我的商铺");

//                    getSupportFragmentManager().beginTransaction().show(shopGoodsFragment);
                }
                else {
                    Drawable drawable= getResources().getDrawable(R.mipmap.goods_unchecked);
                    goodsRb.setCompoundDrawablesWithIntrinsicBounds(null,drawable,null,null);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RequestCodeUtils.REQUEST_CODE_ADD_HEAD && resultCode == RESULT_OK){
            shopInfoFragment.onActivityResult(requestCode, resultCode, data);
        }
        if(requestCode==RequestCodeUtils.REQUEST_CODE_FRESH && resultCode ==RESULT_OK){
            ((ShopGoodsFragment)shopGoodsFragment).refreshRecycle();
        }
    }
}
