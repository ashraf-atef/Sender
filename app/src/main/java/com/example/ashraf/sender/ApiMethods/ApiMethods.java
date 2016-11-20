package com.example.ashraf.sender.ApiMethods;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by ashraf on 11/20/2016.
 */

public interface ApiMethods {
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("/maps/api/directions/json")
    Call<ResponseBody> drawpath(
            @Query(value = "origin") String source,
            @Query(value = "destination") String destination,
            @Query(value = "sensor") boolean sencor,
            @Query(value = "mode") String mode,
            @Query(value = "alternatives") boolean alternatives

    );
}
