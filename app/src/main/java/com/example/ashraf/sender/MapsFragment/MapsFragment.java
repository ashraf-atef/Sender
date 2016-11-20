package com.example.ashraf.sender.MapsFragment;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.ashraf.sender.Pubnub.MyPubnub;
import com.example.ashraf.sender.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;


public class MapsFragment extends Fragment implements OnMapReadyCallback {

    public void setMyPubnub(MyPubnub myPubnub) {
        this.myPubnub = myPubnub;
        Log.d("SEQUENCE", "Fragment set pubub");
    }
    public MyPubnub myPubnub;
    public GoogleMap mMap;
    public MapView mapView;
    public Location myLocation;
    public float currentZoom = 16;
    public Polyline line;
    public View rootview;
    public Marker companyMarker;
    public MapsFragmentPresenter mapsFragmentPresenter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_maps, container, false);
        mapsFragmentPresenter=new MapsFragmentPresenterImpl(this);
        mapsFragmentPresenter.initView();
        return rootview;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapsFragmentPresenter.initMap(savedInstanceState);

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapsFragmentPresenter.onMapReady(googleMap);
        mapsFragmentPresenter.initComponants();
    }


}
