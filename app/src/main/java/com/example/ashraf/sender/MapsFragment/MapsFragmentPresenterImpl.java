package com.example.ashraf.sender.MapsFragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.example.ashraf.sender.ApiMethods.ApiMethods;
import com.example.ashraf.sender.Main.MainActivityPresenter;
import com.example.ashraf.sender.Pubnub.MyPubnub;
import com.example.ashraf.sender.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import Model.PublishedLocation;
import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ashraf on 11/20/2016.
 */

public class MapsFragmentPresenterImpl implements MapsFragmentPresenter{
    MapsFragment mapsFragment ;
    public MapsFragmentPresenterImpl(MapsFragment mapsFragment) {
        this.mapsFragment=mapsFragment;
    }

    @Override

    public void initView() {
        mapsFragment.mapView = (MapView) mapsFragment.rootview.findViewById(R.id.map);
    }

    @Override
    public void initMap(Bundle savedInstanceState) {

        if (isGooglePlayServiceInstalled()) {
            mapsFragment.mapView.onCreate(savedInstanceState);
            mapsFragment.mapView.onResume();
            Log.d("SEQUENCE", "fragment view created");
            mapsFragment.mapView.getMapAsync(mapsFragment);

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder( mapsFragment.getActivity());
            builder.setMessage("Please install Google Play Service to continue :)");
            builder.setCancelable(false);
            builder.setPositiveButton("Install", getGoogleMapsListener());
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    @Override
    public void initComponants() {
        mapsFragment.mMap.getUiSettings().setMyLocationButtonEnabled(true);
        Log.d("MYCHECK", "before check");
        if (ActivityCompat.checkSelfPermission(mapsFragment.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mapsFragment.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= 23) { // Marshmallow
                Log.d("MYCHECK", "before request");
                ActivityCompat.requestPermissions(mapsFragment.getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                Log.d("MYCHECK", "after  request");
                return;
            }


        }
        try {
            mapsFragment.mMap.setMyLocationEnabled(true);
            LocationManager locationManager = (LocationManager) mapsFragment.getActivity().getSystemService(Context.LOCATION_SERVICE);
            mapsFragment.myLocation = locationManager.getLastKnownLocation((LocationManager.NETWORK_PROVIDER));
            mapsFragment.mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            LatLng latLng = new LatLng(mapsFragment.myLocation.getLatitude(), mapsFragment.myLocation.getLongitude());

            // Show the current location in Google Map
            mapsFragment.mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));


            // Zoom in the Google Map
            mapsFragment.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));

            mapsFragment.mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(mapsFragment.getActivity()));
            mapsFragment.mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {

                    if (cameraPosition.zoom < mapsFragment.currentZoom) {

                        PublishedLocation publishedLocation = new PublishedLocation();
                        publishedLocation.setOperation("0");
                        publishedLocation.setLatitude(String.valueOf(mapsFragment.myLocation.getLatitude()));
                        publishedLocation.setLongitude(String.valueOf(mapsFragment.myLocation.getLongitude()));
                        mapsFragment.myPubnub.publishLocation(publishedLocation);
                        Toast.makeText(mapsFragment.getContext(), "Zoom out detected and Location Pushed", Toast.LENGTH_SHORT).show();

                    }
                    mapsFragment.currentZoom = cameraPosition.zoom;
                }
            });

            Log.d("SEQUENCE", "inti components map");
            mapsFragment.mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {


                    double dis =calculateDistance(location.getLatitude(), mapsFragment.myLocation.getLatitude(), location.getLongitude(), mapsFragment.myLocation.getLongitude());
                    Log.d("SEQUENCE", "calculate  distance");
                    if (dis > 1) {
                        Log.d("SEQUENCE", "  distance more than 1");
                        Log.d("LOCATION_CHANGED", "Move more 1 M  : " + String.valueOf(dis));
                        PublishedLocation publishedLocation = new PublishedLocation();
                        publishedLocation.setOperation("0");
                        publishedLocation.setLatitude(String.valueOf(mapsFragment.myLocation.getLatitude()));
                        publishedLocation.setLongitude(String.valueOf(mapsFragment.myLocation.getLongitude()));
                        mapsFragment.myPubnub.publishLocation(publishedLocation);
                        Toast.makeText(mapsFragment.getContext(), "Location Pushed", Toast.LENGTH_SHORT).show();
                        mapsFragment.myLocation = location;
                    }

                }
            });
            mapsFragment.mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    mapsFragment.companyMarker.setPosition(marker.getPosition());
                    drawPath();
                }
            });
            Log.d("SEQUENCE", "end oif init component map ");
            drawPath();
        } catch (Exception E) {
            Toast.makeText(mapsFragment.getContext(), "Something went wrong please try again", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public double calculateDistance(double lat1, double lat2, double lon1, double lon2) {
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapsFragment.mMap = googleMap;
        mapsFragment.companyMarker = mapsFragment.mMap.addMarker(new MarkerOptions().position(new LatLng(29.9855008, 30.9572015)).title("Comapny").draggable(true));
        Log.d("SEQUENCE", "onready map");
    }

    @Override
    public void drawPath() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://maps.googleapis.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiMethods service = retrofit.create(ApiMethods.class);

        retrofit2.Call<ResponseBody> call = service.drawpath(
                String.valueOf(mapsFragment.myLocation.getLatitude()) + "," + String.valueOf(mapsFragment.myLocation.getLongitude()),
                String.valueOf(mapsFragment.companyMarker.getPosition().latitude) + "," + String.valueOf(mapsFragment.companyMarker.getPosition().longitude), false
                , "driving", true);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.body() != null) {
                    try {
                        String res = String.valueOf(response.body().string());
                        resolvePathJsonResult(res);
                        ;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(mapsFragment.getContext(), "Erro", Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                Toast.makeText(mapsFragment.getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                t.printStackTrace();

            }

        });

    }

    @Override
    public void resolvePathJsonResult(String result) {

        try {
            //Tranform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            mapsFragment.line = mapsFragment.mMap.addPolyline(new PolylineOptions()
                    .addAll(list)
                    .width(12)
                    .color(Color.parseColor("#05b1fb"))//Google maps blue color
                    .geodesic(true)
            );


        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(mapsFragment.getContext(), e.getMessage() + "error", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    @Override
    public boolean isGooglePlayServiceInstalled() {
        try {
            ApplicationInfo info = mapsFragment.getActivity().getPackageManager().getApplicationInfo("com.google.android.gms", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    public boolean isGoogleStoreInstalled() {
        try {
            ApplicationInfo info = mapsFragment.getActivity().getPackageManager().getApplicationInfo("com.google.android.apps.vending", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {

            try {
                ApplicationInfo info = mapsFragment.getActivity().getPackageManager().getApplicationInfo("com.google.android.apps.market", 0);
                return true;
            } catch (PackageManager.NameNotFoundException e1) {
                return false;
            }


        }
    }

    @Override
    public DialogInterface.OnClickListener getGoogleMapsListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isGoogleStoreInstalled()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps"));
                    mapsFragment.getContext().startActivity(intent);
                    //Finish the activity so they can't circumvent the check
//                finish();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mapsFragment.getActivity());
                    builder.setMessage("Install Google Store Fisrt");
                    AlertDialog dialog1 = builder.create();
                    dialog1.show();
//                new Transation(getContext()).redirect_to_object_fragment();

                }
            }
        };
    }


}
