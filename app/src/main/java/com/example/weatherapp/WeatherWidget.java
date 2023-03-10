package com.example.weatherapp;
import static android.content.ContentValues.TAG;

import static java.util.Locale.getDefault;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;
;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
public class WeatherWidget extends AppWidgetProvider {

    private static final String API_KEY = "8384ce731c8c416892e111040231802";


    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Start the location service to get the current location
        Intent intent = new Intent(context, LocationService.class);
        context.startService(intent);

        // Get the current location from the location service    ;|
        Location location = LocationService.getLastKnownLocation(context);

        // If location data is available, update the widget with the location information
        if (location != null) {
            String cityName = getCityName(context, location.getLatitude(), location.getLongitude());

            new GetWeatherInfoTask(context, appWidgetManager, appWidgetIds, cityName).execute();
        }
    }

    private String getCityName(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        if (addresses != null && !addresses.isEmpty()) {
            Address address = addresses.get(0);
            return address.getLocality();
        }
        return "";
    }

    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, String cityName, String temperature, String conditionIconUrl) {
        // Update the widget with the city name and weather information
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
        remoteViews.setTextViewText(R.id.city, cityName);

        remoteViews.setTextViewText(R.id.temp, temperature + "??C");
        Toast.makeText(context,cityName+": "+temperature+"??C",Toast.LENGTH_LONG).show();

        Picasso.get().load("http:".concat(conditionIconUrl)).into(remoteViews, R.id.cond, appWidgetIds);
        // Set up a pending intent to open the MainActivity when the widget is clicked
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);

        // Update all instances of the widget
        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    private class GetWeatherInfoTask extends AsyncTask<Void, Void, weatherRvModel> {
        private Context context;
        private AppWidgetManager appWidgetManager;
        private int[] appWidgetIds;
        private String cityName;

        public GetWeatherInfoTask(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, String cityName) {
            this.context = context;
            this.appWidgetManager = appWidgetManager;
            this.appWidgetIds = appWidgetIds;
            this.cityName = cityName;
        }

        @Override
        protected weatherRvModel doInBackground(Void... voids) {
            String url = "http://api.weatherapi.com/v1/forecast.json?key=8384ce731c8c416892e111040231802&q=\" + cityName + \"&days=1&aqi=yes&alerts=yes";

            if(cityName.equals("Rad??s")){
                url = "http://api.weatherapi.com/v1/forecast.json?key=8384ce731c8c416892e111040231802&q=radis&days=1&aqi=yes&alerts=yes";

            }




            RequestQueue requestQueue = Volley.newRequestQueue(context);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String temperature = response.getJSONObject("current").getString("temp_c");
                        String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                        weatherRvModel weatherInfo = new weatherRvModel( temperature, conditionIcon);
                        onPostExecute(weatherInfo);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSONException: " + e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "Please enter valid city name...", Toast.LENGTH_SHORT).show();
                }
            });

            requestQueue.add(jsonObjectRequest);

            return null;
        }

        @Override
        protected void onPostExecute(weatherRvModel weatherInfo) {
            super.onPostExecute(weatherInfo);
            if (weatherInfo != null) {
                updateWidget(context, appWidgetManager, appWidgetIds, cityName, weatherInfo.getTemperature(), weatherInfo.getIcon());
            }
        }

    }}