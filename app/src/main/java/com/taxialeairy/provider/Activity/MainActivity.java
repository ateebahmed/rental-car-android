package com.taxialeairy.provider.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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
import com.facebook.FacebookSdk;
import com.facebook.accountkit.AccountKit;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.taxialeairy.provider.Bean.Connect;
import com.taxialeairy.provider.Fragment.EarningsFragment;
import com.taxialeairy.provider.Fragment.Help;
import com.taxialeairy.provider.Fragment.Map;
import com.taxialeairy.provider.Fragment.SummaryFragment;
import com.taxialeairy.provider.Helper.LocaleUtils;
import com.taxialeairy.provider.Helper.SharedHelper;
import com.taxialeairy.provider.Helper.URLHelper;
import com.taxialeairy.provider.Listeners.ConnectionBooleanChangedListener;
import com.taxialeairy.provider.LocationMonitoringService;
import com.taxialeairy.provider.TranxitApplication;
import com.taxialeairy.provider.Utilities.LocationTracking;
import com.taxialeairy.provider.Utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_YOURTRIPS = "yourtrips";
    private static final String TAG_SUMMARY = "summary";
    private static final String TAG_HELP = "help";
    private static final String TAG_EARNINGS = "earnings";
    private static final String TAG_SHARE = "share";
    private static final String TAG_LOGOUT = "logout";
    private static final int REQUEST_LOCATION = 1450;
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    public static FragmentManager fragmentManager;
    // index to identify current nav menu item
    public int navItemIndex = 0;
    public String CURRENT_TAG = TAG_HOME;
    Fragment fragment;
    Activity activity;
    Context context;
    Toolbar toolbar;
    Utilities utils = new Utilities();
    boolean push = false;
    Button btnFusedLocation;
    TextView tvLocation;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Map lFrag;
    boolean doubleBackToExitPressedOnce = false;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgProfile;
    private TextView txtName, approvaltxt;
    private ImageView status;
    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private NotificationManager notificationManager;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.onAttach(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        context = getApplicationContext();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (SharedHelper.getKey(context, "login_by").equals("facebook"))
            FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(com.taxialeairy.provider.R.layout.activity_main);
        findViewById();
        Bundle extras = getIntent().getExtras();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

//        Intent intent = new Intent(getApplicationContext(), LocationTracking.class);
//        startService(intent);

        if (extras != null) {
            push = extras.getBoolean("push");
        }

        map();
        Connect.addMyBooleanListener(new ConnectionBooleanChangedListener() {
            @Override
            public void OnMyBooleanChanged() {
                Toast.makeText(getApplication(), "Changed", Toast.LENGTH_SHORT).show();
            }
        });
        loadNavHeader();
        setUpNavigationView();

        navHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                startActivity(new Intent(activity, EditProfile.class));
            }
        });


//        Intent myIntent = new Intent(MainActivity.this, LocationMonitoringService.class);
//        PendingIntent pendingIntent = PendingIntent.getService(MainActivity.this, 0, myIntent, 0);
//
//        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
//
//        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.SECOND,5);
//
//        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);


        Intent myIntent = new Intent(MainActivity.this, LocationMonitoringService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(myIntent);
        } else {
            startService(myIntent);
        }
//        startService(myIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           // stopForeground(true);
        }
    }

    private void findViewById() {
        drawer = (DrawerLayout) findViewById(com.taxialeairy.provider.R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(com.taxialeairy.provider.R.id.nav_view);
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(com.taxialeairy.provider.R.id.usernameTxt);
        approvaltxt = (TextView) navHeader.findViewById(com.taxialeairy.provider.R.id.status_txt);
        imgProfile = (ImageView) navHeader.findViewById(com.taxialeairy.provider.R.id.img_profile);
        status = (ImageView) navHeader.findViewById(com.taxialeairy.provider.R.id.status);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case com.taxialeairy.provider.R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        fragment = new Map();
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("push", push);
                        fragment.setArguments(bundle);
                        FragmentManager manager = getSupportFragmentManager();
                        @SuppressLint("CommitTransaction")
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(com.taxialeairy.provider.R.id.content, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                    case com.taxialeairy.provider.R.id.nav_yourtrips:
                       /* navItemIndex = 1;
                        CURRENT_TAG = TAG_YOURTRIPS;
                        fragment = new YourTrips();
                        GoToFragment();*/
                        drawer.closeDrawers();
                        startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                        break;
                    case com.taxialeairy.provider.R.id.nav_wallet:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_SUMMARY;
                        fragment = new SummaryFragment();
                        drawer.closeDrawers();
                        FragmentManager manager2 = getSupportFragmentManager();
                        @SuppressLint("CommitTransaction")
                        FragmentTransaction transaction1 = manager2.beginTransaction();
                        transaction1.replace(com.taxialeairy.provider.R.id.content, fragment);
                        transaction1.addToBackStack(null);
                        transaction1.commit();
                        //GoToFragment();
                        break;
                    case com.taxialeairy.provider.R.id.nav_help:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_HELP;
                        fragment = new Help();
                        drawer.closeDrawers();
                        FragmentManager manager4 = getSupportFragmentManager();
                        @SuppressLint("CommitTransaction")
                        FragmentTransaction transaction2 = manager4.beginTransaction();
                        transaction2.replace(com.taxialeairy.provider.R.id.content, fragment);
                        transaction2.addToBackStack(null);
                        transaction2.commit();
                        //GoToFragment();
                        break;
                    case com.taxialeairy.provider.R.id.nav_earnings:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_EARNINGS;
                        fragment = new EarningsFragment();
                        drawer.closeDrawers();
                        FragmentManager manager1 = getSupportFragmentManager();
                        @SuppressLint("CommitTransaction")
                        FragmentTransaction transaction3 = manager1.beginTransaction();
                        transaction3.replace(com.taxialeairy.provider.R.id.content, fragment);
                        transaction3.addToBackStack(null);
                        transaction3.commit();
//                        GoToFragment();
                        break;
                    /*case R.id.nav_share:
                        drawer.closeDrawers();
                        navigateToShareScreen(URLHelper.APP_URL);
                        return true;*/
                    case com.taxialeairy.provider.R.id.nav_logout:
                        showLogoutDialog();
                        return true;
                    case com.taxialeairy.provider.R.id.nav_setting:
                        startActivity(new Intent(MainActivity.this, ActivitySetting.class));
                        finish();
                        return true;
                    default:
                        navItemIndex = 0;
                }


                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, com.taxialeairy.provider.R.string.navigation_drawer_open, com.taxialeairy.provider.R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    loadNavHeader();
                }
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    private void loadNavHeader() {
        // name, website
        txtName.setText(SharedHelper.getKey(context, "first_name") + " " + SharedHelper.getKey(context, "last_name"));
        if (SharedHelper.getKey(context, "approval_status").equals("new") || SharedHelper.getKey(context, "approval_status").equals("onboarding")) {
            approvaltxt.setTextColor(Color.YELLOW);
            approvaltxt.setText(getText(com.taxialeairy.provider.R.string.waiting_for_approval));
            status.setImageResource(com.taxialeairy.provider.R.drawable.newuser);
        } else if (SharedHelper.getKey(context, "approval_status").equals("banned")) {
            approvaltxt.setTextColor(Color.RED);
            approvaltxt.setText(getText(com.taxialeairy.provider.R.string.banned));
            status.setImageResource(com.taxialeairy.provider.R.drawable.banned);
        } else {
            approvaltxt.setTextColor(Color.GREEN);
            approvaltxt.setText(getText(com.taxialeairy.provider.R.string.approved));
            status.setImageResource(com.taxialeairy.provider.R.drawable.approved);
        }


        utils.print("Profile_PIC", "" + SharedHelper.getKey(context, "picture"));

        // Loading profile image
//        Glide.with(this).load(SharedHelper.getKey(context,"picture"))
//                .placeholder(R.drawable.ic_dummy_user)
//                .error(R.drawable.ic_dummy_user)
//                .crossFade()
//                .thumbnail(0.5f)
//                .bitmapTransform(new CircleTransform(this))
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(imgProfile);
        //Assign current profile values to the edittext
        //Glide.with(activity).load(SharedHelper.getKey(context,"picture")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProfile);
        Picasso.with(context).load(SharedHelper.getKey(context, "picture")).placeholder(com.taxialeairy.provider.R.drawable.ic_dummy_user).error(com.taxialeairy.provider.R.drawable.ic_dummy_user).into(imgProfile);

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
//                if (CURRENT_TAG.equalsIgnoreCase(TAG_SUMMARY) || CURRENT_TAG.equalsIgnoreCase(TAG_EARNINGS)
//                        || CURRENT_TAG.equalsIgnoreCase(TAG_YOURTRIPS)){
//
//                }else{
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                fragment = new Map();
                GoToFragment();
                return;
//                }
            } else {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                } else {

                    doubleBackToExitPressedOnce = true;
                    Toast.makeText(MainActivity.this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 5000);
                    return;
                }
            }
        }

    }

    private void map() {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragment = new Map();
                FragmentManager manager = getSupportFragmentManager();
                @SuppressLint("CommitTransaction")
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(com.taxialeairy.provider.R.id.content, fragment);
                transaction.commit();
                fragmentManager = getSupportFragmentManager();
            }
        });
    }

    public void GoToFragment() {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                drawer.closeDrawers();
                FragmentManager manager = getSupportFragmentManager();
                @SuppressLint("CommitTransaction")
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(com.taxialeairy.provider.R.id.content, fragment);
                transaction.commit();
            }
        });
    }

    public void navigateToShareScreen(String shareUrl) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareUrl + " -via " + getString(com.taxialeairy.provider.R.string.app_name));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }


    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            enableLoc();
        }
    }

    private void enableLoc() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        mGoogleApiClient.connect();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                        utils.print("Location error", "Location error " + connectionResult.getErrorCode());
                    }
                }).build();
        mGoogleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                }
            }
        });
//	        }

    }


    public void logout() {
        JSONObject object = new JSONObject();
        try {
            object.put("id", SharedHelper.getKey(context, "id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.LOGOUT, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                drawer.closeDrawers();
                if (SharedHelper.getKey(context, "login_by").equals("facebook"))
                    LoginManager.getInstance().logOut();
                if (SharedHelper.getKey(context, "login_by").equals("google"))
                    signOut();
                if (!SharedHelper.getKey(MainActivity.this, "account_kit_token").equalsIgnoreCase("")) {
                    Log.e("MainActivity", "Account kit logout: " + SharedHelper.getKey(MainActivity.this, "account_kit_token"));
                    AccountKit.logOut();
                    SharedHelper.putKey(MainActivity.this, "account_kit_token", "");
                }
                SharedHelper.putKey(context, "current_status", "");
                SharedHelper.putKey(activity, "loggedIn", getString(com.taxialeairy.provider.R.string.False));
                SharedHelper.putKey(context, "email", "");
                Intent goToLogin = new Intent(activity, ActivityPassword.class);
                goToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(goToLogin);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.getString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(com.taxialeairy.provider.R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            /*refreshAccessToken();*/
                        } else if (response.statusCode == 422) {

                            json = TranxitApplication.trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(getString(com.taxialeairy.provider.R.string.please_try_again));
                            }

                        } else if (response.statusCode == 503) {
                            displayMessage(getString(com.taxialeairy.provider.R.string.server_down));
                        } else {
                            displayMessage(getString(com.taxialeairy.provider.R.string.please_try_again));
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
                        logout();
                    }
                }
            }
        }) {
            @Override
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                Log.e("getHeaders: Token", SharedHelper.getKey(context, "access_token") + SharedHelper.getKey(context, "token_type"));
                headers.put("Authorization", "" + "Bearer" + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        TranxitApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void signOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //taken from google api console (Web api client id)
//                .requestIdToken("795253286119-p5b084skjnl7sll3s24ha310iotin5k4.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {

                FirebaseAuth.getInstance().signOut();
                if (mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Log.d("MainAct", "Google User Logged out");
                               /* Intent intent = new Intent(LogoutActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();*/
                            }
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.d("MAin", "Google API Client Connection Suspended");
            }
        });
    }

    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } catch (Exception e) {
            try {
                Toast.makeText(context, "" + toastString, Toast.LENGTH_SHORT).show();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    private void showLogoutDialog() {
        if (!isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(com.taxialeairy.provider.R.string.logout));
            builder.setMessage(getString(com.taxialeairy.provider.R.string.exit_confirm));

            builder.setPositiveButton(com.taxialeairy.provider.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    logout();
                }
            });

            builder.setNegativeButton(com.taxialeairy.provider.R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Reset to previous seletion menu in navigation
                    dialog.dismiss();
                    DrawerLayout drawer = (DrawerLayout) findViewById(com.taxialeairy.provider.R.id.drawer_layout);
                    drawer.openDrawer(GravityCompat.START);
                }
            });
            builder.setCancelable(false);
            final AlertDialog dialog = builder.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, com.taxialeairy.provider.R.color.colorPrimaryDark));
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, com.taxialeairy.provider.R.color.colorPrimaryDark));
                }
            });
            dialog.show();
        }
    }

}
