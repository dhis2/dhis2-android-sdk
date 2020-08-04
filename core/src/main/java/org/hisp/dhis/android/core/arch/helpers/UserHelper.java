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

package org.hisp.dhis.android.core.arch.helpers;

import com.google.android.gms.common.util.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okio.ByteString;

public final class UserHelper {

    private UserHelper() {
        // no instances
    }

    /**
     * Encode the given username and password to a base 64 {@link String}.
     *
     * @param username The username of the user account.
     * @param password The password of the user account.
     * @return A encoded base 64 {@link String}.
     */
    public static String base64(String username, String password) {
        String usernameAndPassword = username + ":" + password;
        byte[] bytes = usernameAndPassword.getBytes(StandardCharsets.ISO_8859_1);
        return ByteString.of(bytes).base64();
    }

    /**
     * Encode the given username and password to a MD5 {@link String}.
     *
     * @param username The username of the user account.
     * @param password The password of the user account.
     * @return A encoded MD5 {@link String}.
     */
    @SuppressWarnings({"PMD.UseLocaleWithCaseConversions"})
    public static String md5(String username, String password) {
        try {
            String credentials = usernameAndPassword(username, password);
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(credentials.getBytes(StandardCharsets.ISO_8859_1));
            return Hex.bytesToStringUppercase(md.digest()).toLowerCase();
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            // noop. Every implementation of Java is required to support MD5
            throw new AssertionError(noSuchAlgorithmException);
        }
    }

    private static String usernameAndPassword(String username, String password) {
        return username + ":" + password;
    }
}
