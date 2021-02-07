package com.dunai.home.client;

public interface DataReceivedListener {
    void onDataReceived(String topic, String payload);
}
