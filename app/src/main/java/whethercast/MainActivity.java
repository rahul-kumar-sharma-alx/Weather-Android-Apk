package com.example.whethercast;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    private static final String API_KEY = "754fcdfe1a6ac4bff11e41e6ebaf8471";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private ApiInterface apiInterface;
    ImageView imgWeather;
    ConstraintLayout mainLayout;
    LottieAnimationView lottieAnimationView;
    private TextView tvCity, tvDay, tvTemperature, tvWType, tvHumidity, tvWind, tvSunrise, tvSunset, tvSeaLevel, tvCondition, tvCurDate,tvDate,tvMaxTemp,tvMinTemp;
    private SearchView srch;
    private static final long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private static final long FASTEST_INTERVAL = 2000; /* 2 sec */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LottieAnimationView lottieAnimationView = findViewById(R.id.lottieAnimationView);



        initializeViews();
        initRetrofit();
        initLocationClient();
        requestLocationUpdates();
//        createLocationRequest();
//        startLocationUpdates();

        srch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (NetworkUtil.isNetworkAvailable(MainActivity.this)) {
                    String encodedCity = query.trim().replaceAll(" ", "%20");
                    performSearch(encodedCity);
                    return true;
                } else {
                    showError("No internet connection");
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        requestLocationUpdates();
    }

    private void initializeViews() {
        tvCity = findViewById(R.id.tvCity);
        tvDay = findViewById(R.id.tvDay);
        tvTemperature = findViewById(R.id.tvTemparature);
        tvWType = findViewById(R.id.tvWType);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvWind = findViewById(R.id.tvWindSpeed);
        tvSunrise = findViewById(R.id.tvSunrise);
        tvSunset = findViewById(R.id.tvSunset);
        tvSeaLevel = findViewById(R.id.tvSeaLevel);
        tvCondition = findViewById(R.id.tvCondition);
        srch = findViewById(R.id.srch);
        tvCurDate = findViewById(R.id.tvCurDate);
        tvDate = findViewById(R.id.tvDate);
        tvMinTemp = findViewById(R.id.tvMinTemp);
        tvMaxTemp = findViewById(R.id.tvMaxTemp);
        lottieAnimationView=findViewById(R.id.lottieAnimationView);
        mainLayout=findViewById(R.id.mainLayout);
    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiInterface = retrofit.create(ApiInterface.class);
    }

    private void initLocationClient() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
                    .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                Location lastKnownLocation = task.getResult();
                                double latitude = lastKnownLocation.getLatitude();
                                double longitude = lastKnownLocation.getLongitude();
                                callApiWithLocation(latitude, longitude);
                            } else {
                                showError("Failed to get location");
                            }
                        }
                    });
        } else {
            // Request the permission.
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

//    private void startLocationUpdates() {
//        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
//    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    callApiWithLocation(latitude, longitude);
                }
            }
        }
    };

    private void callApiWithLocation(double latitude, double longitude) {
        if (NetworkUtil.isNetworkAvailable(this)) {
            Call<WeatherApp> call = apiInterface.getWeatherDataByCoordinates(latitude, longitude, API_KEY);
            call.enqueue(new Callback<WeatherApp>() {
                @Override
                public void onResponse(@NonNull Call<WeatherApp> call, @NonNull Response<WeatherApp> response) {
                    if (response.isSuccessful()) {
                        WeatherApp weatherData = response.body();
                        updateUI(weatherData);
                    } else {
                        handleApiError(response);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<WeatherApp> call, @NonNull Throwable t) {
                    showError("Failed to fetch weather data");
                }
            });
        } else {
            showError("No internet connection");
        }
    }

    private void performSearch(String city) {
        Call<WeatherApp> call = apiInterface.getWeatherData(city, API_KEY);
        call.enqueue(new Callback<WeatherApp>() {
            @Override
            public void onResponse(@NonNull Call<WeatherApp> call, @NonNull Response<WeatherApp> response) {
                if (response.isSuccessful()) {
                    WeatherApp weatherData = response.body();
                    updateUI(weatherData);
                } else {
                    handleApiError(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherApp> call, @NonNull Throwable t) {
                showError("Failed to fetch weather data" + t.getMessage());
            }
        });
    }

    private void updateUI(WeatherApp weatherData) {
        if (weatherData != null) {
            //tvCity.setText("City: " + weatherData.getName());
            setCityName(weatherData.getName());

            if (weatherData.getMain() != null) {
//                double temperature = weatherData.getMain().getTemp();
//                double temperatureCelsius = temperature - 273.15;
//                DecimalFormat df = new DecimalFormat("#.##");
//                String temperatureCString = df.format(temperatureCelsius);
//                tvTemperature.setText(temperatureCString + "°C");
                setTemperature(weatherData.getMain().getTemp());
                setHumidity(weatherData.getMain().getHumidity());
                setSeaLevel(weatherData.getMain().getSea_level());
                setDay(weatherData.getDt());
                setCurDate(weatherData.getDt());
                setWindSpeed(weatherData.getWind().getSpeed());
                setCondition(weatherData.getWeather().get(0).getDescription());
                setSunRise(weatherData.getSys().getSunrise());
                setSunSet(weatherData.getSys().getSunset());
                setMaxTemp(weatherData.getMain().getTemp_max());
                setMinTemperature(weatherData.getMain().getTemp_min());
                setWeatherType(weatherData.getWeather().get(0).getMain());
            }
        } else {
            showError("Failed to parse weather data");
        }
    }

    private void setWeatherType(String main) {
        new Handler(Looper.getMainLooper()).post(() -> {
            String weatherType = main;
            switch (weatherType) {
                case "Clouds":
                    tvCondition.setText("Cloudy");
                    mainLayout.setBackgroundResource(R.drawable.colud_background);
                    lottieAnimationView.setAnimation(R.raw.cloudy_new);
                    lottieAnimationView.playAnimation();
                    break;
                case "Clear":
                    tvCondition.setText("Sunny");
                    mainLayout.setBackgroundResource(R.drawable.sunny_bg);
                    lottieAnimationView.setAnimation(R.raw.sunny_new);
                    lottieAnimationView.playAnimation();
                    break;
                case "Rain":
                    tvCondition.setText("Rainy");
                    mainLayout.setBackgroundResource(R.drawable.rain_background);
                    lottieAnimationView.setAnimation(R.raw.rain_new);
                    lottieAnimationView.playAnimation();
                    break;
                case "Thunderstorm":
                    tvCondition.setText("Thunderstorm");
                    mainLayout.setBackgroundResource(R.drawable.white_cloud);
                    lottieAnimationView.setAnimation(R.raw.thunder_new);
                    lottieAnimationView.playAnimation();
                    break;
                case "Snow":
                    tvCondition.setText("Snowy");
                    mainLayout.setBackgroundResource(R.drawable.snow_background);
                    lottieAnimationView.setAnimation(R.raw.snow_new);
                    lottieAnimationView.playAnimation();
                    break;
                default:
                    tvCondition.setText("Sunny");
                    lottieAnimationView.playAnimation();
                    break;
            }
        });
    }


    private void setMaxTemp(double tempMax) {
        new Handler(Looper.getMainLooper()).post(() -> {
            double temperatureCelsius = tempMax - 273.15;
            DecimalFormat df = new DecimalFormat("#.##");
            String temperatureCString = df.format(temperatureCelsius);
            tvMaxTemp.setText("Max: "+temperatureCString + "°C");
        });
    }
    private void setMinTemperature(double tempMin) {
        new Handler(Looper.getMainLooper()).post(() -> {
            double temperatureCelsius = tempMin - 273.15;
            DecimalFormat df = new DecimalFormat("#.##");
            String temperatureCString = df.format(temperatureCelsius);
            tvMinTemp.setText("Min: "+temperatureCString + "°C");
        });
    }
    private void setCurDate(int dt) {
        Date date = new Date(dt * 1000L); // Convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = sdf.format(date);

        // Set the formatted date in the TextView
        tvDate.setText(formattedDate);
    }

    private void setDay(int dt) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis((long) dt * 1000L); // Convert seconds to milliseconds

        // Get the day name from Calendar
        String dayName = new SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.getTime());

        // Set the day name in the TextView
        new Handler(Looper.getMainLooper()).post(() -> {
            tvDay.setText(dayName);
        });
    }

    private void setCityName(String name) {
        new Handler(Looper.getMainLooper()).post(() -> {
            tvCity.setText(name);
        });
    }
    private void setTemperature(double temp) {
        new Handler(Looper.getMainLooper()).post(() -> {
            double temperatureCelsius = temp - 273.15;
            DecimalFormat df = new DecimalFormat("#.##");
            String temperatureCString = df.format(temperatureCelsius);
            tvTemperature.setText(temperatureCString + "°C");
        });
    }
    private void setHumidity(double humidity) {
        new Handler(Looper.getMainLooper()).post(() -> {
            tvHumidity.setText(humidity + "%");
        });
    }
    private void setSeaLevel(double seaLevel) {
        new Handler(Looper.getMainLooper()).post(() -> {
            tvSeaLevel.setText(seaLevel + " hPa");
        });
    }
    private void setWindSpeed(double windSpeed) {
        new Handler(Looper.getMainLooper()).post(() -> {
            tvWind.setText(windSpeed + " m/s");
        });
    }
    private void setCondition(String condition) {
        new Handler(Looper.getMainLooper()).post(() -> {
            tvWType.setText(condition);
        });
    }
    private void setSunRise(long sunRise) {
        Date date = new Date(sunRise * 1000L); // Convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String formattedTime = sdf.format(date);

        // Set the formatted time in the TextView
        new Handler(Looper.getMainLooper()).post(() -> {
            tvSunrise.setText(formattedTime);
        });
    }
    private void setSunSet(long sunSet) {
        Date date = new Date(sunSet * 1000L); // Convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String formattedTime = sdf.format(date);

        // Set the formatted time in the TextView
        new Handler(Looper.getMainLooper()).post(() -> {
            tvSunset.setText(formattedTime);
        });
    }
    private void showError(String message) {
        new Handler(Looper.getMainLooper()).post(() -> {
            Log.e("Error", message);
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        });
    }

    private void handleApiError(final Response<WeatherApp> response) {
        runOnUiThread(() -> {
            int responseCode = response.code();
            String errorMessage = response.message();
            showError("API Error: " + responseCode + " - " + errorMessage);
            if (response.errorBody() != null) {
                try {
                    String errorJson = response.errorBody().string();
                    Log.e("API Error inside if", "Response Code: " + responseCode + " - " + errorMessage + " - " + errorJson);
                } catch (IOException e) {
                    e.printStackTrace();
                    showError("API Error: " + responseCode + " - " + errorMessage);
                }
            } else {
                showError("API Error: " + responseCode + " - " + errorMessage);
            }
        });
    }
//    private void createLocationRequest() {
//        LocationRequest locationRequest = new LocationRequest();
//        locationRequest.setInterval(UPDATE_INTERVAL);
//        locationRequest.setFastestInterval(FASTEST_INTERVAL);
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//    }

}
