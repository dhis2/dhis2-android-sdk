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
package org.hisp.dhis.android.core.dataset

import android.util.Log
import io.reactivex.Completable
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender
import org.hisp.dhis.android.core.arch.repositories.`object`.ReadWriteObjectRepository
import org.hisp.dhis.android.core.arch.repositories.`object`.internal.ObjectRepositoryFactory
import org.hisp.dhis.android.core.arch.repositories.`object`.internal.ReadOnlyOneObjectRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.dataset.internal.DataSetCompleteRegistrationStore
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.hisp.dhis.android.core.user.UserCredentialsObjectRepository
import java.util.Date

class DataSetCompleteRegistrationObjectRepository internal constructor(
    private val dataSetCompleteRegistrationStore: DataSetCompleteRegistrationStore,
    private val credentialsRepository: UserCredentialsObjectRepository,
    childrenAppenders: Map<String, ChildrenAppender<DataSetCompleteRegistration>>,
    scope: RepositoryScope,
    private val period: String,
    private val organisationUnit: String,
    private val dataSet: String,
    private val attributeOptionCombo: String
) : ReadOnlyOneObjectRepositoryImpl<DataSetCompleteRegistration, DataSetCompleteRegistrationObjectRepository>(
    dataSetCompleteRegistrationStore, childrenAppenders, scope,
    ObjectRepositoryFactory { s: RepositoryScope ->
        DataSetCompleteRegistrationObjectRepository(
            dataSetCompleteRegistrationStore, credentialsRepository, childrenAppenders, s,
            period, organisationUnit, dataSet, attributeOptionCombo
        )
    }), ReadWriteObjectRepository<DataSetCompleteRegistration> {
    fun set(): Completable {
        return Completable.fromAction { blockingSet() }
    }

    fun blockingSet() {
        val dataSetCompleteRegistration = blockingGetWithoutChildren()
        if (dataSetCompleteRegistration == null) {
            val username = credentialsRepository.blockingGet()!!.username()
            dataSetCompleteRegistrationStore.insert(
                DataSetCompleteRegistration.builder()
                    .period(period)
                    .dataSet(dataSet)
                    .organisationUnit(organisationUnit)
                    .attributeOptionCombo(attributeOptionCombo)
                    .date(Date())
                    .storedBy(username)
                    .syncState(State.TO_POST)
                    .deleted(false)
                    .build()
            )
        } else {
            val syncState =
                if (dataSetCompleteRegistration.syncState() === State.TO_POST) State.TO_POST
                else State.TO_UPDATE

            val newRecord = dataSetCompleteRegistration.toBuilder()
                .deleted(false)
                .syncState(syncState)
                .build()

            dataSetCompleteRegistrationStore.updateWhere(newRecord)
        }
    }

    override fun delete(): Completable {
        return Completable.fromAction { blockingDelete() }
    }

    @Throws(D2Error::class)
    override fun blockingDelete() {
        val dataSetCompleteRegistration = blockingGetWithoutChildren()
        if (dataSetCompleteRegistration == null) {
            throw D2Error
                .builder()
                .errorComponent(D2ErrorComponent.SDK)
                .errorCode(D2ErrorCode.CANT_DELETE_NON_EXISTING_OBJECT)
                .errorDescription(
                    "DataSetCompleteRegistration can't be deleted because no longer exists"
                )
                .build()
        } else {
            if (dataSetCompleteRegistration.syncState() === State.TO_POST) {
                dataSetCompleteRegistrationStore.deleteWhere(dataSetCompleteRegistration)
            } else {
                val deletedRecord = dataSetCompleteRegistration.toBuilder()
                    .deleted(true)
                    .syncState(State.TO_UPDATE)
                    .build()
                dataSetCompleteRegistrationStore.updateWhere(deletedRecord)
            }
        }
    }

    override fun deleteIfExist(): Completable {
        return Completable.fromAction { blockingDeleteIfExist() }
    }

    override fun blockingDeleteIfExist() {
        try {
            blockingDelete()
        } catch (d2Error: D2Error) {
            Log.v(DataSetCompleteRegistrationObjectRepository::class.java.canonicalName, d2Error.errorDescription())
        }
    }
}
