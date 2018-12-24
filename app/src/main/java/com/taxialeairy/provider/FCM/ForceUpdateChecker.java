package com.taxialeairy.provider.FCM;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.util.Log;


/**
 * Created by Jayakumar on 11/09/17.
 */

public class ForceUpdateChecker {

    public static final String KEY_UPDATE_REQUIRED = "force_update_required_provider";
    public static final String KEY_CURRENT_VERSION = "force_update_current_version_provider";
    public static final String KEY_UPDATE_URL = "force_update_store_url_provider";
    private static final String TAG = ForceUpdateChecker.class.getSimpleName();
    private OnUpdateNeededListener onUpdateNeededListener;
    private Context context;

    public ForceUpdateChecker(@NonNull Context context,
                              OnUpdateNeededListener onUpdateNeededListener) {
        this.context = context;
        this.onUpdateNeededListener = onUpdateNeededListener;
    }

    public static Builder with(@NonNull Context context) {
        return new Builder(context);
    }

    public void check() {
//        final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
//
//        if (remoteConfig.getBoolean(KEY_UPDATE_REQUIRED)) {
//            String currentVersion = remoteConfig.getString(KEY_CURRENT_VERSION);
//            String appVersion = getAppVersion(context);
//            String updateUrl = remoteConfig.getString(KEY_UPDATE_URL);
//
//            if (!TextUtils.equals(currentVersion, appVersion)
//                    && onUpdateNeededListener != null) {
//                onUpdateNeededListener.onUpdateNeeded(updateUrl);
//            }
//        }
    }

    private String getAppVersion(Context context) {
        String result = "";

        try {
            result = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionName;
            result = result.replaceAll("[a-zA-Z]|-", "");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }

        return result;
    }

    public interface OnUpdateNeededListener {
        void onUpdateNeeded(String updateUrl);
    }

    public static class Builder {

        private Context context;
        private OnUpdateNeededListener onUpdateNeededListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder onUpdateNeeded(OnUpdateNeededListener onUpdateNeededListener) {
            this.onUpdateNeededListener = onUpdateNeededListener;
            return this;
        }

        public ForceUpdateChecker build() {
            return new ForceUpdateChecker(context, onUpdateNeededListener);
        }

        public ForceUpdateChecker check() {
            ForceUpdateChecker forceUpdateChecker = build();
            forceUpdateChecker.check();

            return forceUpdateChecker;
        }
    }
}