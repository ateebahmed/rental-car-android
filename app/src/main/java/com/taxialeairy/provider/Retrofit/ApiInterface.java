package com.taxialeairy.provider.Retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by CSS on 8/4/2017.
 */

public interface ApiInterface {

    @FormUrlEncoded
    @POST("api/provider/trip/{id}/calculate")
    Call<ResponseBody> getLiveTracking(@Header("X-Requested-With") String xmlRequest, @Header("Authorization") String strToken,
                                       @Path("id") String id,
                                       @Field("latitude") String latitude, @Field("longitude") String longitude);

    @FormUrlEncoded
    @POST("api/provider/profile/location")
    Call<String> updatelocation(@Header("X-Requested-With") String xmlRequest, @Header("Authorization") String strToken,
                                @Field("latitude") String latitude, @Field("longitude") String longitude);
}
