package com.example.mydacha2.supportclass.weathers;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mydacha2.DataClasses.City;
import com.example.mydacha2.DataClasses.WeatherDayAPI;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyLocations extends ViewModel {
    private static MutableLiveData<List<City>> myCity;

    public MyLocations() {
        myCity = new MutableLiveData<>();
    }

    public LiveData<List<City>> getText() {
        return myCity;
    }

    public static void getLocations(String q) {
        // http://dataservice.accuweather.com/locations/v1/search.json?q=48.46,35.04&apikey=iG0jvsX5oOAQZi0huyOOUG3mt0VOLZ3j

        Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://dataservice.accuweather.com/locations/v1/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build();

        WeatherDayAPI weatherDayAPI = retrofit.create(WeatherDayAPI.class);

        weatherDayAPI.getCity(q, "iG0jvsX5oOAQZi0huyOOUG3mt0VOLZ3j")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<City>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {}

            @Override
            public void onNext(@NonNull List<City> cities) {
                myCity.setValue(cities);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.w("www","onError " + e);}

            @Override
            public void onComplete() {}

        });
    }
}
