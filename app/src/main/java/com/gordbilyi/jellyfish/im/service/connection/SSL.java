package com.gordbilyi.jellyfish.im.service.connection;

import android.util.Log;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by gordbilyi on 5/10/16.
 */
public class SSL {

    private static final String TAG = "SSL";

    /**
     * Temporary solution, no encryption
     * @return ssl context to be used in xmpp connection config
     */
    public static SSLContext getSSLContext() {
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("TLS");
            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s)
                        throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] x509Certificates, String s)
                        throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };
            sc.init(null, new TrustManager[]{tm}, null);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return sc;
    }
}
