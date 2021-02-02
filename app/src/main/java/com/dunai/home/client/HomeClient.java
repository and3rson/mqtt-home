package com.dunai.home.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.security.KeyChain;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.HandlerCompat;
import androidx.preference.PreferenceManager;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.dunai.home.client.interfaces.ConnectionStateChangedListener;
import com.dunai.home.client.interfaces.DataReceivedListener;
import com.dunai.home.client.interfaces.WorkspaceChangedListener;
import com.dunai.home.client.workspace.Item;
import com.dunai.home.client.workspace.ItemFactory;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.PasswordFinder;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.internal.security.SSLSocketFactoryFactory;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttConnect;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyManagementException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509TrustManager;

public class HomeClient {
    private static HomeClient instance = null; // TODO: Memory leak?
    private ConnectionState connectionState = ConnectionState.OFFLINE;
    private MqttAndroidClient mqttClient;
    private Context context;
    private WorkspaceChangedListener workspaceChangedListener;
    private DataReceivedListener dataReceivedListener;
    private ConnectionStateChangedListener connectionStateChangedListener;
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

//    static SSLSocketFactory getSocketFactory (final String caCrtFile, final String crtFile, final String keyFile, final String password) throws Exception
//    {
//        Security.addProvider(new BouncyCastleProvider());
//
//        JcaX509CertificateConverter conv = new JcaX509CertificateConverter();
//
//        // load CA certificate
//        PEMParser reader = new PEMParser(new InputStreamReader(new ByteArrayInputStream(Files.readAllBytes(Paths.get(caCrtFile)))));
//        X509Certificate caCert = conv.getCertificate((X509CertificateHolder) reader.readObject());
//        reader.close();
//
//        // load client certificate
//        reader = new PEMParser(new InputStreamReader(new ByteArrayInputStream(Files.readAllBytes(Paths.get(crtFile)))));
//        X509Certificate cert = conv.getCertificate((X509CertificateHolder) reader.readObject());
//        reader.close();
//
//        // load client private key
//        reader = new PEMParser(
//                new InputStreamReader(new ByteArrayInputStream(Files.readAllBytes(Paths.get(keyFile))))
//        );
//        PEMKeyPair key = (PEMKeyPair)reader.readObject();
//        reader.close();
//
//        // CA certificate is used to authenticate server
//        KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType());
//        caKs.load(null, null);
//        caKs.setCertificateEntry("ca-certificate", caCert);
//        TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX");
//        tmf.init(caKs);
//
//        // client key and certificates are sent to server so it can authenticate us
//        JcaPEMKeyConverter pemConv = new JcaPEMKeyConverter();
//        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
//        ks.load(null, null);
//        ks.setCertificateEntry("certificate", cert);
////        if (password.length() > 0) {
//            ks.setKeyEntry("private-key", pemConv.getKeyPair(key).getPrivate(), password.toCharArray(), new java.security.cert.Certificate[]{cert});
////        } else {
////            ks.setKeyEntry("private-key", key.getPrivateKeyInfo().getPrivateKey().getOctets(), new java.security.cert.Certificate[]{cert});
////        }
//        KeyManagerFactory kmf = KeyManagerFactory.getInstance("PKIX");
//        kmf.init(ks, password.toCharArray());
//
//        // finally, create SSL socket factory
//        SSLContext context = SSLContext.getInstance("TLSv1.2");
//        context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
//
//        return context.getSocketFactory();
//    }

    private static SSLSocketFactory getSocketFactoryV2(final String caCrtFile,
                                                       final String crtFile, final String keyFile, final String password)
            throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        // load CA certificate
        X509Certificate caCert = null;

        FileInputStream fis = new FileInputStream(caCrtFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        while (bis.available() > 0) {
            caCert = (X509Certificate) cf.generateCertificate(bis);
            // System.out.println(caCert.toString());
        }

        // load client certificate
        bis = new BufferedInputStream(new FileInputStream(crtFile));
        X509Certificate cert = null;
        while (bis.available() > 0) {
            cert = (X509Certificate) cf.generateCertificate(bis);
            // System.out.println(caCert.toString());
        }

        // load client private key
        PEMParser pemParser = new PEMParser(new FileReader(keyFile));
        Object object = pemParser.readObject();
        PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder()
                .build(password.toCharArray());
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
//                .setProvider("BC");
        KeyPair key;
        if (object instanceof PEMEncryptedKeyPair) {
            System.out.println("Encrypted key - we will use provided password");
            key = converter.getKeyPair(((PEMEncryptedKeyPair) object)
                    .decryptKeyPair(decProv));
        } else {
            System.out.println("Unencrypted key - no password needed");
            key = converter.getKeyPair((PEMKeyPair) object);
        }
        pemParser.close();

        // CA certificate is used to authenticate server
        KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType());
        caKs.load(null, null);
        caKs.setCertificateEntry("ca-certificate", caCert);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
        tmf.init(caKs);

        // client key and certificates are sent to server so it can authenticate
        // us
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        ks.setCertificateEntry("certificate", cert);
        ks.setKeyEntry("private-key", key.getPrivate(), password.toCharArray(),
                new java.security.cert.Certificate[] { cert });
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory
                .getDefaultAlgorithm());
        kmf.init(ks, password.toCharArray());

        // finally, create SSL socket factory
        SSLContext context = SSLContext.getInstance("TLSv1.2");
        context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        return context.getSocketFactory();
    }

    private SSLSocketFactory getSocketFactoryV3(String keyAlias, boolean validateCerts) throws CertificateException, NoSuchAlgorithmException, KeyManagementException {
        //Configure trustManager if needed
//        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        TrustManager[] trustManagers = {};
//        if (validateCerts) {
//             TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//             factory.init(KeyStore.getInstance(KeyStore.getDefaultType()));
//             trustManagers = factory.getTrustManagers();
//        } else {
        if (!validateCerts) {
            trustManagers = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };
        }

        //Configure keyManager to select the private key and the certificate chain from KeyChain
        KeyManager keyManager = null;
        keyManager = KeyChainKeyManager.fromAlias(context, keyAlias);

        //Configure SSLContext
        SSLContext sslContext = null;
        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(new KeyManager[] {keyManager}, trustManagers, null);

        return sslContext.getSocketFactory();
    }

    public interface MqttConnectOptionsResultHandler {
        void onResult(@Nullable MqttConnectOptions opts);
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
                    opts.setSocketFactory(getSocketFactoryV3(prefs.getString("clientCert", ""), prefs.getBoolean("validateCerts", true)));
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
//        if (useSSL) {
//            try {
//                opts.setSocketFactory(getSocketFactoryV3(prefs.getString("clientCert", "")));
//            } catch (Exception e) {
//                e.printStackTrace();
//                Toast.makeText(context, "Failed to configure SSL: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                return null;
//            }
//        }

//            Properties sslProps = new Properties();
//            sslProps.put(SSLSocketFactoryFactory.TRUSTSTORE, prefs.getString("caCert", ""));
////            sslProps.put(SSLSocketFactoryFactory.TRUSTSTOREPWD, "");
//            opts.setSSLProperties(sslProps);
//        }
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
                        try {
                            HomeClient.this.mqttClient.subscribe("#", 0);
                        } catch (MqttException e) {
                            Log.e("HomeApp", "Failed to subscribe to MQTT topics");
                            e.printStackTrace();
                            Toast.makeText(context, "Failed to subscribe to #: " + e.toString(), Toast.LENGTH_LONG).show();
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
                                Workspace workspace = new Workspace();
                                JSONObject root = null;
                                root = new JSONObject(new String(message.getPayload()));
                                JSONArray items = root.getJSONArray("items");
                                Log.i("HomeApp", "Tiles: " + items.length());
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

    void setConnectionState(ConnectionState connectionState) {
        System.out.println("Conn state -> " + connectionState.name());
        this.connectionState = connectionState;
        if (this.connectionStateChangedListener != null) {
            this.connectionStateChangedListener.onConnectionStateChanged(connectionState);
        }
    }
}
