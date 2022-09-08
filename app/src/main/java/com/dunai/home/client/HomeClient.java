package com.dunai.home.client;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.os.HandlerCompat;
import androidx.preference.PreferenceManager;

import com.dunai.home.client.interfaces.ConnectionStateChangedListener;
import com.dunai.home.client.interfaces.DataReceivedListener;
import com.dunai.home.client.interfaces.WorkspaceChangedListener;
import com.dunai.home.client.workspace.Item;
import com.dunai.home.client.workspace.ItemFactory;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HomeClient {
    @SuppressLint("StaticFieldLeak")
    private static HomeClient instance = null; // TODO: Memory leak?
    private ConnectionState connectionState = ConnectionState.OFFLINE;
    private MqttAndroidClient mqttClient;
    private Context context;
    private WorkspaceChangedListener workspaceChangedListener;
    private DataReceivedListener dataReceivedListener;
    private ConnectionStateChangedListener connectionStateChangedListener;
    private Timer reconnectTimer;
    private Workspace workspace = new Workspace();

    private HomeClient() {
    }

    public static HomeClient getInstance() {
        if (HomeClient.instance == null) {
            HomeClient.instance = new HomeClient();
        }
        return HomeClient.instance;
    }

    public Workspace getWorkspace() {
        return this.workspace;
    }

    private void getConnectionOptions(MqttConnectOptionsResultHandler mqttConnectOptionsResultHandler) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.context);
        final MqttConnectOptions opts = new MqttConnectOptions();
        boolean useSSL = prefs.getBoolean("useSSL", false);
        Executor executor = Executors.newSingleThreadExecutor();
        Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());
        executor.execute(() -> {
            try {
                if (useSSL) {
                    System.out.println("EXEC: get factory...");
                    opts.setSocketFactory(HomeSocketFactory.getSocketFactoryV3(context, prefs.getString("clientCert", ""), prefs.getBoolean("validateCerts", true)));
                    System.out.println("EXEC: got factory!");
                }
                mainThreadHandler.post(() -> {
                    opts.setKeepAliveInterval(Integer.parseInt(prefs.getString("keepAliveInterval", "10")));
                    opts.setAutomaticReconnect(true);
                    opts.setCleanSession(true);
                    opts.setConnectionTimeout(Integer.parseInt(prefs.getString("connectionTimeout", "5")));
                    String[] uris = new String[1];
                    if (prefs.getString("host", "").isEmpty()) {
                        mqttConnectOptionsResultHandler.onResult(null);
                    }
                    uris[0] = (useSSL ? "ssl" : "tcp") + "://" + prefs.getString("host", "127.0.0.1") + ":" + prefs.getString("port", "1883");
                    opts.setServerURIs(uris);
                    String username = prefs.getString("username", "");
                    String password = prefs.getString("password", "");
                    if (!username.isEmpty()) {
                        opts.setUserName(username);
                    }
                    if (!password.isEmpty()) {
                        opts.setPassword(password.toCharArray());
                    }
                    mqttConnectOptionsResultHandler.onResult(opts);
                });
            } catch (CertificateException | NoSuchAlgorithmException | KeyManagementException e) {
                e.printStackTrace();
                mainThreadHandler.post(() -> {
                    Toast.makeText(context, "Failed to load certificate: " + e.toString(), Toast.LENGTH_SHORT).show();
                    mqttConnectOptionsResultHandler.onResult(null);
                });
            }
        });
    }

    public void connect() {
        if (this.reconnectTimer == null) {
            this.reconnectTimer = new Timer();
            this.reconnectTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (connectionState == ConnectionState.OFFLINE || connectionState == ConnectionState.ERROR) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(() -> connect());
                    }
                }
            }, 0, 5000);
        }
        if (this.connectionState == ConnectionState.CONNECTING || this.connectionState == ConnectionState.CONNECTED) {
            return;
        }
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.context);
        if (prefs.getString("host", "").isEmpty()) {
            this.setConnectionState(ConnectionState.NO_CONF);
            return;
        }
        this.setConnectionState(ConnectionState.CONNECTING);
        this.getConnectionOptions(opts -> {
            if (opts == null) {
                this.setConnectionState(ConnectionState.ERROR);
                return;
            }
            String clientId = prefs.getString("clientId", "automatic");
            if (clientId.equals("automatic")) {
                clientId = "homeclient-" + Math.round(Math.random() * 1e6);
            }
            this.mqttClient = new MqttAndroidClient(this.context, opts.getServerURIs()[0], clientId);
            try {
                this.mqttClient.connect(opts).setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        setConnectionState(ConnectionState.CONNECTED);
                        String workspaceTopic = prefs.getString("workspaceTopic", "workspace");
                        try {
//                            HomeClient.this.mqttClient.subscribe("#", 0);
                            HomeClient.this.mqttClient.subscribe(workspaceTopic, 0);
                        } catch (MqttException e) {
                            Log.e("HomeApp", "Failed to subscribe to MQTT workspace topic");
                            e.printStackTrace();
                            Toast.makeText(context, String.format("Failed to subscribe to workspace topic %s: %s", workspaceTopic, e.toString()), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        exception.printStackTrace();
                        Toast.makeText(context, "Failed to connect: " + exception.toString(), Toast.LENGTH_LONG).show();
                        setConnectionState(ConnectionState.OFFLINE);
                    }
                });
            } catch (MqttException e) {
                Log.e("HomeApp", "Failed to connect to MQTT");
                e.printStackTrace();
                Toast.makeText(context, "Failed to connect: " + e.toString(), Toast.LENGTH_LONG).show();
            }
            this.mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    setConnectionState(ConnectionState.OFFLINE);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    if (topic.equals("workspace")) {
                        if (HomeClient.this.workspaceChangedListener != null) {
                            try {
                                // Remember old topics
                                Set<String> oldTopics = new HashSet<>();
                                for (int i = 0; i < workspace.items.size(); i++) {
                                    String itemTopic = workspace.items.get(i).topic;
                                    if (itemTopic != null) {
                                        oldTopics.add(itemTopic);
                                    }
                                }

                                // Create new workspace
                                Workspace workspace = new Workspace();
                                JSONObject root;
                                root = new JSONObject(new String(message.getPayload()));
                                JSONArray items = root.getJSONArray("items");
                                Log.i("HomeApp", "Tiles: " + items.length());

                                // Populate new items & topics
                                Set<String> newTopics = new HashSet<>();
                                int[] qos = new int[items.length()];
                                for (int i = 0; i < items.length(); i++) {
                                    JSONObject item = items.getJSONObject(i);
                                    Item workspaceItem = ItemFactory.createFromJSONObject(item);
                                    workspace.items.add(workspaceItem);
                                    if (workspaceItem.topic != null) {
                                        newTopics.add(workspaceItem.topic);
                                    }
                                    qos[i] = 0;
                                }

                                oldTopics.removeAll(newTopics);
                                newTopics.removeAll(oldTopics);

                                // Unsubscribe from old topics
                                if (oldTopics.size() > 0) {
                                    try {
                                        HomeClient.this.mqttClient.unsubscribe(oldTopics.toArray(new String[0]));
                                        Log.i("HomeApp", "Unsubscribed from " + TextUtils.join(", ", oldTopics.toArray(new String[0])));
                                    } catch (MqttException e) {
                                        Log.e("HomeApp", "Failed to subscribe to MQTT topics");
                                        e.printStackTrace();
                                        Toast.makeText(context, String.format("Failed to unsubscribe from %s: %s", TextUtils.join(", ", oldTopics.toArray(new String[0])), e.toString()), Toast.LENGTH_LONG).show();
                                    }
                                }
                                // Subscribe to new topics
                                if (newTopics.size() > 0) {
                                    try {
                                        HomeClient.this.mqttClient.subscribe(newTopics.toArray(new String[0]), qos);
                                        Log.i("HomeApp", "Subscribed to " + TextUtils.join(", ", newTopics.toArray(new String[0])));
                                    } catch (MqttException e) {
                                        Log.e("HomeApp", "Failed to subscribe to MQTT topics");
                                        e.printStackTrace();
                                        Toast.makeText(context, String.format("Failed to subscribe to %s: %s", TextUtils.join(", ", newTopics.toArray(new String[0])), e.toString()), Toast.LENGTH_LONG).show();
                                    }
                                }
                                HomeClient.this.workspace = workspace;
                                workspaceChangedListener.onWorkspaceChanged(workspace);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(context, "Failed to parse workspace: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        if (HomeClient.this.dataReceivedListener != null) {
                            HomeClient.this.dataReceivedListener.onDataReceived(topic, new String(message.getPayload()));
                        }
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
        });
    }

    public void disconnect() {
        if (this.reconnectTimer != null) {
            this.reconnectTimer.cancel();
            this.reconnectTimer = null;
        }
        try {
            if (this.mqttClient != null && this.mqttClient.isConnected()) {
                this.mqttClient.disconnect();
            }
        } catch (MqttException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to disconnect: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void reconnect() {
        try {
            if (this.mqttClient != null && this.mqttClient.isConnected()) {
                this.mqttClient.disconnect().setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        HomeClient.this.connect();
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.e("HomeApp", "Failed to disconnect from MQTT");
                        Toast.makeText(context, "Failed to disconnect: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                HomeClient.this.connect();
            }
        } catch (MqttException e) {
            Log.e("HomeApp", "Failed to disconnect from MQTT");
            e.printStackTrace();
            Toast.makeText(context, "Failed to disconnect: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void setWorkspaceChangedCallback(WorkspaceChangedListener listener) {
        this.workspaceChangedListener = listener;
    }

    public void setDataReceivedListener(DataReceivedListener listener) {
        this.dataReceivedListener = listener;
    }

    public void setConnectionStateChangedListener(ConnectionStateChangedListener listener) {
        this.connectionStateChangedListener = listener;
    }

    public void publishWorkspace(Workspace newWorkspace) {
        try {
            this.mqttClient.publish("workspace", newWorkspace.serialize().toString(4).getBytes(), 0, true);
        } catch (MqttException | JSONException e) {
            Toast.makeText(context, "Failed to save workspace: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void moveBack(String id) {
        int index = this.workspace.findItem(id);
        if (index == -1) {
            Toast.makeText(context, "Item with ID " + id + " not found", Toast.LENGTH_SHORT).show();
            return;
        }
        if (index == 0) {
            return;
        }
        this.publishWorkspace(this.workspace.swapItems(index - 1, index));
    }

    public void moveForth(String id) {
        int index = this.workspace.findItem(id);
        if (index == -1) {
            Toast.makeText(context, "Item with ID " + id + " not found", Toast.LENGTH_SHORT).show();
            return;
        }
        if (index == this.workspace.items.size() - 1) {
            return;
        }
        this.publishWorkspace(this.workspace.swapItems(index + 1, index));
    }

    public void addItem(Item item) {
        this.publishWorkspace(this.workspace.addItem(item));
    }

    public void updateItem(String id, Item item) {
        Workspace newWorkspace = this.workspace.updateItem(id, item);
        if (newWorkspace == null) {
            Toast.makeText(context, "Item with ID " + id + " not found", Toast.LENGTH_SHORT).show();
            return;
        }
        this.publishWorkspace(newWorkspace);
    }

    public void deleteItem(String id) {
        int index = this.workspace.findItem(id);
        if (index == -1) {
            Toast.makeText(context, "Item with ID " + id + " not found", Toast.LENGTH_SHORT).show();
            return;
        }
        this.publishWorkspace(this.workspace.deleteItem(index));
    }

    public Item getItem(String id) {
        int index = this.workspace.findItem(id);
        if (index == -1) {
            Toast.makeText(context, "Item with ID " + id + " not found", Toast.LENGTH_SHORT).show();
            return null;
        }
        return this.workspace.items.get(index);
    }

    public void publish(String topic, String value, boolean retain) {
        try {
            this.mqttClient.publish(topic, value.getBytes(), 0, retain);
        } catch (MqttException e) {
            Toast.makeText(context, "Failed to publish: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public ConnectionState getConnectionState() {
        return connectionState;
    }

    void setConnectionState(ConnectionState connectionState) {
        this.connectionState = connectionState;
        if (this.connectionStateChangedListener != null) {
            this.connectionStateChangedListener.onConnectionStateChanged(connectionState);
        }
    }

    public interface MqttConnectOptionsResultHandler {
        void onResult(@Nullable MqttConnectOptions opts);
    }
}
