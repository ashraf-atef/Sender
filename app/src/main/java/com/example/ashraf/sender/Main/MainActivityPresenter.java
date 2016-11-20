package com.example.ashraf.sender.Main;

import android.support.v4.view.ViewPager;

import Model.ChatMessage;

/**
 * Created by ashraf on 11/20/2016.
 */

public interface MainActivityPresenter {
     void initView();
     void startPubnubConnection();
     void stopPubnubConnection();
     void setViewPager(ViewPager viewPager) ;
     void publishMessage(String content) ;
     void displayListionerResult(String text);
     void requestPermission(int requestCode, String[] permissions, int[] grantResults);


}
