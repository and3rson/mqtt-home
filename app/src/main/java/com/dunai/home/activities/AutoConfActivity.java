package com.dunai.home.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.dunai.home.R;
import com.dunai.home.client.HomeClient;

public class AutoConfActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_conf);

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();

        System.out.println("AutoConf action: " + action);
        System.out.println("AutoConf data: " + data);

        String host = data.getQueryParameter("host");
        String port = data.getQueryParameter("port");
        String username = data.getQueryParameter("username");
        String password = data.getQueryParameter("password");

        ((TextView) findViewById(R.id.autoConfHostPort)).setText(String.format("%s:%s", host, port));
        ((TextView) findViewById(R.id.autoConfUsername)).setText(username);
        ((TextView) findViewById(R.id.autoConfPassword)).setText(password.replaceAll("(.)", "*"));

        findViewById(R.id.autoConfLoadConfig).setOnClickListener(v -> {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putString("host", host);
            editor.putString("port", port);
            editor.putString("username", username);
            editor.putString("password", password);
            editor.apply();
            HomeClient.getInstance().reconnect();
            Intent main = new Intent(this, MainActivity.class);
            startActivity(main);
            finish();
        });
//        finish();
    }
}