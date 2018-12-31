package com.taxialeairy.provider.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.taxialeairy.provider.Activity.ActivityPassword;
import com.taxialeairy.provider.Activity.HistoryDetails;
import com.taxialeairy.provider.Activity.WelcomeScreenActivity;
import com.taxialeairy.provider.Helper.ConnectionHelper;
import com.taxialeairy.provider.Helper.CustomDialog;
import com.taxialeairy.provider.Helper.SharedHelper;
import com.taxialeairy.provider.Helper.URLHelper;
import com.taxialeairy.provider.TranxitApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class PastTrips extends Fragment {
    Activity activity;
    Context context;
    Boolean isInternet;
    PostAdapter postAdapter;
    RecyclerView recyclerView;
    RelativeLayout errorLayout;
    ConnectionHelper helper;
    CustomDialog customDialog;
    View rootView;

    ImageView backImg;
    LinearLayout toolbar;


    public PastTrips() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(com.taxialeairy.provider.R.layout.fragment_past_trips, container, false);
        if (activity == null) {
            activity = getActivity();
        }
        if (context == null) {
            context = getContext();
        }
        findViewByIdAndInitialize();
        if (isInternet) {
            getHistoryList();
        }

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        Bundle bundle = getArguments();
        String toolbar = null;
        if (bundle != null)
            toolbar = bundle.getString("toolbar");

        if (toolbar != null && toolbar.length() > 0) {
            this.toolbar.setVisibility(View.VISIBLE);
        }

        return rootView;
    }


    public void getHistoryList() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URLHelper.GET_HISTORY_API, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                if (response != null) {
                    postAdapter = new PostAdapter(response);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(activity) {
                        @Override
                        public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                            return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                        }
                    });
                    if (postAdapter != null && postAdapter.getItemCount() > 0) {
                        errorLayout.setVisibility(View.GONE);
                        recyclerView.setAdapter(postAdapter);
                    } else {
                        errorLayout.setVisibility(View.VISIBLE);
                    }

                } else {
                    errorLayout.setVisibility(View.VISIBLE);
                }

                customDialog.dismiss();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
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
                        getHistoryList();
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

        TranxitApplication.getInstance().addToRequestQueue(jsonArrayRequest);
    }

    public void findViewByIdAndInitialize() {
        recyclerView = (RecyclerView) rootView.findViewById(com.taxialeairy.provider.R.id.recyclerView);
        errorLayout = (RelativeLayout) rootView.findViewById(com.taxialeairy.provider.R.id.errorLayout);
        errorLayout.setVisibility(View.GONE);
        helper = new ConnectionHelper(activity);
        isInternet = helper.isConnectingToInternet();
        toolbar = (LinearLayout) rootView.findViewById(com.taxialeairy.provider.R.id.lnrTitle);
        backImg = (ImageView) rootView.findViewById(com.taxialeairy.provider.R.id.backArrow);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void displayMessage(String toastString) {
        try {
            Snackbar.make(getView(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } catch (Exception e) {
            try {
                Toast.makeText(context, "" + toastString, Toast.LENGTH_SHORT).show();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    public void GoToBeginActivity() {
        SharedHelper.putKey(activity, "loggedIn", getString(com.taxialeairy.provider.R.string.False));
        Intent mainIntent = new Intent(activity, ActivityPassword.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        activity.finish();
    }

    private class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
        JSONArray jsonArray;

        public PostAdapter(JSONArray array) {
            this.jsonArray = array;
        }

        public void append(JSONArray array) {
            try {
                for (int i = 0; i < array.length(); i++) {
                    this.jsonArray.put(array.get(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public PostAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(com.taxialeairy.provider.R.layout.history_list_item, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(PostAdapter.MyViewHolder holder, int position) {
            Glide.with(activity).load(jsonArray.optJSONObject(position).optString("static_map")).placeholder(com.taxialeairy.provider.R.drawable.placeholder).error(com.taxialeairy.provider.R.drawable.placeholder).into(holder.tripImg);
            double displayAmount = 0;
            if (jsonArray.optJSONObject(position).optJSONObject("payment") != null) {
                double basePrice = 0;
                double distancePrice = 0;
                double taxPrice = 0;
                try {
                    basePrice = jsonArray.optJSONObject(position).optJSONObject("payment").getDouble("fixed");
                    distancePrice = jsonArray.optJSONObject(position).optJSONObject("payment").getDouble("distance");
                    taxPrice = jsonArray.optJSONObject(position).optJSONObject("payment").getDouble("tax");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                displayAmount = basePrice + distancePrice + taxPrice;

                holder.tripAmount.setText(SharedHelper.getKey(context, "currency") + "" + displayAmount);
            } else {
                holder.tripAmount.setText(SharedHelper.getKey(context, "currency") + "" + displayAmount);
            }

            holder.tripId.setText(jsonArray.optJSONObject(position).optString("booking_id"));

            try {
                if (!jsonArray.optJSONObject(position).optString("assigned_at", "").isEmpty()) {
                    String form = jsonArray.optJSONObject(position).optString("assigned_at");
                    try {
                        holder.tripDate.setText(getDate(form) + "th " + getMonth(form) + " " + getYear(form) + " at " + getTime(form));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return jsonArray.length();
        }

        private String getMonth(String date) throws ParseException {
            Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            String monthName = new SimpleDateFormat("MMM").format(cal.getTime());
            return monthName;
        }

        private String getDate(String date) throws ParseException {
            Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            String dateName = new SimpleDateFormat("dd").format(cal.getTime());
            return dateName;
        }

        private String getYear(String date) throws ParseException {
            Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            String yearName = new SimpleDateFormat("yyyy").format(cal.getTime());
            return yearName;
        }

        private String getTime(String date) throws ParseException {
            Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            String timeName = new SimpleDateFormat("hh:mm a").format(cal.getTime());
            return timeName;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tripDate, tripTime, tripAmount, tripId;
            ImageView tripImg;

            public MyViewHolder(View itemView) {
                super(itemView);
                tripDate = (TextView) itemView.findViewById(com.taxialeairy.provider.R.id.tripDate);
                tripTime = (TextView) itemView.findViewById(com.taxialeairy.provider.R.id.tripTime);
                tripAmount = (TextView) itemView.findViewById(com.taxialeairy.provider.R.id.tripAmount);
                tripImg = (ImageView) itemView.findViewById(com.taxialeairy.provider.R.id.tripImg);
                tripId = (TextView) itemView.findViewById(com.taxialeairy.provider.R.id.tripid);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(activity, HistoryDetails.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("post_value", jsonArray.optJSONObject(getAdapterPosition()).toString());
                        intent.putExtra("tag", "past_trips");
                        activity.startActivity(intent);
                    }
                });
            }
        }
    }
}
