package com.taxialeairy.provider.FCM;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.taxialeairy.provider.Helper.SharedHelper;
import com.taxialeairy.provider.Utilities.Utilities;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();
    Utilities utils = new Utilities();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        SharedHelper.putKey(getApplicationContext(), "device_token", "" + refreshedToken);
        Log.d(TAG, "onTokenRefresh: ");
        utils.print(TAG, "onTokenRefresh" + refreshedToken);
    }
}
