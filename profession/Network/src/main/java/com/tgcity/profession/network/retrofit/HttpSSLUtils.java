package com.tgcity.profession.network.retrofit;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * @author TGCity
 */
public class HttpSSLUtils {

    public static class SSLParams {
        public SSLSocketFactory sSLSocketFactory;
        public X509TrustManager trustManager;
    }

    /**
     * 获取SSLParams
     *
     * @return SSLParams
     */
    public static SSLParams getSslSocketFactory() {
        SSLParams sslParams = new SSLParams();
        try {
            TrustManager[] trustManagers = new TrustManager[]{new UnSafeTrustManager()};

            KeyManager[] keyManagers = prepareKeyManager(null, null);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            X509TrustManager trustManager;
            trustManager = new MyTrustManager(chooseTrustManager(trustManagers));
            sslContext.init(keyManagers, new TrustManager[]{trustManager}, null);
            sslParams.sSLSocketFactory = sslContext.getSocketFactory();
            sslParams.trustManager = trustManager;
            return sslParams;
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        } catch (KeyManagementException e) {
            throw new AssertionError(e);
        } catch (KeyStoreException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * 获取HttpSSLUtils
     *
     * @return UnSafeHostnameVerifier
     */
    public static HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
    }

    private static class UnSafeTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] certs, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateExpiredException, CertificateNotYetValidException {
            if (certs != null && certs.length > 0) {
                certs[0].checkValidity();
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            X509Certificate[] trustedCerts = new X509Certificate[1];
            try {
                InputStream inStream = new ByteArrayInputStream(new String(XIETONG_CERT).getBytes(Charset.forName("UTF-8")));
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                X509Certificate cert = (X509Certificate) cf.generateCertificate(inStream);
                inStream.close();
                trustedCerts[0] = cert;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return trustedCerts;
        }
    }

    private static KeyManager[] prepareKeyManager(InputStream bksFile, String password) {
        try {
            if (bksFile == null || password == null) {
                return null;
            }

            KeyStore clientKeyStore = KeyStore.getInstance("BKS");
            clientKeyStore.load(bksFile, password.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientKeyStore, password.toCharArray());
            return keyManagerFactory.getKeyManagers();

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static X509TrustManager chooseTrustManager(TrustManager[] trustManagers) {
        for (TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }
        return null;
    }


    private static class MyTrustManager implements X509TrustManager {
        private X509TrustManager defaultTrustManager;
        private X509TrustManager localTrustManager;

        public MyTrustManager(X509TrustManager localTrustManager) throws NoSuchAlgorithmException, KeyStoreException {
            TrustManagerFactory var4 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            var4.init((KeyStore) null);
            defaultTrustManager = chooseTrustManager(var4.getTrustManagers());
            this.localTrustManager = localTrustManager;
        }


        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//            try {
//                defaultTrustManager.checkServerTrusted(chain, authType);
//            } catch (CertificateException ce)
//            {
//                localTrustManager.checkServerTrusted(chain, authType);
//            }
        }


        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static final char[] XIETONG_CERT = {
            0x30, 0x82, 0x06, 0xE5, 0x30, 0x82, 0x04, 0xCD, 0xA0, 0x03, 0x02, 0x01, 0x02, 0x02, 0x09, 0x00,
            0xA1, 0x66, 0xE8, 0x4B, 0x28, 0xEA, 0xEC, 0xB7, 0x30, 0x0D, 0x06, 0x09, 0x2A, 0x86, 0x48, 0x86,
            0xF7, 0x0D, 0x01, 0x01, 0x0B, 0x05, 0x00, 0x30, 0x81, 0xE9, 0x31, 0x0B, 0x30, 0x09, 0x06, 0x03,
            0x55, 0x04, 0x06, 0x13, 0x02, 0x43, 0x4E, 0x31, 0x39, 0x30, 0x37, 0x06, 0x03, 0x55, 0x04, 0x0A,
            0x0C, 0x30, 0x53, 0x68, 0x61, 0x6E, 0x67, 0x68, 0x61, 0x69, 0x20, 0x58, 0x69, 0x65, 0x54, 0x6F,
            0x6E, 0x67, 0x20, 0x49, 0x6E, 0x66, 0x6F, 0x72, 0x6D, 0x61, 0x74, 0x69, 0x6F, 0x6E, 0x20, 0x54,
            0x65, 0x63, 0x68, 0x6E, 0x6F, 0x6C, 0x6F, 0x67, 0x79, 0x20, 0x43, 0x6F, 0x2E, 0x2C, 0x20, 0x4C,
            0x74, 0x64, 0x31, 0x35, 0x30, 0x33, 0x06, 0x03, 0x55, 0x04, 0x0B, 0x0C, 0x2C, 0x58, 0x69, 0x65,
            0x54, 0x6F, 0x6E, 0x67, 0x20, 0x49, 0x6E, 0x66, 0x6F, 0x72, 0x6D, 0x61, 0x74, 0x69, 0x6F, 0x6E,
            0x20, 0x54, 0x65, 0x63, 0x68, 0x6E, 0x6F, 0x6C, 0x6F, 0x67, 0x79, 0x20, 0x53, 0x65, 0x63, 0x75,
            0x72, 0x69, 0x74, 0x79, 0x20, 0x54, 0x65, 0x61, 0x6D, 0x31, 0x42, 0x30, 0x40, 0x06, 0x03, 0x55,
            0x04, 0x03, 0x0C, 0x39, 0x58, 0x69, 0x65, 0x54, 0x6F, 0x6E, 0x67, 0x20, 0x49, 0x6E, 0x66, 0x6F,
            0x72, 0x6D, 0x61, 0x74, 0x69, 0x6F, 0x6E, 0x20, 0x54, 0x65, 0x63, 0x68, 0x6E, 0x6F, 0x6C, 0x6F,
            0x67, 0x79, 0x20, 0x52, 0x6F, 0x6F, 0x74, 0x20, 0x43, 0x65, 0x72, 0x74, 0x69, 0x66, 0x69, 0x63,
            0x61, 0x74, 0x65, 0x20, 0x41, 0x75, 0x74, 0x68, 0x6F, 0x72, 0x69, 0x74, 0x79, 0x31, 0x24, 0x30,
            0x22, 0x06, 0x09, 0x2A, 0x86, 0x48, 0x86, 0xF7, 0x0D, 0x01, 0x09, 0x01, 0x16, 0x15, 0x73, 0x75,
            0x70, 0x70, 0x6F, 0x72, 0x74, 0x40, 0x73, 0x68, 0x61, 0x72, 0x65, 0x74, 0x6F, 0x6D, 0x65, 0x2E,
            0x63, 0x6F, 0x6D, 0x30, 0x20, 0x17, 0x0D, 0x31, 0x37, 0x30, 0x35, 0x31, 0x36, 0x30, 0x37, 0x31,
            0x39, 0x34, 0x32, 0x5A, 0x18, 0x0F, 0x32, 0x31, 0x31, 0x37, 0x30, 0x34, 0x32, 0x32, 0x30, 0x37,
            0x31, 0x39, 0x34, 0x32, 0x5A, 0x30, 0x81, 0xE9, 0x31, 0x0B, 0x30, 0x09, 0x06, 0x03, 0x55, 0x04,
            0x06, 0x13, 0x02, 0x43, 0x4E, 0x31, 0x39, 0x30, 0x37, 0x06, 0x03, 0x55, 0x04, 0x0A, 0x0C, 0x30,
            0x53, 0x68, 0x61, 0x6E, 0x67, 0x68, 0x61, 0x69, 0x20, 0x58, 0x69, 0x65, 0x54, 0x6F, 0x6E, 0x67,
            0x20, 0x49, 0x6E, 0x66, 0x6F, 0x72, 0x6D, 0x61, 0x74, 0x69, 0x6F, 0x6E, 0x20, 0x54, 0x65, 0x63,
            0x68, 0x6E, 0x6F, 0x6C, 0x6F, 0x67, 0x79, 0x20, 0x43, 0x6F, 0x2E, 0x2C, 0x20, 0x4C, 0x74, 0x64,
            0x31, 0x35, 0x30, 0x33, 0x06, 0x03, 0x55, 0x04, 0x0B, 0x0C, 0x2C, 0x58, 0x69, 0x65, 0x54, 0x6F,
            0x6E, 0x67, 0x20, 0x49, 0x6E, 0x66, 0x6F, 0x72, 0x6D, 0x61, 0x74, 0x69, 0x6F, 0x6E, 0x20, 0x54,
            0x65, 0x63, 0x68, 0x6E, 0x6F, 0x6C, 0x6F, 0x67, 0x79, 0x20, 0x53, 0x65, 0x63, 0x75, 0x72, 0x69,
            0x74, 0x79, 0x20, 0x54, 0x65, 0x61, 0x6D, 0x31, 0x42, 0x30, 0x40, 0x06, 0x03, 0x55, 0x04, 0x03,
            0x0C, 0x39, 0x58, 0x69, 0x65, 0x54, 0x6F, 0x6E, 0x67, 0x20, 0x49, 0x6E, 0x66, 0x6F, 0x72, 0x6D,
            0x61, 0x74, 0x69, 0x6F, 0x6E, 0x20, 0x54, 0x65, 0x63, 0x68, 0x6E, 0x6F, 0x6C, 0x6F, 0x67, 0x79,
            0x20, 0x52, 0x6F, 0x6F, 0x74, 0x20, 0x43, 0x65, 0x72, 0x74, 0x69, 0x66, 0x69, 0x63, 0x61, 0x74,
            0x65, 0x20, 0x41, 0x75, 0x74, 0x68, 0x6F, 0x72, 0x69, 0x74, 0x79, 0x31, 0x24, 0x30, 0x22, 0x06,
            0x09, 0x2A, 0x86, 0x48, 0x86, 0xF7, 0x0D, 0x01, 0x09, 0x01, 0x16, 0x15, 0x73, 0x75, 0x70, 0x70,
            0x6F, 0x72, 0x74, 0x40, 0x73, 0x68, 0x61, 0x72, 0x65, 0x74, 0x6F, 0x6D, 0x65, 0x2E, 0x63, 0x6F,
            0x6D, 0x30, 0x82, 0x02, 0x22, 0x30, 0x0D, 0x06, 0x09, 0x2A, 0x86, 0x48, 0x86, 0xF7, 0x0D, 0x01,
            0x01, 0x01, 0x05, 0x00, 0x03, 0x82, 0x02, 0x0F, 0x00, 0x30, 0x82, 0x02, 0x0A, 0x02, 0x82, 0x02,
            0x01, 0x00, 0xB7, 0xD2, 0x01, 0x79, 0xC0, 0x01, 0x03, 0x09, 0xF4, 0x6E, 0x76, 0x40, 0xC8, 0xD2,
            0x70, 0x70, 0x01, 0xCE, 0x2C, 0xC6, 0x00, 0xC2, 0x1D, 0xFA, 0x9B, 0x94, 0xF3, 0xBC, 0xC6, 0xFF,
            0xB7, 0x35, 0x4C, 0x6F, 0xFC, 0x19, 0x90, 0x72, 0xF2, 0xA0, 0x3A, 0x65, 0x76, 0xB5, 0xD1, 0x6B,
            0x7C, 0xF8, 0xE5, 0xE3, 0xB7, 0xBB, 0xBB, 0xE2, 0x7F, 0x59, 0x4B, 0x55, 0xD0, 0x45, 0x71, 0xE2,
            0xD7, 0x05, 0xF4, 0xAA, 0x6E, 0xCF, 0x96, 0xF0, 0xFA, 0x29, 0x99, 0xD2, 0x3A, 0xB4, 0x67, 0x6A,
            0x71, 0x14, 0x4C, 0xB9, 0xD8, 0xA6, 0xF6, 0x15, 0xCB, 0x88, 0xB0, 0xC3, 0x6F, 0x0F, 0x01, 0x2F,
            0x18, 0x10, 0xDC, 0x72, 0x9A, 0x53, 0x1E, 0x02, 0x6F, 0x07, 0x9B, 0x11, 0x4F, 0x14, 0xC9, 0x8D,
            0x6F, 0x3C, 0x0F, 0x3B, 0x89, 0x3D, 0xDB, 0x2A, 0x2E, 0x63, 0x3D, 0x56, 0xD1, 0x07, 0x3C, 0x0B,
            0x54, 0xEE, 0x7D, 0x73, 0xB2, 0x50, 0xA1, 0x47, 0x84, 0x06, 0xE6, 0xBE, 0x4B, 0xA6, 0x4C, 0x56,
            0xA2, 0x7B, 0x4D, 0xDE, 0x40, 0xCA, 0x72, 0xB1, 0x45, 0xA3, 0x5E, 0xC2, 0x76, 0x40, 0xB8, 0x2E,
            0xD9, 0xB0, 0xE3, 0x47, 0xF8, 0x19, 0xA5, 0x98, 0x4A, 0x8B, 0x28, 0x2A, 0xFA, 0x37, 0xB0, 0x48,
            0xA2, 0x18, 0xF4, 0x4B, 0xD2, 0xAA, 0xF7, 0x9A, 0x12, 0x5E, 0x19, 0x3E, 0xA6, 0x6F, 0x83, 0xD9,
            0xC3, 0x41, 0x8D, 0xDF, 0x21, 0x67, 0xA0, 0x5C, 0xCE, 0x69, 0x74, 0x80, 0x6E, 0x43, 0x9F, 0xE3,
            0x6D, 0x3D, 0xFE, 0xC4, 0xA8, 0xB1, 0x1D, 0x82, 0x41, 0xC7, 0x04, 0x92, 0xD5, 0xA4, 0x1B, 0x12,
            0x11, 0x70, 0x1E, 0x5F, 0xAF, 0x75, 0x54, 0xFA, 0x6B, 0xAD, 0x63, 0x85, 0xD8, 0x51, 0x14, 0x2B,
            0xFC, 0x6B, 0x7C, 0x9F, 0x9B, 0x9F, 0xA9, 0x22, 0xC6, 0xAD, 0xBD, 0x8D, 0x26, 0x11, 0x4C, 0xBC,
            0xA1, 0xAC, 0xE6, 0x4B, 0x5B, 0x65, 0x40, 0xD0, 0xB1, 0xEA, 0xE7, 0xE0, 0x2B, 0xD4, 0x44, 0x09,
            0x84, 0x39, 0x7A, 0xC3, 0x3B, 0x2B, 0x7B, 0xEB, 0x04, 0xE5, 0xDC, 0x99, 0x3C, 0x41, 0xD2, 0x7E,
            0xE2, 0x83, 0xE1, 0xD4, 0xFF, 0x8C, 0x49, 0x6A, 0x82, 0xDE, 0x50, 0xD2, 0x2C, 0x5C, 0x71, 0xB5,
            0x00, 0x50, 0x80, 0xD1, 0x60, 0x45, 0xFD, 0x91, 0xCF, 0xD8, 0x4C, 0xAB, 0x01, 0x78, 0x34, 0xEE,
            0x70, 0x45, 0xC5, 0xCA, 0x8C, 0x81, 0xD9, 0xC6, 0x2A, 0xA2, 0x35, 0x95, 0x08, 0x90, 0x38, 0xB8,
            0xAA, 0x1C, 0xD2, 0x55, 0x43, 0x41, 0x43, 0xCA, 0x3D, 0x60, 0xAB, 0x09, 0x58, 0x6C, 0x71, 0x73,
            0x3D, 0x3A, 0xA0, 0xE5, 0x96, 0x94, 0xDF, 0x59, 0xCB, 0x62, 0x20, 0xD9, 0x60, 0x9F, 0x51, 0x20,
            0x2E, 0xF4, 0x01, 0x08, 0xB8, 0x55, 0x9E, 0x95, 0x09, 0x70, 0x5F, 0x0C, 0x5C, 0x3E, 0x34, 0xED,
            0x42, 0x44, 0xE5, 0x75, 0xD3, 0xCB, 0x02, 0xFA, 0x17, 0xC2, 0xD1, 0x95, 0x0F, 0x6D, 0x21, 0xF3,
            0x4D, 0x3C, 0x12, 0x09, 0x20, 0x01, 0x57, 0xF7, 0xD4, 0x82, 0x92, 0x75, 0x53, 0x27, 0x9A, 0x9B,
            0x45, 0x3D, 0x08, 0xED, 0xF2, 0x94, 0xC2, 0xF1, 0xF4, 0xFA, 0xCB, 0x63, 0x31, 0x08, 0x7A, 0x2B,
            0x64, 0x58, 0xB1, 0xA0, 0x93, 0x0A, 0xBB, 0x88, 0xCD, 0x67, 0xB6, 0xBC, 0x11, 0xD9, 0x25, 0x71,
            0x4D, 0xFC, 0x6A, 0xEE, 0x16, 0xC4, 0x5E, 0x01, 0x33, 0x96, 0x04, 0x42, 0x3C, 0x5C, 0x14, 0xEA,
            0xEC, 0xCA, 0x56, 0x39, 0x24, 0x69, 0x04, 0x14, 0xBE, 0xAC, 0xEB, 0xA2, 0x2C, 0xA5, 0xEB, 0x5D,
            0x2B, 0x86, 0xE4, 0xF5, 0x7C, 0x91, 0x46, 0x5B, 0x2B, 0x22, 0x97, 0xFA, 0xB0, 0x26, 0x99, 0x00,
            0xD8, 0x08, 0xC6, 0xC0, 0x32, 0x59, 0x36, 0x1C, 0x76, 0x52, 0xDB, 0x05, 0x2B, 0x47, 0xE8, 0xC8,
            0x72, 0xA5, 0x02, 0x03, 0x01, 0x00, 0x01, 0xA3, 0x81, 0x8B, 0x30, 0x81, 0x88, 0x30, 0x1D, 0x06,
            0x03, 0x55, 0x1D, 0x0E, 0x04, 0x16, 0x04, 0x14, 0xBB, 0x8A, 0xA4, 0xAC, 0x73, 0x3B, 0x00, 0x7C,
            0xA8, 0x01, 0xBA, 0x38, 0x3B, 0x8B, 0xF1, 0x97, 0xB1, 0x67, 0x05, 0xFF, 0x30, 0x1F, 0x06, 0x03,
            0x55, 0x1D, 0x23, 0x04, 0x18, 0x30, 0x16, 0x80, 0x14, 0xBB, 0x8A, 0xA4, 0xAC, 0x73, 0x3B, 0x00,
            0x7C, 0xA8, 0x01, 0xBA, 0x38, 0x3B, 0x8B, 0xF1, 0x97, 0xB1, 0x67, 0x05, 0xFF, 0x30, 0x0C, 0x06,
            0x03, 0x55, 0x1D, 0x13, 0x04, 0x05, 0x30, 0x03, 0x01, 0x01, 0xFF, 0x30, 0x38, 0x06, 0x03, 0x55,
            0x1D, 0x1F, 0x04, 0x31, 0x30, 0x2F, 0x30, 0x2D, 0xA0, 0x2B, 0xA0, 0x29, 0x86, 0x27, 0x68, 0x74,
            0x74, 0x70, 0x3A, 0x2F, 0x2F, 0x77, 0x77, 0x77, 0x2E, 0x73, 0x68, 0x61, 0x72, 0x65, 0x74, 0x6F,
            0x6D, 0x65, 0x2E, 0x63, 0x6F, 0x6D, 0x2F, 0x63, 0x72, 0x6C, 0x2F, 0x63, 0x61, 0x2E, 0x63, 0x72,
            0x6C, 0x2E, 0x70, 0x65, 0x6D, 0x30, 0x0D, 0x06, 0x09, 0x2A, 0x86, 0x48, 0x86, 0xF7, 0x0D, 0x01,
            0x01, 0x0B, 0x05, 0x00, 0x03, 0x82, 0x02, 0x01, 0x00, 0x7F, 0x66, 0x99, 0x0E, 0x9C, 0xE8, 0xE4,
            0x4B, 0xCF, 0x14, 0x82, 0x54, 0xBB, 0x5F, 0x31, 0x91, 0x1D, 0x4B, 0x4C, 0xA8, 0x4A, 0x2C, 0xCF,
            0xA8, 0x84, 0xDE, 0xEF, 0xA9, 0x52, 0x87, 0xCB, 0x55, 0xE7, 0xF7, 0xC1, 0xD9, 0x4E, 0x08, 0x9B,
            0xC0, 0xD0, 0x7C, 0xDE, 0x52, 0x60, 0x32, 0x5C, 0xFC, 0xEA, 0xE3, 0x12, 0xFB, 0x36, 0x47, 0x8A,
            0x5D, 0x95, 0x95, 0x8F, 0x5A, 0x26, 0x91, 0xA5, 0x9D, 0x13, 0x18, 0x12, 0xA6, 0xFE, 0x93, 0x59,
            0x9D, 0xE3, 0xC0, 0xD2, 0xC0, 0xF4, 0x56, 0x54, 0x8B, 0x6B, 0x3D, 0xBF, 0x47, 0xF5, 0x62, 0x48,
            0x84, 0xE3, 0x23, 0x2C, 0x63, 0x99, 0x3C, 0x1B, 0xE5, 0x49, 0xC9, 0x2F, 0x48, 0xF6, 0xA4, 0x18,
            0x57, 0x43, 0x52, 0x4A, 0xB9, 0xE8, 0xBC, 0x00, 0xA3, 0x10, 0x07, 0x02, 0x64, 0x4C, 0x61, 0x0D,
            0x93, 0x8D, 0xD3, 0x7F, 0x2A, 0xEE, 0x46, 0xFB, 0x83, 0xC9, 0xA2, 0x3B, 0x06, 0xD0, 0xCC, 0xBD,
            0xF6, 0x7B, 0x31, 0x2A, 0x47, 0x74, 0xDB, 0x1D, 0x18, 0x6E, 0xEF, 0x07, 0x6C, 0x15, 0xA9, 0x8C,
            0xEF, 0x4A, 0xCA, 0x94, 0x8B, 0x2F, 0xDA, 0xBB, 0x40, 0x0A, 0x7F, 0x9D, 0x8F, 0x03, 0xAD, 0x29,
            0x24, 0x01, 0x96, 0x01, 0xF7, 0x1F, 0x9B, 0xD0, 0x41, 0x21, 0x37, 0x41, 0x2D, 0xE7, 0x2F, 0x68,
            0xA0, 0x5E, 0x2C, 0xA8, 0x95, 0x1D, 0x16, 0xB7, 0x8D, 0x64, 0x40, 0xF6, 0x68, 0x02, 0x41, 0xF7,
            0xFB, 0xD1, 0x69, 0xC1, 0x8F, 0x95, 0xB2, 0x0A, 0xA8, 0xA6, 0x0B, 0xEB, 0x51, 0xCB, 0x85, 0x86,
            0x1E, 0x94, 0x71, 0x22, 0xE1, 0xAE, 0xF0, 0xDD, 0xAF, 0x9D, 0xBE, 0xA3, 0x24, 0x8A, 0x90, 0x78,
            0x17, 0x17, 0x6C, 0x1F, 0xBF, 0xA8, 0x24, 0xCC, 0x4E, 0xA2, 0x0A, 0x63, 0xEC, 0xD9, 0x66, 0x15,
            0x2D, 0x11, 0x6D, 0xCF, 0x96, 0xF5, 0x85, 0xF9, 0x30, 0x68, 0x27, 0x7F, 0xC5, 0x60, 0xEF, 0x2C,
            0x92, 0x05, 0xDD, 0x97, 0x1B, 0xB4, 0x4B, 0x99, 0xDA, 0xF2, 0xAE, 0x5E, 0xB8, 0xA7, 0xEB, 0x57,
            0x72, 0x75, 0x24, 0xE5, 0x5B, 0x3F, 0x7D, 0xCA, 0x4B, 0x95, 0xF8, 0xED, 0xBA, 0xD8, 0x45, 0xEC,
            0x8E, 0xF5, 0x44, 0xA4, 0xC1, 0xCE, 0x0E, 0x70, 0xBB, 0x49, 0xBB, 0x2E, 0x58, 0x81, 0x36, 0x2D,
            0xE6, 0xDE, 0xC4, 0x4C, 0xDA, 0x12, 0x57, 0x4B, 0x5E, 0x44, 0x61, 0x86, 0x02, 0x7C, 0xE2, 0x3F,
            0x1C, 0x46, 0x31, 0x1A, 0x7E, 0xA2, 0x8D, 0x47, 0xDA, 0x63, 0xB1, 0x5C, 0x25, 0xDA, 0xFC, 0xEB,
            0x45, 0xCE, 0xAA, 0x89, 0xB7, 0xAE, 0x1D, 0x2D, 0xE2, 0x2B, 0xF6, 0x1A, 0xCF, 0x60, 0x75, 0x7B,
            0xBA, 0x2A, 0xF2, 0x4F, 0x10, 0xB8, 0xAD, 0x32, 0x85, 0x97, 0xA5, 0x90, 0xC8, 0xAC, 0x30, 0x3E,
            0x19, 0x14, 0x64, 0x41, 0xE2, 0x82, 0x2E, 0x63, 0xDF, 0x49, 0xEB, 0x33, 0x15, 0x2A, 0x0C, 0xCE,
            0x9C, 0x83, 0x4D, 0x87, 0x54, 0xDF, 0x3C, 0x32, 0x8E, 0x1E, 0x97, 0xF6, 0x25, 0x06, 0x26, 0x4B,
            0x26, 0xCA, 0x4B, 0x73, 0xD6, 0x7B, 0xC8, 0xA3, 0x43, 0xE9, 0x46, 0x82, 0x31, 0x1E, 0x04, 0x72,
            0xD4, 0x24, 0xFC, 0xA6, 0xAB, 0xBB, 0xA9, 0x30, 0x94, 0x77, 0xF3, 0x44, 0x4E, 0x17, 0xE0, 0x1C,
            0xF9, 0x56, 0x54, 0x1A, 0x49, 0x75, 0x1B, 0xCE, 0xDB, 0x53, 0x0A, 0x4F, 0x7F, 0x7B, 0x78, 0x57,
            0x74, 0x0B, 0xD5, 0x75, 0x22, 0xEC, 0xFF, 0xCA, 0xC0, 0x43, 0x71, 0x2A, 0xA9, 0xEC, 0xA0, 0x06,
            0x43, 0x68, 0x33, 0x79, 0xA8, 0x26, 0x23, 0x1F, 0x27, 0x26, 0xA3, 0xFE, 0x86, 0xD8, 0x65, 0x2A,
            0xA3, 0x32, 0xDB, 0x5D, 0x20, 0xCB, 0x20, 0x37, 0xED, 0x53, 0x0A, 0xDE, 0x32, 0xE0, 0xF5, 0x86,
            0x78, 0xEE, 0x16, 0xFE, 0xB7, 0x6C, 0x12, 0xA0, 0x5C,
    };

}