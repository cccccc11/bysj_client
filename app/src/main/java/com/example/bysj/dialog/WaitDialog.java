package com.example.bysj.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import androidx.annotation.NonNull;

import com.example.bysj.R;

public class WaitDialog extends Dialog {
    public WaitDialog(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.wait_dialog);
        setCanceledOnTouchOutside(false);
    }
}
