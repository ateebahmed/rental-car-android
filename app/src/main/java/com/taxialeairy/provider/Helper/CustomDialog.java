package com.taxialeairy.provider.Helper;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

public class CustomDialog extends Dialog {

    public CustomDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(com.taxialeairy.provider.R.layout.custom_dialog);
    }
}
