package com.dunai.home.client.interfaces;

public interface DataReceivedListener {
    void onDataReceived(String topic, String payload);
}
