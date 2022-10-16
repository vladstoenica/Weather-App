package com.stoe.weatherapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherAPI {

    @GET("weather?appid=859d4e02f9b9c3241c9d3009a7b19de3&units=metric")
    Call<OpenWeatherMap> getWeatherWithLocation(@Query("lat")double lat,@Query("lon")double lon);

    @GET("weather?appid=859d4e02f9b9c3241c9d3009a7b19de3&units=metric")
    Call<OpenWeatherMap> getWeatherWithCity(@Query("q")String name);
}
