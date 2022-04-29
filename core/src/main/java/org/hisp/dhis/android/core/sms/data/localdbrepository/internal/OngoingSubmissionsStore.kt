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
package org.hisp.dhis.android.core.sms.data.localdbrepository.internal

import android.util.Log
import android.util.Pair
import dagger.Reusable
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.sms.domain.repository.internal.LocalDbRepository.TooManySubmissionsException
import org.hisp.dhis.android.core.sms.domain.repository.internal.SubmissionType

@Reusable
internal class OngoingSubmissionsStore @Inject constructor(
    private val smsOngoingSubmissionStore: ObjectWithoutUidStore<SMSOngoingSubmission>,
    private val smsConfigStore: SMSConfigStore
) {
    private var ongoingSubmissions: Map<Int, SubmissionType>? = null
    private var lastGeneratedSubmissionId: Int? = null

    fun getOngoingSubmissions(): Single<Map<Int, SubmissionType>> {
        return Single.fromCallable {
            if (ongoingSubmissions == null) {
                updateOngoingSubmissions()
            }
            ongoingSubmissions!!
        }.onErrorReturn { throwable ->
            Log.e(TAG, throwable.message, throwable)
            emptyMap()
        }
    }

    @SuppressWarnings("MagicNumber")
    fun addOngoingSubmission(id: Int?, type: SubmissionType?): Completable {
        if (id == null || id < 0 || id > 255) {
            return Completable.error(IllegalArgumentException("Wrong submission id"))
        }
        return if (type == null) {
            Completable.error(IllegalArgumentException("Wrong submission type"))
        } else getOngoingSubmissions().flatMapCompletable { submissions: Map<Int, SubmissionType> ->
            if (submissions.containsKey(id)) {
                Completable.error(IllegalArgumentException("Submission id already exists"))
            } else {
                Completable.fromCallable {
                    val ongoingSubmission = SMSOngoingSubmission.builder().submissionId(id).type(type).build()
                    smsOngoingSubmissionStore.insert(ongoingSubmission)
                    updateOngoingSubmissions()
                }
            }
        }
    }

    fun removeOngoingSubmission(id: Int?): Completable {
        return if (id == null) {
            Completable.error(IllegalArgumentException("Wrong submission id"))
        } else {
            Completable.fromCallable {
                val whereClause = WhereClauseBuilder()
                    .appendKeyStringValue(SMSOngoingSubmissionTableInfo.Columns.SUBMISSION_ID, id)
                    .build()
                smsOngoingSubmissionStore.deleteWhereIfExists(whereClause)
                updateOngoingSubmissions()
            }
        }
    }

    private fun updateOngoingSubmissions() {
        val submissionList = smsOngoingSubmissionStore.selectAll()
        this.ongoingSubmissions = submissionList.associate { it.submissionId() to it.type() }

        lastGeneratedSubmissionId?.let { saveLastGeneratedSubmissionId(it) }
    }

    private fun getLastGeneratedSubmissionId(): Single<Int> {
        return if (lastGeneratedSubmissionId != null) {
            Single.just(lastGeneratedSubmissionId)
        } else Single.fromCallable {
            smsConfigStore.get(SMSConfigKey.LAST_SUBMISSION_ID)?.toInt() ?: 0
        }
            .doOnSuccess { lastGeneratedSubmissionId = it }
    }

    private fun saveLastGeneratedSubmissionId(id: Int) {
        smsConfigStore.set(SMSConfigKey.LAST_SUBMISSION_ID, id.toString())
    }

    @SuppressWarnings("MagicNumber")
    fun generateNextSubmissionId(): Single<Int> {
        return Single.zip<Map<Int, SubmissionType>, Int, Pair<Map<Int, SubmissionType>, Int>>(
            getOngoingSubmissions(),
            getLastGeneratedSubmissionId()
        ) { a: Map<Int, SubmissionType>?, b: Int? -> Pair.create(a, b) }
            .flatMap { ids: Pair<Map<Int, SubmissionType>, Int> ->
                val ongoingIds: Collection<Int> = ids.first.keys
                val lastId = ids.second
                var i = lastId
                do {
                    i++
                    if (i >= 255) {
                        i = 0
                    }
                    if (!ongoingIds.contains(i)) {
                        lastGeneratedSubmissionId = i
                        return@flatMap Single.just(i)
                    }
                } while (i != lastId)
                Single.error(TooManySubmissionsException())
            }
    }

    companion object {
        private val TAG = OngoingSubmissionsStore::class.java.simpleName
    }
}
