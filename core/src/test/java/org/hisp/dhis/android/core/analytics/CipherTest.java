/*
 *  Copyright (c) 2004-2024, University of Oslo
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

package org.hisp.dhis.android.core.analytics;

import org.junit.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class CipherTest {

    private static byte[] salt = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };

    private static String encryptMessageGH(String message, String password) throws Exception {
        byte[] iv = salt;
        SecretKey aesKey = getAESKeyFromPassword(password, iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(iv));
        byte[] ciphertext = cipher.doFinal(message.getBytes());
        byte[] encrypted = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, encrypted, 0, iv.length);
        System.arraycopy(ciphertext, 0, encrypted, iv.length, ciphertext.length);
        return Base64.getEncoder().encodeToString(encrypted);
    }

    private static String decryptMessageGH(String encryptedMessage, String password) throws Exception {
        byte[] encrypted = Base64.getDecoder().decode(encryptedMessage);
        byte[] iv = salt;
        SecretKey aesKey = getAESKeyFromPassword(password, iv);
        System.arraycopy(encrypted, 0, iv, 0, iv.length);
        byte[] ciphertext = new byte[encrypted.length - iv.length];
        System.arraycopy(encrypted, iv.length, ciphertext, 0, ciphertext.length);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(iv));
        return new String(cipher.doFinal(ciphertext), "UTF-8");
    }

    public static SecretKey getAESKeyFromPassword(String password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        long start = System.currentTimeMillis();

        SecretKeyFactory factory =    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        // iterationCount = 1000
        // keyLength = 256
        int ITERATION_COUNT = 100000;
        int keyLength = 256;
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, keyLength);
        SecretKey key =  new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");

        long end = System.currentTimeMillis();

        System.out.println("Key generated in " + (end - start));

        return key;
    }

    @Test
    public void cipher_test() throws Exception {
        String orig = "Test message";
        String enc = encryptMessageGH(orig, "abcdef1234");
        System.out.println("Encrypted: " + enc);
        String dec = decryptMessageGH(enc, "abcdef1234");
        System.out.println("Decrypted: " + dec);
    }
}
