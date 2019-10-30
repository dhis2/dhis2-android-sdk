/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.arch.storage.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.x500.X500Principal;

final class AndroidSecureStore implements SecureStore {

    private static final String KEY_ALGORITHM_RSA = "RSA";

    private static final String KEYSTORE_PROVIDER_ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String RSA_ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding";
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private static final String PREFERENCES_FILE = "preferences";

    private SharedPreferences preferences;
    private String alias = "sdk";

    AndroidSecureStore(Context context) {
        preferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);

        KeyStore ks;

        try {
            ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);

            //Use null to load Keystore with default parameters.
            ks.load(null);

            // Check if Private and Public already keys exists. If so we don't need to generate them again
            PrivateKey privateKey = (PrivateKey) ks.getKey(alias, null);
            if (privateKey != null && ks.getCertificate(alias) != null) {
                PublicKey publicKey = ks.getCertificate(alias).getPublicKey();
                if (publicKey != null) {
                    // All keys are available.
                    return;
                }
            }
        } catch (Exception ex) {
            return;
        }

        // Create a start and end time, for the validity range of the key pair that's about to be
        // generated.
        Calendar start = new GregorianCalendar();
        Calendar end = new GregorianCalendar();
        end.add(Calendar.YEAR, 10);

        // Specify the parameters object which will be passed to KeyPairGenerator
        AlgorithmParameterSpec spec;
        if (android.os.Build.VERSION.SDK_INT < 23) {
            spec = new android.security.KeyPairGeneratorSpec.Builder(context)
                    // Alias - is a key for your KeyPair, to obtain it from Keystore in future.
                    .setAlias(alias)
                    // The subject used for the self-signed certificate of the generated pair
                    .setSubject(new X500Principal("CN=" + alias))
                    // The serial number used for the self-signed certificate of the generated pair.
                    .setSerialNumber(BigInteger.valueOf(1337))
                    // Date range of validity for the generated pair.
                    .setStartDate(start.getTime()).setEndDate(end.getTime())
                    .build();
        } else {
            spec = new KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_DECRYPT)
                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                    .build();
        }

        // Initialize a KeyPair generator using the the intended algorithm (in this example, RSA
        // and the KeyStore. This example uses the AndroidKeyStore.
        KeyPairGenerator kpGenerator;
        try {
            kpGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM_RSA, KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
            kpGenerator.initialize(spec);
            // Generate private/public keys
            kpGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchProviderException e) {
            try {
                if (ks != null)
                    ks.deleteEntry(alias);
            } catch (Exception e1) {
                // Just ignore any errors here
            }
        }
    }

    public void setData(String key, String data) {
        KeyStore ks = null;
        try {
            ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);

            ks.load(null);
            if (ks.getCertificate(alias) == null) return;

            PublicKey publicKey = ks.getCertificate(alias).getPublicKey();

            if (publicKey == null) {
                return;
            }

            String value = encrypt(publicKey, data.getBytes(CHARSET));

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(key, value);
            editor.apply();
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException
                | IllegalBlockSizeException | BadPaddingException | KeyStoreException |
                CertificateException | IOException e) {
            try {
                if (ks != null)
                    ks.deleteEntry(alias);
            } catch (Exception e1) {
                // Just ignore any errors here
            }
        }
    }

    public String getData(String key) {
        KeyStore ks = null;
        try {
            ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
            ks.load(null);
            PrivateKey privateKey = (PrivateKey) ks.getKey(alias, null);
            String value = preferences.getString(key, null);

            return value == null ? null :
                    new String(decrypt(privateKey, preferences.getString(key, null)), CHARSET);
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException
                | UnrecoverableEntryException | InvalidKeyException | NoSuchPaddingException
                | IllegalBlockSizeException | BadPaddingException e) {
            try {
                if (ks != null)
                    ks.deleteEntry(alias);
            } catch (Exception e1) {
                // Just ignore any errors here
            }
        }
        return null;
    }

    public void removeData(String key) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();
    }

    private static String encrypt(PublicKey encryptionKey, byte[] data) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Cipher cipher = Cipher.getInstance(RSA_ECB_PKCS1_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, encryptionKey);
        byte[] encrypted = cipher.doFinal(data);
        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    private static byte[] decrypt(PrivateKey decryptionKey, String encryptedData) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        if (encryptedData == null)
            return null;
        byte[] encryptedBuffer = Base64.decode(encryptedData, Base64.DEFAULT);
        Cipher cipher = Cipher.getInstance(RSA_ECB_PKCS1_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, decryptionKey);
        return cipher.doFinal(encryptedBuffer);
    }
}