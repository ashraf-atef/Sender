package com.example.ashraf.sender;

import android.Manifest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;

import Model.PublishedLocation;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    public void setMyPubnub(MyPubnub myPubnub) {
        this.myPubnub = myPubnub;
        Log.d("SEQUENCE", "Fragment set pubub");
    }

    public MyPubnub myPubnub;
    private GoogleMap mMap;
    private MapView mapView;
    Location myLocation;
    float currentZoom = 16;
    SupportMapFragment mapFragment;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_maps, container, false);
        mapView = (MapView) rootview.findViewById(R.id.map);
        return rootview;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (isGooglePlayServiceInstalled()) {
            mapView.onCreate(savedInstanceState);
            mapView.onResume();
            Log.d("SEQUENCE", "fragment view created");
            mapView.getMapAsync(this);

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Please install Google Play Service to continue :)");
            builder.setCancelable(false);
            builder.setPositiveButton("Install", getGoogleMapsListener());
            AlertDialog dialog = builder.create();
            dialog.show();
        }


    }


    private void init_companents() {
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        Log.d("MYCHECK","before check");
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= 23) { // Marshmallow
                Log.d("MYCHECK","before request");
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                Log.d("MYCHECK","after  request");
                return;
            }


        }
        mMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        myLocation = locationManager.getLastKnownLocation((LocationManager.NETWORK_PROVIDER));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

        // Show the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));


        // Zoom in the Google Map
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));

        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(getActivity()));
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                if (cameraPosition.zoom < currentZoom) {

                    PublishedLocation publishedLocation = new PublishedLocation();
                    publishedLocation.setOperation("0");
                    publishedLocation.setLatitude(String.valueOf(myLocation.getLatitude()));
                    publishedLocation.setLongitude(String.valueOf(myLocation.getLongitude()));
                    myPubnub.publish(publishedLocation);

                }
                currentZoom = cameraPosition.zoom;
            }
        });
        Log.d("SEQUENCE", "inti components map");
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {


                double dis = CalculationByDistance(location.getLatitude(), myLocation.getLatitude(), location.getLongitude(), myLocation.getLongitude());
                Log.d("SEQUENCE", "calculate  distance");
                if (dis > 1) {
                    Log.d("SEQUENCE", "  distance more than 1");
                    Log.d("LOCATION_CHANGED", "Move more 1 M  : " + String.valueOf(dis));
                    PublishedLocation publishedLocation = new PublishedLocation();
                    publishedLocation.setOperation("0");
                    publishedLocation.setLatitude(String.valueOf(myLocation.getLatitude()));
                    publishedLocation.setLongitude(String.valueOf(myLocation.getLongitude()));
                    myPubnub.publish(publishedLocation);
                    Toast.makeText(getContext(), "Location Pushed", Toast.LENGTH_SHORT).show();
                    myLocation = location;
                }

            }
        });
        Log.d("SEQUENCE", "end oif init component map ");
    }

    public double CalculationByDistance(double lat1, double lat2, double lon1, double lon2) {
        int Radius = 6371;// radius of earth in Km

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;

        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));

        System.out.println("km:" + km);
        System.out.println("kmInDec:" + kmInDec);
        System.out.println("meter:" + meter);
        System.out.println("meterInDec:" + meterInDec);
        System.out.println("----------------------------------------------");

        return meter * 1000;
    }

    public static void main(String[] args) {
        MapsFragment mapsActivity = new MapsFragment();
        System.out.println(mapsActivity.CalculationByDistance(30.545835, 30.545519, 31.134391, 31.134418));
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        Log.d("SEQUENCE", "onready map");
        init_companents();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == 1) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

init_companents();

                Log.d("MYCHECK","Allow perm");
            } else { // if permission is not granted
                Log.d("MYCHECK","not Allow perm");
                // decide what you want to do if you don't get permissions
            }
        }
    }


    // ***************************************** Validation on Map ****************************************************
    public boolean isGooglePlayServiceInstalled() {
        try {
            ApplicationInfo info = getActivity().getPackageManager().getApplicationInfo("com.google.android.gms", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public boolean isGoogleStoreInstalled() {
        try {
            ApplicationInfo info = getActivity().getPackageManager().getApplicationInfo("com.google.android.apps.vending", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {

            try {
                ApplicationInfo info = getActivity().getPackageManager().getApplicationInfo("com.google.android.apps.market", 0);
                return true;
            } catch (PackageManager.NameNotFoundException e1) {
                return false;
            }


        }
    }

    public DialogInterface.OnClickListener getGoogleMapsListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isGoogleStoreInstalled()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps"));
                    getContext().startActivity(intent);

                    //Finish the activity so they can't circumvent the check
//                finish();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Install Google Store Fisrt");
                    AlertDialog dialog1 = builder.create();
                    dialog1.show();
//                new Transation(getContext()).redirect_to_object_fragment();

                }
            }
        };
    }


}
