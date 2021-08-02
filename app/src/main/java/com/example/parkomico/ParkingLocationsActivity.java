package com.example.parkomico;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ParkingLocationsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    SearchView searchAddressEditText;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==0 && permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            getUserCurrentLocation(mMap);
        }

    }

    public void initialise()
    {

        searchAddressEditText=findViewById(R.id.searchAddressEditText);

        searchAddressEditText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                String location=searchAddressEditText.getQuery().toString();

                List<Address> addressList=null;

                if(!location.equals(""))
                {
                    Geocoder geocoder=new Geocoder(ParkingLocationsActivity.this);

                    try {
                        addressList=geocoder.getFromLocationName(location, 1);

                        Address address=addressList.get(0);

                        mMap.addMarker(new MarkerOptions().title(location).position(new LatLng(address.getLatitude(), address.getLongitude())));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(address.getLatitude(), address.getLongitude()), 12));

                    } catch(Exception e)
                    {
                        Toast.makeText(ParkingLocationsActivity.this, "Location not found", Toast.LENGTH_SHORT).show();

                        e.printStackTrace();
                    }
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_locations);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initialise();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        getUserCurrentLocation(mMap);

        getParkingLocations(new LatLng(28.633583, 77.088570));
        getParkingLocations(new LatLng(28.622416, 77.214760));
        getParkingLocations(new LatLng(28.950956, 77.074315));

    }

    public void getUserCurrentLocation(final GoogleMap mMap)
    {
        FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(ParkingLocationsActivity.this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ParkingLocationsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else {
            locationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
                    try {
                        String address=new Geocoder(ParkingLocationsActivity.this, Locale.getDefault()).getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0).getAddressLine(0);
                        mMap.addMarker(new MarkerOptions().position(sydney).title(address));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 5));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void getParkingLocations(LatLng latLng)
    {
        mMap.addMarker(new MarkerOptions().position(latLng).title("Demo location").icon(BitmapDescriptorFactory.fromBitmap(markerBitmap("10"))));
    }

    public Bitmap markerBitmap(String cost)
    {
        View view= LayoutInflater.from(ParkingLocationsActivity.this).inflate(R.layout.marker_google_maps, null, false);

        ((TextView) view.findViewById(R.id.locationCostTextView)).setText(cost);

        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.buildDrawingCache();

        Bitmap bitmap=Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas=new Canvas(bitmap);

        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);

        Drawable drawable=view.getBackground();

        if(drawable!=null)
        {
            drawable.draw(canvas);
        }

        view.draw(canvas);

        return bitmap;

    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.animstart, R.anim.animend);

    }
}
