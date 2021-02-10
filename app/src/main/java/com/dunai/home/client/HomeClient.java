package com.dunai.home.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
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

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HomeClient {
    private static HomeClient instance = null; // TODO: Memory leak?
    private MqttAndroidClient mqttClient;
    private Context context;
    private WorkspaceChangedListener workspaceChangedListener;
    private DataReceivedListener dataReceivedListener;
    private ConnectionStateChangedListener connectionStateChangedListener;

    public ConnectionState connectionState = ConnectionState.OFFLINE;
    private Timer reconnectTimer;
    private Workspace workspace;

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

    private @Nullable MqttConnectOptions getConnectionOptions() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.context);
        MqttConnectOptions opts = new MqttConnectOptions();
        opts.setKeepAliveInterval(Integer.parseInt(prefs.getString("keepAliveInterval", "10")));
        opts.setAutomaticReconnect(true);
        opts.setCleanSession(true);
        opts.setConnectionTimeout(Integer.parseInt(prefs.getString("connectionTimeout", "5")));
        String[] uris = new String[1];
        if (prefs.getString("host", "").isEmpty()) {
            return null;
        }
        uris[0] = "tcp://" + prefs.getString("host", "127.0.0.1") + ":" + prefs.getString("port", "1883");
        opts.setServerURIs(uris);
        String username = prefs.getString("username", "");
        String password = prefs.getString("password", "");
        if (!username.isEmpty()) {
            opts.setUserName(username);
        }
        if (!password.isEmpty()) {
            opts.setPassword(password.toCharArray());
        }
        return opts;
    }
    public void connect() {
        if (this.reconnectTimer == null) {
            this.reconnectTimer = new Timer();
            this.reconnectTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (connectionState == ConnectionState.OFFLINE) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(() -> connect());
                    }
                }
            }, 0, 3000);
        }
        MqttConnectOptions opts = this.getConnectionOptions();
        if (opts == null) {
            this.setConnectionState(ConnectionState.NO_CONF);
            return;
        }
        this.setConnectionState(ConnectionState.CONNECTING);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.context);
        String clientId = prefs.getString("clientId", "automatic");
        if (clientId.equals("automatic")) {
            clientId = "homeclient-" + Math.round(Math.random() * 1e6);
        }
        this.mqttClient = new MqttAndroidClient(this.context, opts.getServerURIs()[0], clientId);
        try {
            this.mqttClient.connect(this.getConnectionOptions()).setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    setConnectionState(ConnectionState.CONNECTED);
                    try {
                        HomeClient.this.mqttClient.subscribe("#", 0);
                    } catch (MqttException e) {
                        Log.e("HomeApp", "Failed to subscribe to MQTT topics");
                        e.printStackTrace();
                        Toast.makeText(context, "Failed to subscribe to #: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    System.out.println("Offline");
                    setConnectionState(ConnectionState.OFFLINE);
                }
            });
        } catch (MqttException e) {
            Log.e("HomeApp", "Failed to connect to MQTT");
            e.printStackTrace();
            Toast.makeText(context, "Failed to connect: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
                            Workspace workspace = new Workspace();
                            JSONObject root = null;
                            root = new JSONObject(new String(message.getPayload()));
                            JSONArray items = root.getJSONArray("items");
                            Log.i("HomeApp", "Tiles: " + String.valueOf(items.length()));
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject item = items.getJSONObject(i);
                                Item workspaceItem = ItemFactory.createFromJSONObject(item);
                                workspace.items.add(workspaceItem);
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
    }

    public void disconnect() {
        this.reconnectTimer.cancel();
        this.reconnectTimer = null;
        try {
            if (this.mqttClient.isConnected()) {
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

    void setConnectionState(ConnectionState connectionState) {
        this.connectionState = connectionState;
        if (this.connectionStateChangedListener != null) {
            this.connectionStateChangedListener.onConnectionStateChanged(connectionState);
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

    private int findItem(String itemId) {
        for (int i = 0; i < this.workspace.items.size(); i++) {
            if (this.workspace.items.get(i).id.equals(itemId)) {
                return i;
            }
        }
        return -1;
    }

    public void moveBack(String id) {
        int index = this.findItem(id);
        if (index == -1) {
            Toast.makeText(context, "Item with ID " + id + " not found", Toast.LENGTH_SHORT).show();
            return;
        }
        if (index == 0) {
            return;
        }
        Workspace newWorkspace = new Workspace();
        newWorkspace.items = (ArrayList<Item>) this.workspace.items.clone();
        Item tmp = newWorkspace.items.get(index - 1);
        newWorkspace.items.set(index - 1, newWorkspace.items.get(index));
        newWorkspace.items.set(index, tmp);
        this.publishWorkspace(newWorkspace);
    }

    public void moveForth(String id) {
        int index = this.findItem(id);
        if (index == -1) {
            Toast.makeText(context, "Item with ID " + id + " not found", Toast.LENGTH_SHORT).show();
            return;
        }
        if (index == this.workspace.items.size() - 1) {
            return;
        }
        Workspace newWorkspace = new Workspace();
        newWorkspace.items = (ArrayList<Item>) this.workspace.items.clone();
        Item tmp = newWorkspace.items.get(index + 1);
        newWorkspace.items.set(index + 1, newWorkspace.items.get(index));
        newWorkspace.items.set(index, tmp);
        this.publishWorkspace(newWorkspace);
    }

    public void createItem(Item item) {
        Workspace newWorkspace = new Workspace();
        newWorkspace.items = (ArrayList<Item>) this.workspace.items.clone();
        newWorkspace.items.add(item);
        this.publishWorkspace(newWorkspace);
    }

    public void updateItem(String id, Item item) {
        Workspace newWorkspace = new Workspace();
        newWorkspace.items = (ArrayList<Item>) this.workspace.items.clone();
        int index = this.findItem(id);
        if (index == -1) {
            Toast.makeText(context, "Item with ID " + id + " not found", Toast.LENGTH_SHORT).show();
            return;
        }
        newWorkspace.items.set(index, item);
        this.publishWorkspace(newWorkspace);
    }

    public void deleteItem(String id) {
        Workspace newWorkspace = new Workspace();
        newWorkspace.items = (ArrayList<Item>) this.workspace.items.clone();
        int index = this.findItem(id);
        if (index == -1) {
            Toast.makeText(context, "Item with ID " + id + " not found", Toast.LENGTH_SHORT).show();
            return;
        }
        newWorkspace.items.remove(index);
        this.publishWorkspace(newWorkspace);
    }

    public Item getItem(String id) {
        int index = this.findItem(id);
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
}
