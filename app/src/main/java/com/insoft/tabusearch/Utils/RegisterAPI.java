package com.insoft.tabusearch.Utils;

import com.insoft.tabusearch.json.TabusearchRequestJson;
import com.insoft.tabusearch.json.TabusearchResponseJson;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface RegisterAPI {
    @Headers("Content-Type: application/json")

    @POST("getjarakfromuser")
    Call<TabusearchResponseJson> tabulist(@Body TabusearchRequestJson param);

    @GET("daftarwisata")
    Call<TabusearchResponseJson> daftarwisata();

}
