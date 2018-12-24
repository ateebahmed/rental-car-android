package com.taxialeairy.provider.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.taxialeairy.provider.FCM.ForceUpdateChecker;
import com.taxialeairy.provider.Helper.ConnectionHelper;
import com.taxialeairy.provider.Helper.LocaleUtils;
import com.taxialeairy.provider.Helper.SharedHelper;
import com.taxialeairy.provider.Helper.URLHelper;
import com.taxialeairy.provider.R;
import com.taxialeairy.provider.TranxitApplication;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashScreen extends AppCompatActivity implements ForceUpdateChecker.OnUpdateNeededListener {

    public Activity activity = SplashScreen.this;
    public Context context = SplashScreen.this;
    String TAG = "SplashActivity";
    ConnectionHelper helper;
    Boolean isInternet;
    Handler handleCheckStatus;
    int retryCount = 0;
    AlertDialog alert;
    String device_token, device_UDID;
    TextView lblVersion;
    LinearLayout btnArabic, btnEnglish;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.onAttach(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.taxialeairy.provider.R.layout.activity_splash);
        ForceUpdateChecker.with(this).onUpdateNeeded(this).check();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        helper = new ConnectionHelper(context);
        lblVersion = (TextView) findViewById(com.taxialeairy.provider.R.id.lblVersion);

        btnArabic = (LinearLayout) findViewById(R.id.btnArabic);
        btnEnglish = (LinearLayout) findViewById(R.id.btnEnglish);
        //lblVersion.setText(getResources().getString(R.string.version) +" "+ BuildConfig.VERSION_NAME+" ("+BuildConfig.VERSION_CODE+")");
        isInternet = helper.isConnectingToInternet();
        handleCheckStatus = new Handler();

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(SplashScreen.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Android M Permission check
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                SharedHelper.putKey(SplashScreen.this, "first_lat", String.valueOf(location.getLatitude()));
                                SharedHelper.putKey(SplashScreen.this, "first_lang", String.valueOf(location.getLongitude()));
                            }
                        }
                    });
        }

        btnArabic.setVisibility(View.GONE);
        btnEnglish.setVisibility(View.GONE);
        handleCheckStatus.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (helper.isConnectingToInternet()) {

                    if (SharedHelper.getKey(context, "loggedIn").equalsIgnoreCase(getString(com.taxialeairy.provider.R.string.True))) {

                        GetToken();
                        getProfile();
                    } else {
                        btnArabic.setVisibility(View.VISIBLE);
                        btnEnglish.setVisibility(View.VISIBLE);
                        //GoToBeginActivity();
                    }
                    if (alert != null && alert.isShowing()) {
                        alert.dismiss();
                    }
                } else {
                    showDialog();
                    handleCheckStatus.postDelayed(this, 3000);
                }
            }
        }, 5000);

        btnEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (latlng()) {
                    SharedHelper.putKey(SplashScreen.this, "language", "en");
                    LocaleUtils.setLocale(SplashScreen.this, "en");
                    if (helper.isConnectingToInternet()) {
                        if (SharedHelper.getKey(context, "loggedIn").equalsIgnoreCase(context.getResources().getString(R.string.True))) {
                            GetToken();
                            getProfile();
                        } else {
                            GoToBeginActivity();
                        }
                        if (alert != null && alert.isShowing()) {
                            alert.dismiss();
                        }
                    } else {
                        showDialog();
                    }
                }
            }
        });
        btnArabic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (latlng()) {
                    SharedHelper.putKey(SplashScreen.this, "language", "ar");
                    LocaleUtils.setLocale(SplashScreen.this, "ar");
                    if (helper.isConnectingToInternet()) {
                        if (SharedHelper.getKey(context, "loggedIn").equalsIgnoreCase(context.getResources().getString(R.string.True))) {
                            GetToken();
                            getProfile();
                        } else {
                            GoToBeginActivity();
                        }
                        if (alert != null && alert.isShowing()) {
                            alert.dismiss();
                        }
                    } else {
                        showDialog();
                    }
                }
            }
        });

    }

    public boolean latlng() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(SplashScreen.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Android M Permission check
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return false;
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                SharedHelper.putKey(SplashScreen.this, "first_lat", String.valueOf(location.getLatitude()));
                                SharedHelper.putKey(SplashScreen.this, "first_lang", String.valueOf(location.getLongitude()));
                            }
                        }
                    });
            return true;
        }
    }

    public void GetToken() {
        try {
            if (!SharedHelper.getKey(context, "device_token").equals("") && SharedHelper.getKey(context, "device_token") != null) {
                device_token = SharedHelper.getKey(context, "device_token");
                Log.i(TAG, "GCM Registration Token: " + device_token);
            } else {
                device_token = "" + FirebaseInstanceId.getInstance().getToken();
                SharedHelper.putKey(context, "device_token", "" + FirebaseInstanceId.getInstance().getToken());
                Log.i(TAG, "Failed to complete token refresh: " + device_token);
            }
        } catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            Log.d(TAG, "Failed to complete token refresh", e);
        }

        try {
            device_UDID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            Log.i(TAG, "Device UDID:" + device_UDID);
        } catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            Log.d(TAG, "Failed to complete device UDID");
        }
    }


    public void getProfile() {
        retryCount++;
        JSONObject object = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.USER_PROFILE_API + "?device_type=android&device_id=" + device_UDID + "&device_token=" + device_token, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                SharedHelper.putKey(context, "id", response.optString("id"));
                SharedHelper.putKey(context, "first_name", response.optString("first_name"));
                SharedHelper.putKey(context, "last_name", response.optString("last_name"));
                SharedHelper.putKey(context, "email", response.optString("email"));
                SharedHelper.putKey(context, "sos", response.optString("sos"));
                if (response.optString("avatar").startsWith("http"))
                    SharedHelper.putKey(context, "picture", response.optString("avatar"));
                else
                    SharedHelper.putKey(context, "picture", URLHelper.base + "storage/" + response.optString("avatar"));
                SharedHelper.putKey(context, "gender", response.optString("gender"));
                SharedHelper.putKey(context, "mobile", response.optString("mobile"));
                SharedHelper.putKey(context, "approval_status", response.optString("status"));
                if (!response.optString("currency").equalsIgnoreCase("") && response.optString("currency") != null)
                    SharedHelper.putKey(context, "currency", response.optString("currency"));
                else
                    SharedHelper.putKey(context, "currency", "$");
                SharedHelper.putKey(context, "loggedIn", getString(com.taxialeairy.provider.R.string.True));

                if (response.optJSONObject("service") != null) {
                    try {
                        JSONObject service = response.optJSONObject("service");
                        if (service.optJSONObject("service_type") != null) {
                            JSONObject serviceType = service.optJSONObject("service_type");
                            SharedHelper.putKey(context, "service", serviceType.optString("name"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (response.optString("status").equalsIgnoreCase("new")) {
                    Intent intent = new Intent(activity, WaitingForApproval.class);
                    activity.startActivity(intent);
                } else {
                    GoToMainActivity();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (retryCount < 5) {
                    getProfile();
                } else {
                    GoToBeginActivity();
                }
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(com.taxialeairy.provider.R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            SharedHelper.putKey(context, "loggedIn", getString(com.taxialeairy.provider.R.string.False));
                            if (retryCount > 5)
                                GoToBeginActivity();
                        } else if (response.statusCode == 422) {

                            json = TranxitApplication.trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(getString(com.taxialeairy.provider.R.string.please_try_again));
                            }

                        } else if (response.statusCode == 503) {
                            displayMessage(getString(com.taxialeairy.provider.R.string.server_down));
                        }
                    } catch (Exception e) {
                        displayMessage(getString(com.taxialeairy.provider.R.string.something_went_wrong));
                    }
                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(com.taxialeairy.provider.R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(com.taxialeairy.provider.R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        getProfile();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        TranxitApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    public void GoToMainActivity() {
        Intent mainIntent = new Intent(activity, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    public void GoToBeginActivity() {
        SharedHelper.putKey(activity, "loggedIn", getString(com.taxialeairy.provider.R.string.False));
        Intent mainIntent = new Intent(activity, WelcomeScreenActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        activity.finish();
    }

    public void displayMessage(String toastString) {
        Toast.makeText(activity, toastString, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(com.taxialeairy.provider.R.string.connect_to_network))
                .setCancelable(false)
                .setPositiveButton(getString(com.taxialeairy.provider.R.string.connect_to_wifi), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alert.dismiss();
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton(getString(com.taxialeairy.provider.R.string.quit), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alert.dismiss();
                        finish();
                    }
                });
        if (alert == null) {
            alert = builder.create();
            alert.show();
        }
    }


    @Override
    public void onUpdateNeeded(final String updateUrl) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(com.taxialeairy.provider.R.string.new_version_available))
                .setMessage(getResources().getString(com.taxialeairy.provider.R.string.update_to_continue))
                .setPositiveButton(getResources().getString(com.taxialeairy.provider.R.string.update),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                redirectStore(updateUrl);
                            }
                        }).setNegativeButton(getResources().getString(com.taxialeairy.provider.R.string.no_thanks),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).create();
        dialog.show();
    }

    private void redirectStore(String updateUrl) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
