package com.example.ashraf.sender.Main;

import android.content.pm.PackageManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import com.example.ashraf.sender.MapsFragment.MapsFragment;
import com.example.ashraf.sender.Pubnub.MyPubnub;
import com.example.ashraf.sender.R;
import com.example.ashraf.sender.RecycleFragment.RecycleviewFragment;

import java.util.Date;

import Model.ChatMessage;

/**
 * Created by ashraf on 11/20/2016.
 */

public class MainActivityPresenterImpl implements MainActivityPresenter {
    MainActivity mainActivity;

    public MainActivityPresenterImpl(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void initView() {
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mainActivity.mTabLayout = (TabLayout) mainActivity.findViewById(R.id.tabLayout);
        mainActivity.mPager = (ViewPager) mainActivity.findViewById(R.id.tab_viewpager);
        mainActivity.mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mainActivity.mTabLayout));
        mainActivity.sendMessageFAB = (FloatingActionButton) mainActivity.findViewById(R.id.sendMessageFloatActionButton);
        mainActivity.messageEdittext = (EditText) mainActivity.findViewById(R.id.messageEdittext);
        mainActivity.mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mainActivity.mPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mainActivity.myPubnub = new MyPubnub(mainActivity);
        setViewPager(mainActivity.mPager);
        mainActivity.mTabLayout.setupWithViewPager(mainActivity.mPager);
        mainActivity.sendMessageFAB.setOnClickListener(mainActivity);

    }

    @Override
    public void startPubnubConnection() {
        Log.d("SEQUENCE", "Actvivty Started");
        mainActivity.myPubnub.set_Config();
        Log.d("SEQUENCE", "Actvivty Started config");
        mainActivity.myPubnub.subscribe();
        Log.d("SEQUENCE", "Actvivty Started subscribe");
        mainActivity.myPubnub.setListioner();
    }

    @Override
    public void stopPubnubConnection() {
        mainActivity.myPubnub.unsubscribe();
        Log.d("SEQUENCE", "Actvivty Paused unsubscribe");
    }

    @Override
    public void setViewPager(ViewPager viewPager) {
        mainActivity.mapsFragment = new MapsFragment();
        mainActivity.mapsFragment.setMyPubnub(mainActivity.myPubnub);
        mainActivity.recycleviewFragment = new RecycleviewFragment();
        ViewPagerAdaptor adapter = new ViewPagerAdaptor(mainActivity.getSupportFragmentManager());
        adapter.addFrag(mainActivity.mapsFragment, "Maps");
        adapter.addFrag(mainActivity.recycleviewFragment, "Message");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void publishMessage(String content) {
        if (content.trim().length() > 0) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setPhotoUrl(null);
            chatMessage.setPhotoType(null);
            chatMessage.setContent(content);
            chatMessage.setNameSender("Sender");
            chatMessage.setDate(String.valueOf(new Date().getTime()));
            mainActivity.myPubnub.publishMessage(chatMessage);
            mainActivity.messageEdittext.setText("");
            Toast.makeText(mainActivity.getBaseContext(), "Message Pushed", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void displayListionerResult(final String text) {

        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (text != null && text.trim().length() > 0) {


                    String[] arr = text.replace("\"", "").replaceAll("\\[", "").replaceAll("\\]", "").split(",");
                    if (arr != null) {


                        if (arr[0].equals("1")) {
                            if (arr.length == 6) {
                                Toast.makeText(mainActivity.getBaseContext(), "Message Received", Toast.LENGTH_SHORT).show();
                                ChatMessage chatMessage = new ChatMessage();
                                chatMessage.setPhotoUrl(arr[1]);
                                chatMessage.setPhotoType(arr[2]);
                                chatMessage.setNameSender(arr[3]);
                                chatMessage.setDate(arr[4]);
                                chatMessage.setContent(arr[5]);
                                mainActivity.recycleviewFragment.recycleAdaptor.chatMessageList.add(chatMessage);
                                mainActivity.recycleviewFragment.recycleAdaptor.notifyDataSetChanged();

                                if ("Sender".equals(chatMessage.getNameSender())) {
                                    mainActivity.recycleviewFragment.recyclerView.scrollToPosition(
                                            mainActivity.recycleviewFragment.recycleAdaptor.getItemCount() - 1
                                    );
                                }
                                else
                                {

                                    if (mainActivity.recycleviewFragment.llm.findLastVisibleItemPosition()==
                                            mainActivity.recycleviewFragment.recycleAdaptor.getItemCount()-2)
                                    {
                                        mainActivity.recycleviewFragment.recyclerView.scrollToPosition(
                                                mainActivity.recycleviewFragment.recycleAdaptor.getItemCount() - 1
                                        );
                                    }
                                }

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
    public void requestPermission(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == 1) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MYCHECK", "Allow");
                mainActivity.mapsFragment.mapsFragmentPresenter.initComponants();

            } else { // if permission is not granted
                Log.d("MYCHECK", "not allow Allow " + String.valueOf(grantResults.length > 0) + " " + String.valueOf(grantResults[0] == PackageManager.PERMISSION_GRANTED));
                // decide what you want to do if you don't get permissions
            }
        }
    }
}
