package com.example.ashraf.sender;

import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Model.ChatMessage;
import Model.PublishedLocation;

/**
 * Created by ashraf on 11/16/2016.
 */

public class MyPubnub {
    PubNub pubnub;
    MainActivity mainActivity;

    public MyPubnub(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
    public void set_config() {
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey("sub-c-8631c4d0-aaa7-11e6-be20-0619f8945a4f");
        pnConfiguration.setPublishKey("pub-c-5cfe59a8-2468-4ed2-97b1-ece51855c8bd");
        pnConfiguration.setSecure(false);

        pubnub = new PubNub(pnConfiguration);
    }

    public void subscribe() {
        pubnub.subscribe()
                .channels(Arrays.asList("my_channel")) // subscribe to channels
                .execute();
    }

    public void publish(PublishedLocation location) {
        List<String> list = new ArrayList<>();
        list.add(location.getOperation());
        list.add(location.getLatitude());
        list.add(location.getLongitude());

        Log.d("SEQUENCE", "start publish");
        pubnub.publish()
                .message(list)
                .channel("my_channel")
                .shouldStore(true)
                .usePOST(true)
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        if (status.isError()) {
                            // something bad happened.
                            Log.d("ML", "error happened while publishing: " + status.toString() + "\n" + String.valueOf(result));
                        } else {
                            Log.d("ML", "publish worked! timetoken: " + result.getTimetoken());
                            Log.d("SEQUENCE", " published ");
                        }
                    }
                });
    }

    public void publish_message(ChatMessage chatMessage) {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add(chatMessage.getPhotoUrl());
        list.add(chatMessage.getPhotoType());
        list.add(chatMessage.getNameSender());
        list.add(chatMessage.getDate());
        list.add(chatMessage.getContent());

        pubnub.publish()
                .message(list)
                .channel("my_channel")
                .shouldStore(true)
                .usePOST(true)
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        if (status.isError()) {
                            // something bad happened.
                            Log.d("ML", "error happened while publishing: " + status.toString() + "\n" + String.valueOf(result));
                        } else {
                            Log.d("ML", "publish worked! timetoken: " + result.getTimetoken());
                            Log.d("SEQUENCE", " published ");
                        }
                    }
                });
    }

    public void set_listioner() {

        pubnub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
//                if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {
//
////                    pubnub.publish().channel("my_channel").message("").async(new PNCallback<PNPublishResult>() {
////                        @Override
////                        public void onResponse(PNPublishResult result, PNStatus status) {
////
////                        }
////                    });
//                }

            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {

                JsonNode jsonNode = message.getMessage();
                if (jsonNode != null) {


                    mainActivity.display(jsonNode.toString());

                    Log.d("ML message", jsonNode.toString() + " " + String.valueOf(message.getTimetoken()));

                } else {
                    Log.d("ML message Null", "nulllllllllllllllllll");

                }
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });
        pubnub.subscribe().channels(Arrays.asList("my_channel"));


    }
    public void unsubscribe() {
        pubnub.unsubscribe()
                .channels(Arrays.asList("my_channel"))
                .execute();
    }

}
