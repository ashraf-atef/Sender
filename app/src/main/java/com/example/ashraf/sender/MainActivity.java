package com.example.ashraf.sender;


import android.Manifest;
import android.content.pm.PackageManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

import Model.ChatMessage;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    MyPubnub myPubnub;
    TabLayout mTabLayout;
    ViewPager mPager;
    FloatingActionButton sendMessageFAB;
    EditText messageEdittext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mPager = (ViewPager) findViewById(R.id.tab_viewpager);
        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        sendMessageFAB = (FloatingActionButton) findViewById(R.id.sendMessageFloatActionButton);
        messageEdittext = (EditText) findViewById(R.id.messageEdittext);
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Log.d("SEQUENCE", "Actvivty created");
        myPubnub = new MyPubnub(this);
        Log.d("SEQUENCE", "Actvivty created pubnub created");

        setupViewPager(mPager);
        mTabLayout.setupWithViewPager(mPager);


//        if (getIntent().getExtras() == null) {
//            MapsFragment mapsActivity = new MapsFragment();
//            mapsActivity.setMyPubnub(myPubnub);
//            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.add(R.id.frame, mapsActivity, "test");
//            ft.commit();
//            Log.d("SEQUENCE", "Actvivty created commit fragment");
//        } else {
//            ((MapsFragment) getSupportFragmentManager().findFragmentByTag("test")).setMyPubnub(myPubnub);
//        }

        sendMessageFAB.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("SEQUENCE", "Actvivty Started");
        myPubnub.set_config();
        Log.d("SEQUENCE", "Actvivty Started config");
        myPubnub.subscribe();
        Log.d("SEQUENCE", "Actvivty Started subscribe");
        myPubnub.set_listioner();
    }

    @Override
    protected void onPause() {


        super.onPause();
        myPubnub.unsubscribe();
        Log.d("SEQUENCE", "Actvivty Paused unsubscribe");
    }

    @Override
    protected void onStop() {
        super.onStop();


    }

    RecycleviewFragment recycleviewFragment;

    private void setupViewPager(ViewPager viewPager) {
        MapsFragment mapsFragment = new MapsFragment();
        mapsFragment.setMyPubnub(myPubnub);
        recycleviewFragment = new RecycleviewFragment();
        ViewPagerAdaptor adapter = new ViewPagerAdaptor(getSupportFragmentManager());
        adapter.addFrag(mapsFragment, "Maps");
        adapter.addFrag(recycleviewFragment, "Message");
        viewPager.setAdapter(adapter);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendMessageFloatActionButton:
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setPhotoUrl(null);
                chatMessage.setPhotoType(null);
                chatMessage.setContent(messageEdittext.getText().toString());
                chatMessage.setNameSender("Sender");
                chatMessage.setDate(String.valueOf(new Date().getTime()));
                myPubnub.publish_message(chatMessage);
//                recycleviewFragment.recycleAdaptor.chatMessageList.add(chatMessage);
//                recycleviewFragment.recycleAdaptor.notifyDataSetChanged();
                Toast.makeText(getBaseContext(), "Message Pushed", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void display(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (text != null && text.trim().length() > 0) {

                    Toast.makeText(getBaseContext(), "Received", Toast.LENGTH_LONG).show();
                    String[] arr = text.replace("\"", "").replaceAll("\\[", "").replaceAll("\\]", "").split(",");
                    if (arr != null) {

                        if (arr[0].equals("1")) {
                            if (arr.length == 6) {
                                Toast.makeText(getBaseContext(), "Message Received", Toast.LENGTH_SHORT).show();
                                ChatMessage chatMessage = new ChatMessage();
                                chatMessage.setPhotoUrl(arr[1]);
                                chatMessage.setPhotoType(arr[2]);
                                chatMessage.setNameSender(arr[3]);
                                chatMessage.setDate(arr[4]);
                                chatMessage.setContent(arr[5]);
                                recycleviewFragment.recycleAdaptor.chatMessageList.add(chatMessage);
                                recycleviewFragment.recycleAdaptor.notifyDataSetChanged();

                            }
                        }
                    }
                } else {
                    Log.d("MESSAGE_NULL", null);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == 1) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // start to find location...

            } else { // if permission is not granted

                // decide what you want to do if you don't get permissions
            }
        }
    }
}
