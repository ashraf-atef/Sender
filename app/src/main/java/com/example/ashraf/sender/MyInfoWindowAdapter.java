package com.example.ashraf.sender;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ashraf on 11/16/2016.
 */

public class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View myContentsView;
    CardView cv;
    TextView personName;
    TextView personDate;
    TextView personDesc;
    CircleImageView personPhoto;
    CircleImageView typePhoto;
    Activity appCompatActivity ;

    MyInfoWindowAdapter(Activity appCompatActivity) {
        this.appCompatActivity=appCompatActivity ;
        myContentsView = appCompatActivity.getLayoutInflater().inflate(R.layout.row_layout, null);
    }

    @Override
    public View getInfoContents(Marker marker) {

        int position = Integer.parseInt(marker.getTitle());

        return myContentsView;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        // TODO Auto-generated method stub
        return null;
    }

}