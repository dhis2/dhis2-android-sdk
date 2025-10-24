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
package org.hisp.dhis.android.core.datavalue

import io.reactivex.Completable
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.rxCompletable
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.`object`.ReadWriteValueObjectRepository
import org.hisp.dhis.android.core.arch.repositories.`object`.internal.ObjectRepositoryFactory
import org.hisp.dhis.android.core.arch.repositories.`object`.internal.ReadWriteWithValueObjectRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.datavalue.internal.DataValueStore
import org.hisp.dhis.android.core.maintenance.D2Error
import java.util.Date

class DataValueObjectRepository internal constructor(
    store: DataValueStore,
    childrenAppenders: ChildrenAppenderGetter<DataValue>,
    scope: RepositoryScope,
    private val period: String,
    private val organisationUnit: String,
    private val dataElement: String,
    private val categoryOptionCombo: String,
    private val attributeOptionCombo: String,
) : ReadWriteWithValueObjectRepositoryImpl<DataValue, DataValueObjectRepository>(
    store,
    childrenAppenders,
    scope,
    ObjectRepositoryFactory { s: RepositoryScope ->
        DataValueObjectRepository(
            store,
            childrenAppenders,
            s,
            period,
            organisationUnit,
            dataElement,
            categoryOptionCombo,
            attributeOptionCombo,
        )
    },
),
    ReadWriteValueObjectRepository<DataValue> {
    override fun set(value: String?): Completable {
        return Completable.fromAction { blockingSet(value) }
    }

    @Throws(D2Error::class)
    override fun blockingSet(value: String?) {
        updateIfChanged(value, { it?.value() }) { dataValue: DataValue?, newValue ->
            setBuilder(dataValue).value(newValue).deleted(false).build()
        }
    }

    @Throws(D2Error::class)
    fun setFollowUp(followUp: Boolean) {
        updateIfChanged(followUp, { it?.followUp() }) { dataValue: DataValue?, newValue ->
            setBuilder(dataValue).followUp(newValue).deleted(false).build()
        }
    }

    @Throws(D2Error::class)
    fun setComment(comment: String?) {
        updateIfChanged(comment, { it?.comment() }) { dataValue: DataValue?, newValue ->
            setBuilder(dataValue).comment(newValue).deleted(false).build()
        }
    }

    override fun delete(): Completable {
        return rxCompletable { deleteInternal() }
    }

    @Throws(D2Error::class)
    override fun blockingDelete() {
        runBlocking { deleteInternal() }
    }

    @Throws(D2Error::class)
    override suspend fun deleteInternal() {
        getWithoutChildrenInternal()?.let { dataValue ->
            if (dataValue.syncState() === State.TO_POST) {
                super.deleteInternal(dataValue)
            } else {
                setObject(dataValue.toBuilder().deleted(true).syncState(State.TO_UPDATE).build())
            }
        }
    }

    private fun setBuilder(dataValue: DataValue? = null): DataValue.Builder {
        val date = Date()
        return if (dataValue != null) {
            val state =
                if (dataValue.syncState() === State.TO_POST) State.TO_POST else State.TO_UPDATE
            dataValue.toBuilder()
                .syncState(state)
                .lastUpdated(date)
        } else {
            DataValue.builder()
                .syncState(State.TO_POST)
                .created(date)
                .lastUpdated(date)
                .followUp(java.lang.Boolean.FALSE)
                .period(period)
                .organisationUnit(organisationUnit)
                .dataElement(dataElement)
                .categoryOptionCombo(categoryOptionCombo)
                .attributeOptionCombo(attributeOptionCombo)
                .deleted(false)
        }
    }
}
