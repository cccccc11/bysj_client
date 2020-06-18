package com.example.bysj.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.bysj.R;
import com.example.bysj.ShopAdd;

import java.util.Date;
import java.util.Calendar;

public class ShopDatePickDialog extends Dialog {

    private Activity activity;
    private DatePicker datePicker;
    private TextView dateTv,commitTv;
    private String dateType;
    private ShopAdd shopAdd;
    public ShopDatePickDialog(@NonNull Context context, Activity activity, TextView dateTv, ShopAdd shopAdd) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.date_pick_dialog);
        this.activity = activity;
        this.dateTv = dateTv;
        this.shopAdd = shopAdd;
        if(dateTv.getId()==R.id.shop_goods_add_date_start){
            dateType = "start";
        }else {
            dateType = "end";
        }
        init();
    }

    private void init(){
        datePicker = findViewById(R.id.date_picker);
        commitTv = findViewById(R.id.date_picker_commit);
//        if(startDate==null&&endDate==null){
//            datePicker.setMinDate(Calendar.getInstance().getTimeInMillis());
//        }else if(startDate==null&&endDate!=null){
//            datePicker.setMaxDate(endDate.getTime());
//        }else if(startDate!=null&&endDate==null){
//            datePicker.setMinDate(startDate.getTime());
//        }else {
//            datePicker.setMinDate(startDate.getTime());
//            datePicker.setMaxDate(endDate.getTime());
//        }
        if(shopAdd.startDate==null&&shopAdd.endDate==null){
            datePicker.setMinDate(Calendar.getInstance().getTimeInMillis());
        }
        if(dateType.equals("start")&&shopAdd.endDate!=null){
            datePicker.setMinDate(Calendar.getInstance().getTimeInMillis());
            datePicker.setMaxDate(shopAdd.endDate.getTime());
        }
        if(dateType.equals("end")&&shopAdd.startDate!=null){
            datePicker.setMinDate(shopAdd.startDate.getTime());
        }
        initCommit();
    }

    private void initCommit(){
        commitTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();
                if(dateType.equals("start")){
                    shopAdd.startDate = new Date(year-1900,month,day);
                }else {
                    shopAdd.endDate = new Date(year-1900,month,day);
                }
                String s = year+"年"+(month+1)+"月"+day+"日";
                dateTv.setText(s);
                ShopDatePickDialog.this.dismiss();
            }
        });
    }


}
