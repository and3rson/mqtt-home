package com.dunai.home.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.dunai.home.R;
import com.dunai.home.client.ConnectionState;
import com.dunai.home.client.HomeClient;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private MenuItem menuConnectionStatus;
    private boolean alive;
    private ArrayAdapter<AlertItem> alertAdapter;

    private static class AlertItem {
        public final String title;
        public final int icon;
        public final Class<? extends AppCompatActivity> activityClass;

        public AlertItem(String title, int icon, Class<? extends AppCompatActivity> activityClass) {
            this.title = title;
            this.icon = icon;
            this.activityClass = activityClass;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.alive = true;
        Log.i("HomeApp.MainActivity", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.hide(getSupportFragmentManager().findFragmentById(R.id.fragment_tiles));
        ft.commit();

        HomeClient.getInstance().setConnectionStateChangedListener(this::setConnectionState);
        setConnectionState(HomeClient.getInstance().getConnectionState());
        Log.i("HomeApp", "onCreate");

        this.alertAdapter = new ArrayAdapter<AlertItem>(this, android.R.layout.simple_list_item_1) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                AlertItem item = getItem(position);
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setText(item.title);
                textView.setCompoundDrawablesWithIntrinsicBounds(item.icon, 0, 0, 0);
                int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
                textView.setCompoundDrawablePadding(dp5);
                return view;
            }
        };

        alertAdapter.addAll(Arrays.asList(
                new AlertItem(getString(R.string.w_section), R.drawable.ic_w_section, SectionEditActivity.class),
                new AlertItem(getString(R.string.w_text), R.drawable.ic_w_text, TextWidgetEditActivity.class),
                new AlertItem(getString(R.string.w_switch), R.drawable.ic_w_switch, SwitchWidgetEditActivity.class),
                new AlertItem(getString(R.string.w_graph), R.drawable.ic_w_graph, GraphWidgetEditActivity.class),
                new AlertItem(getString(R.string.w_dropdown), R.drawable.ic_w_dropdown, DropdownWidgetEditActivity.class),
                new AlertItem(getString(R.string.w_color), R.drawable.ic_w_color, ColorWidgetEditActivity.class),
                new AlertItem(getString(R.string.w_button), R.drawable.ic_w_button, ButtonWidgetEditActivity.class),
                new AlertItem(getString(R.string.w_slider), R.drawable.ic_w_slider, SliderWidgetEditActivity.class)
        ));
    }

//    @Override
//    protected void onStart() {
//        Log.i("HomeApp.MainActivity", "onStart");
//        super.onStart();
//    }

    @Override
    protected void onResume() {
        super.onResume();
        this.alive = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.alive = false;
    }

    @Override
    protected void onDestroy() {
        Log.i("HomeApp.MainActivity", "onDestroy");
        HomeClient.getInstance().setConnectionStateChangedListener(null);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menuConnectionStatus = menu.findItem(R.id.action_connection_status);
        setConnectionState(HomeClient.getInstance().getConnectionState());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings || (id == R.id.action_connection_status && HomeClient.getInstance().getConnectionState() == ConnectionState.NO_CONF)) {
            Intent intent = new Intent(this, SettingsActivity.class);
            this.startActivity(intent);
            return true;
        } else if (id == R.id.action_add_item) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.create_new_item);
            builder.setAdapter(alertAdapter, ((dialog, which) -> {
                AlertItem alertItem = alertAdapter.getItem(which);
                Intent intent = new Intent(this, alertItem.activityClass);
                this.startActivity(intent);
            }));
            builder.show();
        } else if (id == R.id.action_reorder_items) {
            Intent intent = new Intent(this, ReorderItemsActivity.class);
            this.startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void setConnectionState(ConnectionState connectionState) {
//        final TextView view = ((TextView) findViewById(R.id.connectionState));
//        MenuItem view = findViewById(R.id.menuConnectionStatus);
        if (!alive) {
            return;
        }
        if (menuConnectionStatus == null) {
            return;
        }
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();

        if (this.isDestroyed()) {
            return;
        }

        try {
            switch (connectionState) {
                case NO_CONF:
                case ERROR:
                    if (connectionState == ConnectionState.NO_CONF) {
                        menuConnectionStatus.setTitle(R.string.status_no_config);
                    } else {
                        menuConnectionStatus.setTitle(R.string.status_error);
                    }
                    ft1.hide(getSupportFragmentManager().findFragmentById(R.id.fragment_tiles));
                    ft1.show(getSupportFragmentManager().findFragmentById(R.id.fragment_not_connected));
                    ft1.commit();
                    break;
                case OFFLINE:
                    menuConnectionStatus.setTitle(R.string.status_offline);
                    ft1.hide(getSupportFragmentManager().findFragmentById(R.id.fragment_tiles));
                    ft1.show(getSupportFragmentManager().findFragmentById(R.id.fragment_not_connected));
                    ft1.commit();
                    break;
                case CONNECTING:
                    menuConnectionStatus.setTitle(R.string.status_connecting);
                    break;
                case CONNECTED:
                    menuConnectionStatus.setTitle(R.string.status_on_air);
                    ft1.hide(getSupportFragmentManager().findFragmentById(R.id.fragment_not_connected));
                    ft1.show(getSupportFragmentManager().findFragmentById(R.id.fragment_tiles));
                    ft1.commit();
                    getSupportActionBar().setTitle(PreferenceManager.getDefaultSharedPreferences(this).getString("host", ""));
                    break;
            }
        } catch (IllegalStateException e) { // Hack for java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
            e.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}