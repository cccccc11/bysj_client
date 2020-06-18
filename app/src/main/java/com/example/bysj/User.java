package com.example.bysj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bysj.fragment.userFragment.UserCommunicationFragment;
import com.example.bysj.fragment.userFragment.UserInfoFragment;
import com.example.bysj.fragment.userFragment.UserMainFragment;
import com.example.bysj.fragment.userFragment.UserOrderFragment;
import com.example.bysj.fragment.userFragment.UserShoppingCartFragment;

public class User extends AppCompatActivity {

    public static Fragment mainFragment,shoppingCartFragment,userInfoFragment,communicationFragment,orderFragment;
    private RadioButton mainRb,shoppingCartRb,userInfoRb,communicationRb,orderRb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        init();

    }

    private void init(){

        mainFragment = new UserMainFragment(this);
        shoppingCartFragment = new UserShoppingCartFragment(this);
        userInfoFragment = new UserInfoFragment(this);
        communicationFragment = new UserCommunicationFragment(this);
        orderFragment = new UserOrderFragment(this);
        mainRb = findViewById(R.id.user_commodity_rb);
        shoppingCartRb = findViewById(R.id.user_shopping_cart_rb);
        userInfoRb = findViewById(R.id.user_user_info_rb);
        communicationRb = findViewById(R.id.user_communication_rb);
        orderRb = findViewById(R.id.user_order_rb);
        getSupportFragmentManager().beginTransaction().replace(R.id.user_content,mainFragment).commit();
        initRb();



    }


    //初始化radiobutton按钮
    private void initRb(){
        mainRb.setChecked(true);

        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (buttonView.getId()){
                    case R.id.user_commodity_rb:{
                        if(isChecked){
                            Drawable drawable = getDrawable(R.mipmap.commodity_checked);
                            mainRb.setCompoundDrawablesWithIntrinsicBounds(null,drawable,null,null);
                            getSupportFragmentManager().beginTransaction().replace(R.id.user_content,mainFragment).commit();
                        }
                        else {
                            Drawable drawable = getDrawable(R.mipmap.commodity_unchecked);
                            mainRb.setCompoundDrawablesWithIntrinsicBounds(null,drawable,null,null);
                        }
                        break;
                    }
                    case R.id.user_shopping_cart_rb:{
                        if(isChecked){
                            Drawable drawable = getDrawable(R.mipmap.shopping_cart_checked);
                            shoppingCartRb.setCompoundDrawablesWithIntrinsicBounds(null,drawable,null,null);
                            getSupportFragmentManager().beginTransaction().replace(R.id.user_content,shoppingCartFragment).commit();
                        }
                        else {
                            Drawable drawable = getDrawable(R.mipmap.shopping_cart_unchecked);
                            shoppingCartRb.setCompoundDrawablesWithIntrinsicBounds(null,drawable,null,null);
                        }
                        break;
                    }
                    case R.id.user_user_info_rb:{
                        if(isChecked){
                            Drawable drawable = getDrawable(R.mipmap.shop_checked);
                            userInfoRb.setCompoundDrawablesWithIntrinsicBounds(null,drawable,null,null);
                            getSupportFragmentManager().beginTransaction().replace(R.id.user_content,userInfoFragment).commit();
                        }
                        else {
                            Drawable drawable = getDrawable(R.mipmap.shop_unchecked);
                            userInfoRb.setCompoundDrawablesWithIntrinsicBounds(null,drawable,null,null);
                        }
                        break;
                    }
                    case R.id.user_communication_rb:{
                        if(isChecked){
                            Drawable drawable = getDrawable(R.mipmap.communication_checked);
                            communicationRb.setCompoundDrawablesWithIntrinsicBounds(null,drawable,null,null);
                            getSupportFragmentManager().beginTransaction().replace(R.id.user_content,communicationFragment).commit();
                        }
                        else {
                            Drawable drawable = getDrawable(R.mipmap.communication_unchecked);
                            communicationRb.setCompoundDrawablesWithIntrinsicBounds(null,drawable,null,null);
                        }
                        break;
                    }
                    case R.id.user_order_rb:{
                        if(isChecked){
                            Drawable drawable = getDrawable(R.mipmap.order_checked);
                            orderRb.setCompoundDrawablesWithIntrinsicBounds(null,drawable,null,null);
                            getSupportFragmentManager().beginTransaction().replace(R.id.user_content,orderFragment).commit();
                        }
                        else {
                            Drawable drawable = getDrawable(R.mipmap.order_unchecked);
                            orderRb.setCompoundDrawablesWithIntrinsicBounds(null,drawable,null,null);
                        }
                        break;
                    }
                }
            }
        };

        mainRb.setOnCheckedChangeListener(onCheckedChangeListener);
        shoppingCartRb.setOnCheckedChangeListener(onCheckedChangeListener);
        userInfoRb.setOnCheckedChangeListener(onCheckedChangeListener);
        communicationRb.setOnCheckedChangeListener(onCheckedChangeListener);
        orderRb.setOnCheckedChangeListener(onCheckedChangeListener);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            {
                if (requestCode==RequestCodeUtils.REQUEST_CODE_ADD_HEAD ){
                    userInfoFragment.onActivityResult(requestCode, resultCode, data);
                }
            }


        }

    }
}
