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

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallErrorCatcher
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.fileresource.FileResource
import org.hisp.dhis.android.core.fileresource.FileResourceInternalAccessor
import org.hisp.dhis.android.core.fileresource.FileResourceStorageStatus
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class FileResourceStorageStatusVerifierShould {

    private val fileResourceNetworkHandler: FileResourceNetworkHandler = mock()

    // Fake implementation that just executes the block
    private val coroutineAPICallExecutor = object : CoroutineAPICallExecutor {
        override suspend fun <P> wrap(
            storeError: Boolean,
            acceptedErrorCodes: List<Int>?,
            errorCatcher: APICallErrorCatcher?,
            errorClassParser: ((body: String) -> P)?,
            block: suspend () -> P,
        ): Result<P, D2Error> {
            return try {
                Result.Success(block())
            } catch (e: D2Error) {
                Result.Failure(e)
            } catch (e: Exception) {
                Result.Failure(
                    D2Error.builder()
                        .errorCode(D2ErrorCode.API_UNSUCCESSFUL_RESPONSE)
                        .errorDescription(e.message ?: "Unknown error")
                        .build(),
                )
            }
        }

        override suspend fun <P> wrapTransactionallyRoom(
            cleanForeignKeyErrors: Boolean,
            block: suspend () -> P,
        ): P = block()
    }

    private lateinit var verifier: FileResourceStorageStatusVerifier

    @Before
    fun setUp() {
        verifier = FileResourceStorageStatusVerifier(
            fileResourceNetworkHandler,
            coroutineAPICallExecutor,
        )
    }

    @Test
    fun `return empty map when no UIDs provided`() = runTest {
        val result = verifier.verifyStorageStatusBatch(emptyList())

        assertThat(result).isEmpty()
    }

    @Test
    fun `verify single file resource with STORED status`() = runTest {
        val uid = "test-uid-1"
        val fileResourceBuilder = FileResource.builder().uid(uid)
        val fileResource = FileResourceInternalAccessor.insertStorageStatus(
            fileResourceBuilder,
            FileResourceStorageStatus.STORED,
        ).build()

        whenever(fileResourceNetworkHandler.getFileResource(uid))
            .thenReturn(fileResource)

        val result = verifier.verifyStorageStatusBatch(listOf(uid), maxAttempts = 1)

        assertThat(result).hasSize(1)
        assertThat(result[uid]?.isVerified).isTrue()
        assertThat(result[uid]?.status).isEqualTo(FileResourceStorageStatus.STORED)
        assertThat(result[uid]?.timedOut).isFalse()
    }

    @Test
    fun `verify multiple file resources with STORED status`() = runTest {
        val uid1 = "test-uid-1"
        val uid2 = "test-uid-2"
        val uid3 = "test-uid-3"

        val fileResource1 = FileResourceInternalAccessor.insertStorageStatus(
            FileResource.builder().uid(uid1),
            FileResourceStorageStatus.STORED,
        ).build()

        val fileResource2 = FileResourceInternalAccessor.insertStorageStatus(
            FileResource.builder().uid(uid2),
            FileResourceStorageStatus.STORED,
        ).build()

        val fileResource3 = FileResourceInternalAccessor.insertStorageStatus(
            FileResource.builder().uid(uid3),
            FileResourceStorageStatus.STORED,
        ).build()

        whenever(fileResourceNetworkHandler.getFileResource(uid1)).thenReturn(fileResource1)
        whenever(fileResourceNetworkHandler.getFileResource(uid2)).thenReturn(fileResource2)
        whenever(fileResourceNetworkHandler.getFileResource(uid3)).thenReturn(fileResource3)

        val result = verifier.verifyStorageStatusBatch(listOf(uid1, uid2, uid3), maxAttempts = 1)

        assertThat(result).hasSize(3)
        assertThat(result[uid1]?.isVerified).isTrue()
        assertThat(result[uid2]?.isVerified).isTrue()
        assertThat(result[uid3]?.isVerified).isTrue()
    }

    @Test
    fun `mark file resource as FAILED when status is FAILED`() = runTest {
        val uid = "test-uid-failed"
        val fileResource = FileResourceInternalAccessor.insertStorageStatus(
            FileResource.builder().uid(uid),
            FileResourceStorageStatus.FAILED,
        ).build()

        whenever(fileResourceNetworkHandler.getFileResource(uid))
            .thenReturn(fileResource)

        val result = verifier.verifyStorageStatusBatch(listOf(uid), maxAttempts = 1)

        assertThat(result).hasSize(1)
        assertThat(result[uid]?.isVerified).isFalse()
        assertThat(result[uid]?.status).isEqualTo(FileResourceStorageStatus.FAILED)
        assertThat(result[uid]?.timedOut).isFalse()
    }

    @Test
    fun `mark file resource as timed out when PENDING after max attempts`() = runTest {
        val uid = "test-uid-pending"
        val fileResource = FileResourceInternalAccessor.insertStorageStatus(
            FileResource.builder().uid(uid),
            FileResourceStorageStatus.PENDING,
        ).build()

        whenever(fileResourceNetworkHandler.getFileResource(uid))
            .thenReturn(fileResource)

        val result = verifier.verifyStorageStatusBatch(
            listOf(uid),
            maxAttempts = 2,
            delayMs = 10,
        )

        assertThat(result).hasSize(1)
        assertThat(result[uid]?.isVerified).isFalse()
        assertThat(result[uid]?.status).isEqualTo(FileResourceStorageStatus.PENDING)
        assertThat(result[uid]?.timedOut).isTrue()
    }

    @Test
    fun `handle mixed statuses correctly`() = runTest {
        val uidStored = "test-uid-stored"
        val uidFailed = "test-uid-failed"
        val uidPending = "test-uid-pending"

        val fileResourceStored = FileResourceInternalAccessor.insertStorageStatus(
            FileResource.builder().uid(uidStored),
            FileResourceStorageStatus.STORED,
        ).build()

        val fileResourceFailed = FileResourceInternalAccessor.insertStorageStatus(
            FileResource.builder().uid(uidFailed),
            FileResourceStorageStatus.FAILED,
        ).build()

        val fileResourcePending = FileResourceInternalAccessor.insertStorageStatus(
            FileResource.builder().uid(uidPending),
            FileResourceStorageStatus.PENDING,
        ).build()

        whenever(fileResourceNetworkHandler.getFileResource(uidStored)).thenReturn(fileResourceStored)
        whenever(fileResourceNetworkHandler.getFileResource(uidFailed)).thenReturn(fileResourceFailed)
        whenever(fileResourceNetworkHandler.getFileResource(uidPending)).thenReturn(fileResourcePending)

        val result = verifier.verifyStorageStatusBatch(
            listOf(uidStored, uidFailed, uidPending),
            maxAttempts = 1,
        )

        assertThat(result).hasSize(3)
        assertThat(result[uidStored]?.isVerified).isTrue()
        assertThat(result[uidFailed]?.isVerified).isFalse()
        assertThat(result[uidFailed]?.status).isEqualTo(FileResourceStorageStatus.FAILED)
        assertThat(result[uidPending]?.isVerified).isFalse()
        assertThat(result[uidPending]?.timedOut).isTrue()
    }

    @Test
    fun `handle network errors gracefully`() = runTest {
        val uid = "test-uid-error"
        val error = D2Error.builder()
            .errorCode(D2ErrorCode.API_UNSUCCESSFUL_RESPONSE)
            .errorDescription("Network error")
            .build()

        whenever(fileResourceNetworkHandler.getFileResource(uid))
            .thenAnswer { throw error }

        val result = verifier.verifyStorageStatusBatch(listOf(uid), maxAttempts = 1)

        assertThat(result).hasSize(1)
        assertThat(result[uid]?.isVerified).isFalse()
        assertThat(result[uid]?.status).isEqualTo(FileResourceStorageStatus.FAILED)
    }
}
