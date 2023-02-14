/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.arch.storage.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;

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
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.x500.X500Principal;

@SuppressWarnings({"PMD.EmptyCatchBlock", "PMD.ExcessiveImports", "PMD.PreserveStackTrace"})
public final class AndroidSecureStore implements SecureStore {

    private static final String KEY_CIPHER_JELLYBEAN_PROVIDER = "AndroidOpenSSL";
    private static final String KEY_CIPHER_MARSHMALLOW_PROVIDER = "AndroidKeyStoreBCWorkaround";

    private static final String KEY_ALGORITHM_RSA = "RSA";

    private static final String KEYSTORE_PROVIDER_ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String RSA_ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding";
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private static final String PREFERENCES_FILE = "preferences";
    private static final String ALIAS = "dhis_sdk_key";

    private final SharedPreferences preferences;

    public AndroidSecureStore(Context context) throws D2Error {
        preferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);

        KeyStore ks;

        try {
            ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
            ks.load(null);
            PrivateKey privateKey = (PrivateKey) ks.getKey(ALIAS, null);

            if (privateKey != null && ks.getCertificate(ALIAS) != null) {
                PublicKey publicKey = ks.getCertificate(ALIAS).getPublicKey();
                if (publicKey != null) {
                    return;
                }
            }
        } catch (KeyStoreException | CertificateException | IOException |
                NoSuchAlgorithmException | UnrecoverableKeyException ex) {
            throw keyStoreError(ex, D2ErrorCode.CANT_ACCESS_KEYSTORE);
        }

        // Create a start and end time, for the validity range of the key pair that's about to be
        // generated.
        Calendar start = new GregorianCalendar();
        Calendar end = new GregorianCalendar();
        end.add(Calendar.YEAR, 10);

        AlgorithmParameterSpec spec;
        if (android.os.Build.VERSION.SDK_INT < 23) {
            spec = new android.security.KeyPairGeneratorSpec.Builder(context)
                    .setAlias(ALIAS)
                    .setSubject(new X500Principal("CN=" + ALIAS))
                    .setSerialNumber(BigInteger.valueOf(1337))
                    .setStartDate(start.getTime()).setEndDate(end.getTime())
                    .build();
        } else {
            spec = new KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_DECRYPT)
                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                    .build();
        }

        KeyPairGenerator kpGenerator;
        try {
            kpGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM_RSA, KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
            kpGenerator.initialize(spec);
            kpGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchProviderException e) {
            deleteKeyStoreEntry(ks, ALIAS);
            throw keyStoreError(e, D2ErrorCode.CANT_INSTANTIATE_KEYSTORE);
        }
    }

    public void setData(@NonNull String key, @Nullable String data) {
        if (data == null) {
            return;
        }
        KeyStore ks = null;
        try {
            ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
            ks.load(null);

            if (ks.getCertificate(ALIAS) == null) {
                throw new RuntimeException("Couldn't find certificate for key: " + key);
            }

            PublicKey publicKey = ks.getCertificate(ALIAS).getPublicKey();

            if (publicKey == null) {
                throw new RuntimeException("Couldn't find publicKey for key: " + key);
            }

            String value = encrypt(publicKey, data.getBytes(CHARSET));

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(key, value);
            editor.apply();
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException
                | IllegalBlockSizeException | BadPaddingException | KeyStoreException |
                CertificateException | IOException e) {
            deleteKeyStoreEntry(ks, ALIAS);
            throw new RuntimeException("Couldn't store value in AndroidSecureStore for key: " + key, e);
        }
    }

    public String getData(@NonNull String key) {
        KeyStore ks = null;
        PrivateKey privateKey;
        String value = null;
        try {
            ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
            ks.load(null);
            privateKey = (PrivateKey) ks.getKey(ALIAS, null);
            value = preferences.getString(key, null);

            return value == null ? null :
                    new String(decrypt(privateKey, value), CHARSET);
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException
                | UnrecoverableEntryException | InvalidKeyException | NoSuchPaddingException
                | IllegalBlockSizeException | BadPaddingException e) {
            deleteKeyStoreEntry(ks, ALIAS);
            String valueToDisplay = value == null ? "null" : value;
            String errorMessage = String.format(
                    "Couldn't get value from AndroidSecureStore for key: %s and value: %s",
                    key,
                    valueToDisplay);
            throw new RuntimeException(errorMessage, e);
        }
    }

    private static Cipher getCipherInstance() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return Cipher.getInstance(RSA_ECB_PKCS1_PADDING, KEY_CIPHER_MARSHMALLOW_PROVIDER);
            } else {
                return Cipher.getInstance(RSA_ECB_PKCS1_PADDING, KEY_CIPHER_JELLYBEAN_PROVIDER);
            }
        } catch(Exception exception) {
            throw new RuntimeException("getCipher: Failed to get an instance of Cipher", exception);
        }
    }

    public void removeData(String key) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();
    }

    private static String encrypt(PublicKey encryptionKey, byte[] data) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Cipher cipher = getCipherInstance();
        cipher.init(Cipher.ENCRYPT_MODE, encryptionKey);
        byte[] encrypted = cipher.doFinal(data);
        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    private static byte[] decrypt(PrivateKey decryptionKey, @NonNull String encryptedData)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {

        byte[] encryptedBuffer = Base64.decode(encryptedData, Base64.DEFAULT);
        Cipher cipher = getCipherInstance();
        cipher.init(Cipher.DECRYPT_MODE, decryptionKey);
        return cipher.doFinal(encryptedBuffer);
    }

    private void deleteKeyStoreEntry(KeyStore ks, String entry) {
        try {
            if (ks != null) {
                ks.deleteEntry(entry);
            }
        } catch (Exception e1) {
            Log.w("SECURE_STORE", "Cannot deleted entry " + entry);
        }
    }

    private D2Error keyStoreError(Exception ex, D2ErrorCode d2ErrorCode) {
        return D2Error.builder()
                .errorComponent(D2ErrorComponent.SDK)
                .errorCode(d2ErrorCode)
                .errorDescription(ex.getMessage())
                .originalException(ex)
                .created(new Date())
                .build();
    }
}