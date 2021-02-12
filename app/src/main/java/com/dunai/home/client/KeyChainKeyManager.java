package com.dunai.home.client;

import android.content.Context;
import android.security.KeyChain;
import android.security.KeyChainException;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509ExtendedKeyManager;

public class KeyChainKeyManager extends X509ExtendedKeyManager {
    private final String mClientAlias;
    private final X509Certificate[] mCertificateChain;
    private final PrivateKey mPrivateKey;

    /**
     * Builds an instance of a KeyChainKeyManager using the given certificate alias.
     * If for any reason retrieval of the credentials from the system {@link android.security.KeyChain} fails,
     * a {@code null} value will be returned.
     */
    public static KeyChainKeyManager fromAlias(Context context, String alias)
            throws CertificateException {
        X509Certificate[] certificateChain;
        try {
            certificateChain = KeyChain.getCertificateChain(context, alias);
        } catch (KeyChainException e) {
            throw new CertificateException(e);
        } catch (InterruptedException e) {
            throw new CertificateException(e);
        }

        PrivateKey privateKey;
        try {
            privateKey = KeyChain.getPrivateKey(context, alias);
        } catch (KeyChainException e) {
            throw new CertificateException(e);
        } catch (InterruptedException e) {
            throw new CertificateException(e);
        }

        if (certificateChain == null || privateKey == null) {
            throw new CertificateException("Can't access certificate from keystore");
        }

        return new KeyChainKeyManager(alias, certificateChain, privateKey);
    }

    private KeyChainKeyManager(
            String clientAlias, X509Certificate[] certificateChain, PrivateKey privateKey) {
        mClientAlias = clientAlias;
        mCertificateChain = certificateChain;
        mPrivateKey = privateKey;
    }

    @Override
    public String chooseClientAlias(String[] keyTypes, Principal[] issuers, Socket socket) {
        return mClientAlias;
    }

    @Override
    public X509Certificate[] getCertificateChain(String alias) {
        return mCertificateChain;
    }

    @Override
    public PrivateKey getPrivateKey(String alias) {
        return mPrivateKey;
    }

    @Override
    public final String chooseServerAlias( String keyType, Principal[] issuers, Socket socket) {
        // not a client SSLSocket callback
        throw new UnsupportedOperationException();
    }

    @Override
    public final String[] getClientAliases(String keyType, Principal[] issuers) {
        // not a client SSLSocket callback
        throw new UnsupportedOperationException();
    }

    @Override
    public final String[] getServerAliases(String keyType, Principal[] issuers) {
        // not a client SSLSocket callback
        throw new UnsupportedOperationException();
    }
}
