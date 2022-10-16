package com.stoe.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherActivity extends AppCompatActivity {

    private TextView cityWeather, temperatureWeather, weatherCondition, humidityWeather, maxTemperatureWeather, minTemperatureWeather, pressureWeather, windWeather;
    private ImageView imageViewWeather, searchWeather;
    private EditText editText;

    ConstraintLayout backgroundWeather;

    Calendar cal;
    Boolean isNight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        cityWeather = findViewById(R.id.textViewCityWeather);
        temperatureWeather = findViewById(R.id.textViewTempWeather);
        weatherCondition = findViewById(R.id.textViewWeatherConditionWeather);
        humidityWeather = findViewById(R.id.textViewHumidityWeather);
        maxTemperatureWeather = findViewById(R.id.textViewMaxWeather);
        minTemperatureWeather = findViewById(R.id.textViewMinWeather);
        pressureWeather = findViewById(R.id.textViewPressureWeather);
        windWeather = findViewById(R.id.textViewWindWeather);
        imageViewWeather = findViewById(R.id.imageViewWeatherWeather);
        searchWeather = findViewById(R.id.searchWeather);
        editText = findViewById(R.id.editText);

        backgroundWeather = findViewById(R.id.weatherBackground);
        cal = Calendar.getInstance();

        searchWeather.setOnClickListener(view -> {

            String cityName = editText.getText().toString();
            getWeatherData(cityName);
            editText.setText("");
        });
    }


    public void getWeatherData(String name){
        WeatherAPI weatherAPI = RetrofitWeather.getClient().create(WeatherAPI.class);
        Call<OpenWeatherMap> call = weatherAPI.getWeatherWithCity(name);

        call.enqueue(new Callback<OpenWeatherMap>() {
            @Override
            public void onResponse(Call<OpenWeatherMap> call, Response<OpenWeatherMap> response) {

                if(response.isSuccessful()){

                    double temp, min, max;
                    String condition;
                    int hour = cal.get(Calendar.HOUR_OF_DAY);
                    isNight = hour < 7 || hour > 19;

                    cityWeather.setText(response.body().getName());  // + ", " + response.body().getSys().getCountry()
                    temp = response.body().getMain().getTemp();
                    temperatureWeather.setText(String.valueOf((int) temp) + "°");
                    weatherCondition.setText(response.body().getWeather().get(0).getDescription());
                    humidityWeather.setText(response.body().getMain().getHumidity() + "%");
                    max = response.body().getMain().getTempMax();
                    maxTemperatureWeather.setText(String.valueOf((int) max) + "°");
                    min = response.body().getMain().getTempMin();
                    minTemperatureWeather.setText(String.valueOf((int) min) + "°");
                    pressureWeather.setText("" + response.body().getMain().getPressure());
                    windWeather.setText(response.body().getWind().getSpeed() + "km");

                    String iconCode = response.body().getWeather().get(0).getIcon();
                    Picasso.get().load("https://openweathermap.org/img/wn/" + iconCode + "@2x.png")
                            .placeholder(null)
                            .into(imageViewWeather);

                    condition = response.body().getWeather().get(0).getDescription();
                    setBackground(condition, isNight);
                } else {
                    Toast.makeText(WeatherActivity.this, "City not found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<OpenWeatherMap> call, Throwable t) {

            }

            public void setBackground(String condition, Boolean isNight) {

                if (!isNight) {  //daca e zi
                    if (condition.equals("clear sky")) {
                        backgroundWeather.setBackgroundResource(R.drawable.clear_day);
                    } else if (condition.equals("scattered clouds")) {
                        backgroundWeather.setBackgroundResource(R.drawable.scattered_day);
                    } else if (condition.equals("few clouds")) {
                        backgroundWeather.setBackgroundResource(R.drawable.few_clouds_day);
                    } else if (condition.equals("broken clouds")) {
                        backgroundWeather.setBackgroundResource(R.drawable.cloudy_day);
                    } else if (condition.equals("shower rain") || condition.equals("rain") || condition.equals("light rain")){
                        backgroundWeather.setBackgroundResource(R.drawable.rain_day);
                    } else if (condition.equals("thunderstorm")) {
                        backgroundWeather.setBackgroundResource(R.drawable.thunderstorm_day);
                    } else if (condition.equals("snow")) {
                        backgroundWeather.setBackgroundResource(R.drawable.snow_day);
//                        city.setTextColor(Color.BLACK);
//                        temperature.setTextColor(Color.BLACK);
//                        humidity.setTextColor(Color.BLACK);
//                        maxTemperature.setTextColor(Color.BLACK);
//                        minTemperature.setTextColor(Color.BLACK);
//                        pressure.setTextColor(Color.BLACK);
//                        wind.setTextColor(Color.BLACK);
                    }
                    else if (condition.equals("mist") || condition.equals("rain")){
                        backgroundWeather.setBackgroundResource(R.drawable.rain_day);
                    }
                } else {  //daca e noapte
                    if (condition.equals("clear sky")) {
                        backgroundWeather.setBackgroundResource(R.drawable.clear_night);
                    } else if (condition.equals("scattered clouds")) {
                        backgroundWeather.setBackgroundResource(R.drawable.clouds_night);
                    } else if (condition.equals("few clouds")) {
                        backgroundWeather.setBackgroundResource(R.drawable.few_clouds_night);
                    } else if (condition.equals("broken clouds")) {
                        backgroundWeather.setBackgroundResource(R.drawable.clouds_night);
                    } else if (condition.equals("shower rain") || condition.equals("rain") || condition.equals("light rain")){
                        backgroundWeather.setBackgroundResource(R.drawable.night_rain);
                    } else if (condition.equals("thunderstorm")) {
                        backgroundWeather.setBackgroundResource(R.drawable.thunderstorm_day);
                    } else if (condition.equals("snow")) {
                        backgroundWeather.setBackgroundResource(R.drawable.snow_day);
//                        city.setTextColor(Color.BLACK);
//                        temperature.setTextColor(Color.BLACK);
//                        humidity.setTextColor(Color.BLACK);
//                        maxTemperature.setTextColor(Color.BLACK);
//                        minTemperature.setTextColor(Color.BLACK);
//                        pressure.setTextColor(Color.BLACK);
//                        wind.setTextColor(Color.BLACK);
                    }
                    else if (condition.equals("mist") || condition.equals("rain")){
                        backgroundWeather.setBackgroundResource(R.drawable.rain_day);
                    }
                }
            }
        });
    }
}