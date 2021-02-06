package com.dunai.home;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private HomeClient client;
    private MenuItem menuConnectionStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("HomeApp.MainActivity", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.hide(getSupportFragmentManager().findFragmentById(R.id.fragment_tiles));
        ft.commit();

        this.client = HomeClient.getInstance();
        this.client.setContext(this.getApplicationContext());
        client.setConnectionStateChangedListener(this::setConnectionState);
        setConnectionState(client.connectionState);
        Log.i("HomeApp", "onCreate");

        client.connect();
    }

//    @Override
//    protected void onStart() {
//        Log.i("HomeApp.MainActivity", "onStart");
//        super.onStart();
//    }

    @Override
    protected void onDestroy() {
        Log.i("HomeApp.MainActivity", "onDestroy");
        client.disconnect();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menuConnectionStatus = menu.findItem(R.id.menuConnectionStatus);
        if (client != null) {
            setConnectionState(client.connectionState);
        }
        System.out.println("Menu: " + menuConnectionStatus);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            this.startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void setConnectionState(ConnectionState connectionState) {
//        final TextView view = ((TextView) findViewById(R.id.connectionState));
//        MenuItem view = findViewById(R.id.menuConnectionStatus);
        if (menuConnectionStatus == null) {
            return;
        }
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();

        switch (connectionState) {
            case OFFLINE:
                menuConnectionStatus.setTitle("Offline");
                ft1.hide(getSupportFragmentManager().findFragmentById(R.id.fragment_tiles));
                ft1.show(getSupportFragmentManager().findFragmentById(R.id.fragment_not_connected));
                ft1.commit();
                break;
            case CONNECTING:
                menuConnectionStatus.setTitle("Connecting...");
                break;
            case CONNECTED:
                menuConnectionStatus.setTitle("Connected");
                ft1.hide(getSupportFragmentManager().findFragmentById(R.id.fragment_not_connected));
                ft1.show(getSupportFragmentManager().findFragmentById(R.id.fragment_tiles));
                ft1.commit();
                getSupportActionBar().setTitle(PreferenceManager.getDefaultSharedPreferences(this).getString("host", ""));
                break;
        }
    }
}