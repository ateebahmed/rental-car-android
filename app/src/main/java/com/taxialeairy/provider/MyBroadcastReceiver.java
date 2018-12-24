package com.taxialeairy.provider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.taxialeairy.provider.Activity.MainActivity;

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startActivity(new Intent(context, MainActivity.class));
    }
}
