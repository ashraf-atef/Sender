package com.example.ashraf.sender.Pubnub;

import Model.ChatMessage;
import Model.PublishedLocation;

/**
 * Created by ashraf on 11/20/2016.
 */

public interface PubnubPresenter {
    void set_Config();
    void subscribe() ;
    void publishLocation(PublishedLocation location) ;
    void publishMessage(ChatMessage chatMessage);
    void setListioner() ;
    void unsubscribe() ;
}
