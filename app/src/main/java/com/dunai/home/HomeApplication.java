package com.dunai.home;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.dunai.home.client.HomeClient;

public class HomeApplication extends Application implements LifecycleObserver {
    @Override
    public void onCreate() {
        super.onCreate();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void on_create() {
        Log.i("HomeApp.HomeApplication", "ON_CREATE");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void on_destroy() {
        Log.i("HomeApp.HomeApplication", "ON_DESTROY");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void on_start() {
        Log.i("HomeApp.HomeApplication", "ON_START");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void on_stop() {
        Log.i("HomeApp.HomeApplication", "ON_STOP");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void on_resume() {
        Log.i("HomeApp.HomeApplication", "ON_RESUME");
        HomeClient client = HomeClient.getInstance();
        client.setContext(this.getApplicationContext());
        client.connect();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void on_pause() {
        Log.i("HomeApp.HomeApplication", "ON_PAUSE");
        HomeClient client = HomeClient.getInstance();
        client.disconnect();
    }
}
