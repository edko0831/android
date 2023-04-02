package com.example.mydacha2.DataClasses;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface WeatherDayAPI {

    @GET("{cityId}.json?apikey=iG0jvsX5oOAQZi0huyOOUG3mt0VOLZ3j")
    Observable<List<WeatherDay>> weatherDay(@Path("cityId") String cityId);

    @GET("search.json")
    Observable<List<City>> getCity(
            @Query("q") String q,
            @Query("apikey") String apikey);
}
