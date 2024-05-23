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
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
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
    databaseAdapter: DatabaseAdapter,
    childrenAppenders: ChildrenAppenderGetter<DataValue>,
    scope: RepositoryScope,
    private val period: String,
    private val organisationUnit: String,
    private val dataElement: String,
    private val categoryOptionCombo: String,
    private val attributeOptionCombo: String,
) : ReadWriteWithValueObjectRepositoryImpl<DataValue, DataValueObjectRepository>(
    store,
    databaseAdapter,
    childrenAppenders,
    scope,
    ObjectRepositoryFactory { s: RepositoryScope ->
        DataValueObjectRepository(
            store,
            databaseAdapter,
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
        shouldUpdateObject(setBuilder().build().value(), value) {
            val objectWithValue = setBuilder().value(value).deleted(false).build()
            setObject(objectWithValue)
        }
    }

    @Throws(D2Error::class)
    fun setFollowUp(followUp: Boolean) {
        shouldUpdateObject(setBuilder().build().followUp(), followUp) {
            setObject(setBuilder().followUp(followUp).build())
        }
    }

    @Throws(D2Error::class)
    fun setComment(comment: String?) {
        shouldUpdateObject(setBuilder().build().comment(), comment) {
            setObject(setBuilder().comment(comment).build())
        }
    }

    override fun delete(): Completable {
        return Completable.fromAction { blockingDelete() }
    }

    @Throws(D2Error::class)
    override fun blockingDelete() {
        blockingGetWithoutChildren()?.let { dataValue ->
            if (dataValue.syncState() === State.TO_POST) {
                super.delete(dataValue)
            } else {
                setObject(dataValue.toBuilder().deleted(true).syncState(State.TO_UPDATE).build())
            }
        }
    }

    private fun setBuilder(): DataValue.Builder {
        val date = Date()
        val dataValue = blockingGetWithoutChildren()

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
