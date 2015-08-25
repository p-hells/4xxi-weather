package com.example.home.fourxxiweather.activities;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.home.fourxxiweather.R;
import com.example.home.fourxxiweather.adapters.AutoCompleteAdapter;
import com.example.home.fourxxiweather.models.AutoCompletePlace;
import com.example.home.fourxxiweather.utils.CommonMethods;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import java.util.Locale;

public class CityFinderActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    AutoCompleteTextView actvCityFinder;

    private GoogleApiClient mGoogleApiClient;
    private AutoCompleteAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_finder);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        actvCityFinder = (AutoCompleteTextView) findViewById(R.id.actvCityFinder);

        mAdapter = new AutoCompleteAdapter(this);
        actvCityFinder.setAdapter(mAdapter);
        actvCityFinder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    AutoCompletePlace place = (AutoCompletePlace) parent.getItemAtPosition(position);
                    actvCityFinder.setText("");
                    actvCityFinder.setEnabled(false);
                    findPlaceById(place.getId());
                    actvCityFinder.setEnabled(true);
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void findPlaceById(String id) {
        if (TextUtils.isEmpty(id) || mGoogleApiClient == null || !mGoogleApiClient.isConnected())
            return;

        Places.GeoDataApi.getPlaceById(mGoogleApiClient, id).setResultCallback(new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(PlaceBuffer places) {
                Place place = null;
                if (places.getStatus().isSuccess()) {
                    place = places.get(0);
                    mAdapter.clear();
                }
                //Release the PlaceBuffer to prevent a memory leak
                processPlace(place);
                places.release();
            }
        });
    }

    private void processPlace(Place place) {
        if (place != null) {
            Address address = CommonMethods.getAddress(this, place, null);
            Address addressInt = CommonMethods.getAddress(this, place, Locale.ENGLISH);
            if (address != null && address.getLocality() != null && address.getCountryName() != null
                    && address.getCountryCode() != null && addressInt != null && addressInt.getLocality() != null) {
                Intent data = new Intent();
                data.putExtra("Country", address.getCountryName());
                data.putExtra("CountryCode", address.getCountryCode());
                data.putExtra("City", address.getLocality());
                data.putExtra("CityInt", addressInt.getLocality());
                setResult(RESULT_OK, data);
                finish();
            } else {
                Toast.makeText(this, getString(R.string.txt_empty_place), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mAdapter.setGoogleApiClient(null);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mAdapter != null)
            mAdapter.setGoogleApiClient(mGoogleApiClient);

    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }
}
