package com.taxialeairy.provider.Retrofit;

import org.json.JSONArray;

/**
 * Created by Esack N on 7/24/2017.
 */

public interface ResponseListener {
    void getJSONArrayResult(String strTag, JSONArray arrayResponse);
}
