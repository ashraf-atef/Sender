package com.example.ashraf.sender.PreviousActivity;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.example.ashraf.sender.R;

public class PreviousMainActivity extends AppCompatActivity {
PreviousMainActivityPresenter previousMainActivityPresenter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_main);
previousMainActivityPresenter=new PreviousMainActivityPresenterImpl(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        previousMainActivityPresenter.checkGPS();
    }

}
