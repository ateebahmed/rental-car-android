package com.taxialeairy.provider.Retrofit;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiInterface2 {

    @FormUrlEncoded
    @POST("api/provider/profile/location")
    Call<String> updatelocation(@Header("X-Requested-With") String xmlRequest, @Header("Authorization") String strToken,
                                @Field("latitude") String latitude, @Field("longitude") String longitude);
}
