package com.example.ashraf.sender.Main;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.example.ashraf.sender.MapsFragment.MapsFragment;
import com.example.ashraf.sender.Pubnub.MyPubnub;
import com.example.ashraf.sender.R;
import com.example.ashraf.sender.RecycleFragment.RecycleviewFragment;
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    RecycleviewFragment recycleviewFragment;
    MapsFragment mapsFragment;
    MyPubnub myPubnub;
    TabLayout mTabLayout;
    ViewPager mPager;
    FloatingActionButton sendMessageFAB;
    EditText messageEdittext;
    public MainActivityPresenter mainActivityPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivityPresenter = new MainActivityPresenterImpl(this);
        mainActivityPresenter.initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mainActivityPresenter.startPubnubConnection();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainActivityPresenter.startPubnubConnection();

    }

    @Override
    protected void onStop() {
        super.onStop();
        mainActivityPresenter.stopPubnubConnection();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendMessageFloatActionButton:
                mainActivityPresenter.publishMessage(messageEdittext.getText().toString());
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        mainActivityPresenter.requestPermission(requestCode, permissions, grantResults);
    }
}
