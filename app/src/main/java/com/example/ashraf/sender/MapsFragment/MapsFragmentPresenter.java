package com.example.ashraf.sender.MapsFragment;

import android.content.DialogInterface;
import android.os.Bundle;

import com.example.ashraf.sender.Pubnub.MyPubnub;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by ashraf on 11/20/2016.
 */

public interface MapsFragmentPresenter {

    void initView();
    void initMap(Bundle savedInstanceState);
    void initComponants() ;
    double calculateDistance(double lat1, double lat2, double lon1, double lon2);
    void onMapReady(GoogleMap googleMap);
    void drawPath();
    void resolvePathJsonResult(String result);
    List<LatLng> decodePoly(String encoded) ;
    boolean isGooglePlayServiceInstalled();
    boolean isGoogleStoreInstalled();
    DialogInterface.OnClickListener getGoogleMapsListener();



}
