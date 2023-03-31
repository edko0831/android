package com.example.mydacha2.supportclass.weathers;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mydacha2.DataClasses.WeatherDay;
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

public class MyWeather extends ViewModel {
    private static MutableLiveData<List<WeatherDay>> mWeatherDay;
    private static final String user = "admin";
    private static final String pwd = "admin";

    public MyWeather() {
        mWeatherDay = new MutableLiveData<>();
    }

    public LiveData<List<WeatherDay>> getText() {
        return mWeatherDay;
    }

    public static void getClient(String cityId) {

//            OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(300, TimeUnit.SECONDS).addInterceptor(new BasicAuthInterceptor(user, pwd)).build();

        // todo ОПРЕДЕЛЕНИЕ города по GPS
        // http://dataservice.accuweather.com/locations/v1/search.json?q=48.46,35.04&apikey=iG0jvsX5oOAQZi0huyOOUG3mt0VOLZ3j

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://dataservice.accuweather.com/currentconditions/v1/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build();
            WeatherDayAPI weatherDayAPI = retrofit.create(WeatherDayAPI.class);

            weatherDayAPI.weatherDay(cityId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<WeatherDay>>() {

                        @Override
                        public void onNext(@NonNull List<WeatherDay> weatherDayList) {
                            mWeatherDay.setValue(weatherDayList);
                        }

                        @Override
                        public void onSubscribe(@NonNull Disposable d) {}

                        @Override
                        public void onError(Throwable e) {}

                        @Override
                        public void onComplete() {}
                    });
    }
}
