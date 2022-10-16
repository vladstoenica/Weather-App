package com.stoe.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView city, temperature, weatherCondition, humidity, maxTemperature, minTemperature, pressure, wind;
    private ImageView imageView, search;

    LocationManager locationManager;
    LocationListener locationListener;
    double lat, lon;

    ConstraintLayout background;
    FrameLayout frameLayout;

    Calendar cal;
    Boolean isNight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        city = findViewById(R.id.textViewCity);
        temperature = findViewById(R.id.textViewTemp);
        weatherCondition = findViewById(R.id.textViewWeatherCondition);
        humidity = findViewById(R.id.textViewHumidity);
        maxTemperature = findViewById(R.id.textViewMax);
        minTemperature = findViewById(R.id.textViewMin);
        pressure = findViewById(R.id.textViewPressure);
        wind = findViewById(R.id.textViewWind);
        imageView = findViewById(R.id.imageViewWeather);
        search = findViewById(R.id.search);
        background = findViewById(R.id.background);
        frameLayout = findViewById(R.id.frame);

        cal = Calendar.getInstance();

        search.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, WeatherActivity.class));
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();

                Log.e("lat: ", String.valueOf(lat));
                Log.e("lon: ", String.valueOf(lon));
                Log.d("ioi", "a ajuns");

                getWeatherData(lat, lon);
                frameLayout.setOnClickListener(view -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://weather.com/weather/today/l/" + lat + "," + lon));
                    startActivity(browserIntent);
                });
            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 50, locationListener);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && permissions.length > 0 && ContextCompat.checkSelfPermission(this
                , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 50, locationListener);
        }
    }

    public void getWeatherData(double lat, double lon) {
        WeatherAPI weatherAPI = RetrofitWeather.getClient().create(WeatherAPI.class);
        Call<OpenWeatherMap> call = weatherAPI.getWeatherWithLocation(lat, lon);

        call.enqueue(new Callback<OpenWeatherMap>() {
            @Override
            public void onResponse(Call<OpenWeatherMap> call, Response<OpenWeatherMap> response) {
                double temp, min, max;
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                isNight = hour <= 7 || hour >= 19;
                String condition;

                city.setText(response.body().getName());  // + ", " + response.body().getSys().getCountry()

                temp = response.body().getMain().getTemp();
                temperature.setText(String.valueOf((int) temp) + "°");

                weatherCondition.setText(response.body().getWeather().get(0).getDescription());
                humidity.setText(response.body().getMain().getHumidity() + "%");

//                if((response.body().getWeather().get(0).getDescription()).equals("scattered clouds")){
//                    background.setBackgroundResource(R.drawable.scattered_day);
//                }

                max = response.body().getMain().getTempMax();
                maxTemperature.setText(String.valueOf((int) max) + "°");

                min = response.body().getMain().getTempMin();
                minTemperature.setText(String.valueOf((int) min) + "°");

                pressure.setText("" + response.body().getMain().getPressure());
                wind.setText(response.body().getWind().getSpeed() + "km");

                String iconCode = response.body().getWeather().get(0).getIcon();
                Picasso.get().load("https://openweathermap.org/img/wn/" + iconCode + "@2x.png")
                        .placeholder(null)
                        .into(imageView);

                condition = response.body().getWeather().get(0).getDescription();
                setBackground(condition, isNight);

            }

            @Override
            public void onFailure(Call<OpenWeatherMap> call, Throwable t) {

            }
        });
    }

    public void setBackground(String condition, Boolean isNight) {

        if (!isNight) {  //daca e zi
            if (condition.equals("clear sky")) {
                background.setBackgroundResource(R.drawable.clear_day);
            } else if (condition.equals("scattered clouds")) {
                background.setBackgroundResource(R.drawable.scattered_day);
            } else if (condition.equals("few clouds")) {
                background.setBackgroundResource(R.drawable.few_clouds_day);
            } else if (condition.equals("broken clouds")) {
                background.setBackgroundResource(R.drawable.cloudy_day);
            } else if (condition.equals("shower rain") || condition.equals("rain") || condition.equals("light rain")){
                background.setBackgroundResource(R.drawable.rain_day);
            } else if (condition.equals("thunderstorm")) {
                background.setBackgroundResource(R.drawable.thunderstorm_day);
            } else if (condition.equals("snow")) {
                background.setBackgroundResource(R.drawable.snow_day);
                city.setTextColor(Color.BLACK);
                temperature.setTextColor(Color.BLACK);
                humidity.setTextColor(Color.BLACK);
                maxTemperature.setTextColor(Color.BLACK);
                minTemperature.setTextColor(Color.BLACK);
                pressure.setTextColor(Color.BLACK);
                wind.setTextColor(Color.BLACK);
            }
            else if (condition.equals("mist") || condition.equals("rain")){
                background.setBackgroundResource(R.drawable.rain_day);
            }
        } else {  //daca e noapte
            if (condition.equals("clear sky")) {
                background.setBackgroundResource(R.drawable.clear_night);
            } else if (condition.equals("scattered clouds")) {
                background.setBackgroundResource(R.drawable.clouds_night);
            } else if (condition.equals("few clouds")) {
                background.setBackgroundResource(R.drawable.few_clouds_night);
            } else if (condition.equals("broken clouds")) {
                background.setBackgroundResource(R.drawable.clouds_night);
            } else if (condition.equals("shower rain") || condition.equals("rain")){
                background.setBackgroundResource(R.drawable.night_rain);
            } else if (condition.equals("thunderstorm")) {
                background.setBackgroundResource(R.drawable.thunderstorm_day);
            } else if (condition.equals("snow")) {
                background.setBackgroundResource(R.drawable.snow_day);
                city.setTextColor(Color.BLACK);
                temperature.setTextColor(Color.BLACK);
                humidity.setTextColor(Color.BLACK);
                maxTemperature.setTextColor(Color.BLACK);
                minTemperature.setTextColor(Color.BLACK);
                pressure.setTextColor(Color.BLACK);
                wind.setTextColor(Color.BLACK);
            }
            else if (condition.equals("mist") || condition.equals("rain")){
                background.setBackgroundResource(R.drawable.rain_day);
            }
        }
    }
}