package com.dunai.home.activities;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.security.KeyChain;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.dunai.home.R;
import com.dunai.home.client.HomeClient;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SettingsActivity extends AppCompatActivity {
    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        settingsFragment = new SettingsFragment();
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, settingsFragment)
                    .commit();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    public void onStop() {
        if (this.settingsFragment.settingsChanged) {
            HomeClient.getInstance().reconnect();
        }
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode >= 10001 && requestCode <= 10003 && resultCode == RESULT_OK) {
            ArrayList<String> filePaths = data.getStringArrayListExtra("filePaths");
            if (filePaths.size() > 0) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                switch (requestCode) {
                    case 10001:
                        editor.putString("caCert", filePaths.get(0));
                        break;
                    case 10002:
                        editor.putString("clientCert", filePaths.get(0));
                        break;
                    case 10003:
                        editor.putString("clientKey", filePaths.get(0));
                        break;
                }
                editor.apply();

                settingsFragment.refreshSummaries();
            }
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
        public boolean settingsChanged = false;
        private int clicks = 0;

//        private boolean checkPermissions() {
//            int permissionCheck1 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
//            int permissionCheck2 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            if (permissionCheck1 != PackageManager.PERMISSION_GRANTED || permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1337);
//                return false;
//            }
//            return true;
//        }

//        private UnicornFilePicker getUnicornFilePickerConfigBuilder() {
//            return UnicornFilePicker.from(getActivity())
//                    .addConfigBuilder()
//                    .selectMultipleFiles(false)
//                    .setRootDirectory(Environment.getExternalStorageDirectory().getAbsolutePath())
//                    .showHiddenFiles(false)
////                    .setFilters(new String[]{"pdf", "png", "jpg", "jpeg"})
//                    .addItemDivider(true)
//                    .theme(R.style.UnicornFilePicker_Dracula)
//                    .build();
//        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

            refreshSummaries();

            findPreference("clientCert").setOnPreferenceClickListener(preference -> {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                String clientCert = prefs.getString("clientCert", "");
                KeyChain.choosePrivateKeyAlias(getActivity(), alias -> {
                    System.out.println("Alias: " + alias + ", preselected: " + clientCert);
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                    if (alias != null) {
                        editor.putString("clientCert", alias);
                    } else {
                        editor.putString("clientCert", "");
                    }
                    editor.apply();
                    refreshSummaries();
                }, new String[]{"RSA", "DSA"}, null, null, -1, clientCert);
                return true;
            });

            findPreference("shareServerConfig").setOnPreferenceClickListener(preference -> {
                new AlertDialog.Builder(getContext())
                        .setTitle("Create server configuration URI")
                        .setMessage("Warning: copied URI will contain unencrypted server credentials!\n\nAnyone who receives it will gain access to your MQTT server.\n\nMake sure to share it only with your trusted friends!\n\nNote: this app DOES NOT send this link anywhere. It is up to you if you want to give it to someone.")
                        .setPositiveButton("Copy link to clipboard", (dialog, which) -> {
                            Uri.Builder builder = new Uri.Builder();
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                            Uri uri = builder
                                    .scheme("mqtthome")
                                    .authority("autoconf")
                                    .appendQueryParameter("host", prefs.getString("host", ""))
                                    .appendQueryParameter("port", prefs.getString("port", "1883"))
                                    .appendQueryParameter("username", prefs.getString("username", ""))
                                    .appendQueryParameter("password", prefs.getString("password", ""))
                                    .build();
                            ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                            clipboardManager.setPrimaryClip(ClipData.newPlainText("MQTT Home URI", uri.toString()));
                            Toast.makeText(getContext(), "Link copied to clipboard!", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();

                return true;
            });

            PackageInfo info;
            try {
                info = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), PackageManager.GET_ACTIVITIES);
                Preference appVersion = findPreference("appVersion");
                appVersion.setSummary(info.versionName);
                appVersion.setOnPreferenceClickListener(preference -> {
                    clicks++;
                    if (clicks == 5) {
                        clicks = 0;
                        Intent rollIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=dQw4w9WgXcQ"));
                        startActivity(rollIntent);
                    }
                    return true;
                });
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    clicks = 0;
                }
            }, 0, 1000);

//            findPreference("caCert").setOnPreferenceClickListener(preference -> {
//                if (checkPermissions()) {
//                    getUnicornFilePickerConfigBuilder().forResult(10001);
//                }
//                return true;
//            });
//            findPreference("clientCert").setOnPreferenceClickListener(preference -> {
//                if (checkPermissions()) {
//                    getUnicornFilePickerConfigBuilder().forResult(10002);
//                }
//                return true;
//            });
//            findPreference("clientKey").setOnPreferenceClickListener(preference -> {
//                if (checkPermissions()) {
//                    getUnicornFilePickerConfigBuilder().forResult(10003);
//                }
//                return true;
//            });
        }

        private void refreshSummaries() {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            findPreference("clientCert").setSummary("     " + prefs.getString("clientCert", ""));
            findPreference("sslPassword").setSummary("     " + prefs.getString("sslPassword", "").replaceAll("(.)", "*"));
//            if (prefs.getString("caCert", "").length() > 0) {
//                findPreference("caCert").setSummary(prefs.getString("caCert", ""));
//            }
//            if (prefs.getString("clientCert", "").length() > 0) {
//                findPreference("clientCert").setSummary(prefs.getString("clientCert", ""));
//            }
//            if (prefs.getString("clientKey", "").length() > 0) {
//                findPreference("clientKey").setSummary(prefs.getString("clientKey", ""));
//            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            this.settingsChanged = true;
        }
    }
}
