/*
 *  Copyright (c) 2004-2025, University of Oslo
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
package org.hisp.dhis.android.core.fileresource.internal

import android.util.Log
import kotlinx.coroutines.delay
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.fileresource.FileResourceInternalAccessor
import org.hisp.dhis.android.core.fileresource.FileResourceStorageStatus
import org.hisp.dhis.android.core.fileresource.internal.FileResourcePostCall.Companion.TAG
import org.koin.core.annotation.Singleton
import kotlin.math.pow

/**
 * Verifies the storage status of uploaded FileResources in batch to ensure they are STORED
 * before being used in data values. This prevents issues with files being wrongly assigned
 * when they are posted without waiting for the STORED status.
 *
 */
@Singleton
internal class FileResourceStorageStatusVerifier(
    private val fileResourceNetworkHandler: FileResourceNetworkHandler,
    private val coroutineAPICallExecutor: CoroutineAPICallExecutor,
) {

    /**
     * Verifies the storage status of multiple file resources in batch.
     *
     * @param fileResourceUids List of file resource UIDs to verify
     * @param maxAttempts Maximum number of polling attempts per file (default: 30)
     * @param delayMs Delay in milliseconds between polling attempts (default: 1000ms)
     * @param batchSize Number of files to verify in parallel (default: 10)
     * @return Map of file resource UID to verification result (true if STORED, false otherwise)
     */
    suspend fun verifyStorageStatusBatch(
        fileResourceUids: List<String>,
        maxAttempts: Int = DEFAULT_MAX_ATTEMPTS,
        delayMs: Long = DEFAULT_DELAY_MS,
        batchSize: Int = DEFAULT_BATCH_SIZE,
    ): Map<String, FileResourceVerificationResult> {
        if (fileResourceUids.isEmpty()) {
            return emptyMap()
        }

        Log.d(TAG, "Starting batch verification for ${fileResourceUids.size} file resources")

        val results = mutableMapOf<String, FileResourceVerificationResult>()
        val pendingUids = fileResourceUids.toMutableSet()

        for (attempt in 1..maxAttempts) {
            if (pendingUids.isEmpty()) {
                break
            }

            Log.d(TAG, "Verification attempt $attempt/$maxAttempts for ${pendingUids.size} pending files")

            // Process in batches to avoid overwhelming the server
            val currentBatch = pendingUids.take(batchSize)
            val batchResults = verifyBatch(currentBatch)

            batchResults.forEach { (uid, result) ->
                when (result.status) {
                    FileResourceStorageStatus.STORED -> {
                        results[uid] = result
                        pendingUids.remove(uid)
                        Log.d(TAG, "File resource $uid is STORED")
                    }
                    FileResourceStorageStatus.FAILED -> {
                        results[uid] = result
                        pendingUids.remove(uid)
                        Log.w(TAG, "File resource $uid FAILED to store")
                    }
                    FileResourceStorageStatus.PENDING -> {
                        // Keep in pending list for next attempt
                        Log.d(TAG, "File resource $uid still PENDING")
                    }
                    FileResourceStorageStatus.NONE -> {
                        // Treat as pending
                        Log.d(TAG, "File resource $uid has status NONE")
                    }
                }
            }

            // If there are still pending files and we haven't reached max attempts, wait before next poll
            if (pendingUids.isNotEmpty() && attempt < maxAttempts) {
                delay((delayMs * (2.0.pow(attempt) - 1)).toLong())
            }
        }

        // Mark remaining pending files as timed out
        pendingUids.forEach { uid ->
            results[uid] = FileResourceVerificationResult(
                uid = uid,
                status = FileResourceStorageStatus.PENDING,
                isVerified = false,
                timedOut = true,
            )
            Log.w(TAG, "File resource $uid timed out after $maxAttempts attempts")
        }

        val storedCount = results.count { it.value.isVerified }
        val failedCount = results.count { it.value.status == FileResourceStorageStatus.FAILED }
        val timedOutCount = results.count { it.value.timedOut }

        Log.i(
            TAG,
            "Batch verification complete: $storedCount stored, $failedCount failed, $timedOutCount timed out",
        )

        return results
    }

    /**
     * Verifies a single batch of file resources.
     */
    private suspend fun verifyBatch(uids: List<String>): Map<String, FileResourceVerificationResult> {
        return uids.associateWith { uid ->
            verifyFileResource(uid)
        }
    }

    /**
     * Verifies a single file resource by fetching its current status from the server.
     */
    @Suppress("TooGenericExceptionCaught")
    private suspend fun verifyFileResource(uid: String): FileResourceVerificationResult {
        return try {
            val fileResource = coroutineAPICallExecutor.wrap(storeError = false) {
                fileResourceNetworkHandler.getFileResource(uid)
            }.getOrThrow()

            val status = FileResourceInternalAccessor.storageStatus(fileResource)
            val isStored = FileResourceInternalAccessor.isStored(fileResource)

            FileResourceVerificationResult(
                uid = uid,
                status = status,
                isVerified = isStored,
                timedOut = false,
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error verifying file resource $uid: ${e.message}", e)
            FileResourceVerificationResult(
                uid = uid,
                status = FileResourceStorageStatus.FAILED,
                isVerified = false,
                timedOut = false,
            )
        }
    }

    companion object {
        private const val DEFAULT_MAX_ATTEMPTS = 30
        private const val DEFAULT_DELAY_MS = 1000L
        private const val DEFAULT_BATCH_SIZE = 10
    }
}
