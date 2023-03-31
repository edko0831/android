package com.example.mydacha2.DataClasses;

import com.google.gson.annotations.SerializedName;

public class WeatherDay {

    @SerializedName("LocalObservationDateTime")
    public String dateTime;
    @SerializedName("WeatherText")
    public String weatherText;
    @SerializedName("WeatherIcon")
    public String weatherIcon;
    @SerializedName("PrecipitationType")
    public String precipitationType;
    @SerializedName("Temperature")
    public Temperature temperature;


   public class Temperature{
      @SerializedName("Metric")
      public Metric metric;
      @SerializedName("Imperial")
      public Imperial imperial;

   }

   public class Metric{
       @SerializedName("Value")
       public String Value;
       @SerializedName("Unit")
       public String unit;
       @SerializedName("UnitType")
       public String unitType;
    }

    public class Imperial{
       @SerializedName("Value")
       public String Value;
       @SerializedName("Unit")
       public String unit;
       @SerializedName("UnitType")
       public String unitType;
    }


}
