/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.datavalue.internal

import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.fileresource.FileResource
import org.hisp.dhis.android.core.fileresource.FileResourceDataDomainType
import org.hisp.dhis.android.core.fileresource.internal.FileResourceHelper
import org.hisp.dhis.android.core.fileresource.internal.FileResourcePostCall
import org.hisp.dhis.android.core.fileresource.internal.FileResourceStorageStatusVerifier
import org.hisp.dhis.android.core.fileresource.internal.FileResourceUploadResult
import org.hisp.dhis.android.core.fileresource.internal.FileResourceValue
import org.hisp.dhis.android.core.fileresource.internal.FileResourceVerificationResult
import org.koin.core.annotation.Singleton

@Singleton
internal class DataValueFileResourcePostCall(
    private val fileResourceHelper: FileResourceHelper,
    private val fileResourcePostCall: FileResourcePostCall,
    private val fileResourceStorageStatusVerifier: FileResourceStorageStatusVerifier,
) {
    suspend fun uploadFileResource(dataValues: List<DataValue>): DataValueFileResourcePostCallResult {
        val fileResources = fileResourceHelper.getUploadableFileResources()
        if (fileResources.isEmpty()) {
            return DataValueFileResourcePostCallResult(dataValues, emptyList())
        }

        val uploadResults = uploadAllFiles(dataValues, fileResources)
        val verificationResults = verifyUploadedFiles(uploadResults)
        updateVerifiedValues(uploadResults, verificationResults)

        return buildFinalResult(dataValues, uploadResults, verificationResults)
    }

    private suspend fun uploadAllFiles(
        dataValues: List<DataValue>,
        fileResources: List<FileResource>,
    ): List<Pair<DataValue, FileResourceUploadResult>> {
        return dataValues.mapNotNull { dataValue ->
            fileResourceHelper.findDataValueFileResource(dataValue, fileResources)?.let { fileResource ->
                val fValue = FileResourceValue.DataValue(dataValue.dataElement()!!)
                val uploadResult = fileResourcePostCall.uploadFileResourceWithoutUpdate(fileResource, fValue)
                dataValue to uploadResult
            }
        }
    }

    private suspend fun verifyUploadedFiles(
        uploadResults: List<Pair<DataValue, FileResourceUploadResult>>,
    ): Map<String, FileResourceVerificationResult> {
        val uidsToVerify = uploadResults.mapNotNull { it.second.uploadedUid }
        return if (uidsToVerify.isNotEmpty()) {
            fileResourceStorageStatusVerifier.verifyStorageStatusBatch(uidsToVerify)
        } else {
            emptyMap()
        }
    }

    private suspend fun updateVerifiedValues(
        uploadResults: List<Pair<DataValue, FileResourceUploadResult>>,
        verificationResults: Map<String, FileResourceVerificationResult>,
    ) {
        uploadResults.forEach { (_, uploadResult) ->
            val uploadedUid = uploadResult.uploadedUid ?: return@forEach
            val verificationResult = verificationResults[uploadedUid]
            if (verificationResult?.isVerified == true) {
                fileResourcePostCall.updateValueAfterVerification(
                    uploadResult.originalFileResource,
                    uploadedUid,
                    uploadResult.value,
                )
            }
        }
    }

    private fun buildFinalResult(
        dataValues: List<DataValue>,
        uploadResults: List<Pair<DataValue, FileResourceUploadResult>>,
        verificationResults: Map<String, FileResourceVerificationResult>,
    ): DataValueFileResourcePostCallResult {
        val uploadedFileResources = mutableListOf<String>()
        val validDataValues = dataValues.map { dataValue ->
            val uploadResult = uploadResults.find { it.first == dataValue }?.second
            val newUid = getVerifiedUid(uploadResult, verificationResults, uploadedFileResources)
            newUid?.let { dataValue.toBuilder().value(it).build() } ?: dataValue
        }
        return DataValueFileResourcePostCallResult(validDataValues, uploadedFileResources)
    }

    private fun getVerifiedUid(
        uploadResult: FileResourceUploadResult?,
        verificationResults: Map<String, FileResourceVerificationResult>,
        uploadedFileResources: MutableList<String>,
    ): String? {
        val uploadedUid = uploadResult?.uploadedUid ?: return null
        val verificationResult = verificationResults[uploadedUid]
        return if (verificationResult?.isVerified == true) {
            uploadedFileResources.add(uploadedUid)
            uploadedUid
        } else {
            null
        }
    }

    suspend fun updateFileResourceStates(fileResources: List<String>) {
        fileResourceHelper.updateFileResourceStates(fileResources, FileResourceDataDomainType.AGGREGATED)
    }
}

internal data class DataValueFileResourcePostCallResult(
    val dataValues: List<DataValue>,
    val fileResources: List<String>,
)
