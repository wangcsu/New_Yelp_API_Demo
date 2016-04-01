package com.example.gewang.newyelpapidemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private static final String CONSUMER_KEY = "L8jbFjdSQQwyos1vyLmrlg";
    private static final String CONSUMER_SECRET = "Mo6kThGoagq75PPA8w5KVKtvzRI";
    private static final String TOKEN = "3TuGtUT4j4Ja_W9IzoXvkB68gLAgFZVm";
    private static final String TOKEN_SECRET = "0bxMCU-U0YcK5OwbzX4r3OOpbVM";
    private static final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 11;

    protected static final String TAG = "MainActivity";
    private static final String TERM = "restaurants";
    private Button sb;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mCurrentLocation;
    private String mLatitude;
    private String mLongitude;
    private ArrayList<Restaurant> restaurants = new ArrayList<>();
    ArrayList<Business> businesses;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        }

        sb = (Button) findViewById(R.id.searchBtn);
        sb.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection Failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "Current Location" + mLatitude + ", " + mLongitude);
        YelpAPIFactory apiFactory = new YelpAPIFactory(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
        YelpAPI yelpAPI = apiFactory.createAPI();
        Map<String, String> params = new HashMap<>();
        params.put("term", TERM);
        params.put("limit", "20");
        CoordinateOptions coordinate = CoordinateOptions.builder().latitude(Double.valueOf(mLatitude)).
                longitude(Double.valueOf(mLongitude)).build();
        Callback<SearchResponse> callback = new Callback<SearchResponse>() {
            @Override
            public void onResponse(Response<SearchResponse> response, Retrofit retrofit) {
                SearchResponse searchResponse = response.body();
                // Update UI text with the Business object.
                businesses = searchResponse.businesses();
                for (int i = 0; i < 20; i++) {
                    restaurants.add(new Restaurant(businesses.get(i).name(), businesses.get(i).rating()));
                }
                ListAdapter mAdapter = new MyListAdapter(getApplicationContext(), restaurants);
                ListView mListView = (ListView) findViewById(R.id.res_name);
                mListView.setAdapter(mAdapter);
            }
            @Override
            public void onFailure(Throwable t) {
                // HTTP error happened, do something to handle it.
            }
        };
        Call<SearchResponse> call = yelpAPI.search(coordinate, params);
        call.enqueue(callback);
        //new MyTask().execute();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                }
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mCurrentLocation != null) {
            mLatitude = String.valueOf(mCurrentLocation.getLatitude());
            mLongitude = String.valueOf(mCurrentLocation.getLongitude());
        }
    }

    /*private class MyTask extends AsyncTask<Void, Void, ArrayList<Restaurant>> {
        ArrayList<Restaurant> restaurants = new ArrayList<>();
        @Override
        protected ArrayList<Restaurant> doInBackground(Void... args) {
            YelpAPIFactory apiFactory = new YelpAPIFactory(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
            YelpAPI yelpAPI = apiFactory.createAPI();
            Map<String, String> params = new HashMap<>();
            params.put("term", TERM);
            params.put("limit", "20");
            CoordinateOptions coordinate = CoordinateOptions.builder().latitude(Double.valueOf(mLatitude)).
                    longitude(Double.valueOf(mLongitude)).build();
            Call<SearchResponse> call = yelpAPI.search(coordinate, params);
            try {
                Response<SearchResponse> response = call.execute();
                SearchResponse searchResponse = response.body();

                // searchResponse.total doesn't return the limit I've set which is 20
                // so I have to set the loop time to 20 manually.

                // int totalNumberOfResults = searchResponse.total();

                ArrayList<Business> businesses = searchResponse.businesses();
                for (int i = 0; i < 20; i++) {
                    restaurants.add(new Restaurant(businesses.get(i).name(), businesses.get(i).rating()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return restaurants;
        }

        @Override
        protected void onPostExecute(ArrayList<Restaurant> restaurants) {
            ListAdapter mAdapter = new MyListAdapter(getApplicationContext(), restaurants);
            ListView mListView = (ListView) findViewById(R.id.res_name);
            mListView.setAdapter(mAdapter);
        }
    }*/
}
