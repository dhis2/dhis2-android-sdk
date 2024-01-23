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

package org.hisp.dhis.android.core.util

import java.io.File
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

@Suppress("NestedBlockDepth", "MagicNumber")
internal object CipherUtil {
    fun encryptFileUsingCredentials(input: File, output: File, username: String, password: String) {
        val cipher = getCipher(Cipher.ENCRYPT_MODE, username, password)
        applyCipher(cipher, input, output)
    }

    fun decryptFileUsingCredentials(input: File, output: File, username: String, password: String) {
        val cipher = getCipher(Cipher.DECRYPT_MODE, username, password)
        applyCipher(cipher, input, output)
    }

    private fun applyCipher(cipher: Cipher, input: File, output: File) {
        input.inputStream().use { inputStream ->
            output.outputStream().use { outputStream ->
                val buffer = ByteArray(64)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    cipher.update(buffer, 0, bytesRead)?.let { outputStream.write(it) }
                }
                cipher.doFinal()?.let { outputStream.write(it) }
            }
        }
    }

    private fun getCipher(mode: Int, username: String, password: String): Cipher {
        val iv: ByteArray = getSalt(username)
        val aesKey: SecretKey = getAESKeyFromPassword(password, iv)
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(mode, aesKey, IvParameterSpec(iv))

        return cipher
    }

    internal fun getSalt(string: String): ByteArray {
        val md = MessageDigest.getInstance("MD5")
        md.reset()
        md.update(string.toByteArray((StandardCharsets.UTF_8)))
        return md.digest().slice(IntRange(0, 15)).toByteArray()
    }

    private fun getAESKeyFromPassword(password: String, salt: ByteArray?): SecretKey {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val iterationCount = 10000
        val keyLength = 256
        val spec: KeySpec = PBEKeySpec(password.toCharArray(), salt, iterationCount, keyLength)
        return SecretKeySpec(factory.generateSecret(spec).encoded, "AES")
    }
}
