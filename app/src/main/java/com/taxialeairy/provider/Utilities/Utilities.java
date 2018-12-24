package com.taxialeairy.provider.Utilities;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.taxialeairy.provider.Activity.MainActivity;
import com.taxialeairy.provider.BuildConfig;
import com.taxialeairy.provider.R;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Freeware Sys on 4/8/2017.
 */

public class Utilities {

    boolean showLog = BuildConfig.DEBUG;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;

    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;

        String PASSWORD_PATTERN = "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{8,}";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        boolean status = matcher.matches();

        return status;
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean getBatteryLevel(Context context) {
        int batteryPercentage = 0;
        try {
            IntentFilter ifilter = new IntentFilter(
                    Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);

            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

            float batteryPct = level / (float) scale;

            batteryPercentage = (int) (batteryPct * 100);

            if (batteryPercentage < 0) {
                batteryPercentage = 0;
            }


            String strStatus = "";
            switch (status) {
                case BatteryManager.BATTERY_STATUS_UNKNOWN:
                    strStatus = "Unknown Charged";
                    return sendBatteryStatus(strStatus, batteryPercentage);
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    strStatus = "Charged Plugged";
                    return sendBatteryStatus(strStatus, batteryPercentage);
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    strStatus = "Charged Unplugged";
                    return sendBatteryStatus(strStatus, batteryPercentage);
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    strStatus = "Not Charging";
                    return sendBatteryStatus(strStatus, batteryPercentage);
                case BatteryManager.BATTERY_STATUS_FULL:
                    strStatus = "Charged Completed";
                    return sendBatteryStatus(strStatus, batteryPercentage);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
        return false;
    }

    private static boolean sendBatteryStatus(String batterStatus, int batteryLevel) {

        if ((batterStatus.equalsIgnoreCase("Not Charging") && batteryLevel <= 10) || (batterStatus.equalsIgnoreCase("Unknown Charged") && batteryLevel <= 10)) {
            return true;
        } else {
            return false;
        }
    }

    public static void notify(Context context, Activity activity) {

        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("push", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);


        android.support.v4.app.NotificationCompat.Builder notificationBuilder = new android.support.v4.app.NotificationCompat.Builder(context)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText("" + context.getResources().getString(R.string.battery))
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(pendingIntent);

        notificationBuilder.setSmallIcon(getNotificationIcon(notificationBuilder, context), 1);


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    private static int getNotificationIcon(android.support.v4.app.NotificationCompat.Builder notificationBuilder, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
            return R.drawable.notification_white;
        } else {
            return R.mipmap.ic_launcher;
        }
    }

    public void print(String tag, String message) {
        if (showLog) {
            Log.v(tag, message);
        }
    }
}
