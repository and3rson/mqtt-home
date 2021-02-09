package com.dunai.home.client.interfaces;

import com.dunai.home.client.ConnectionState;

public interface ConnectionStateChangedListener {
    void onConnectionStateChanged(ConnectionState connectionState);
}
