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
package org.hisp.dhis.android.core.trackedentity

import io.reactivex.Single
import org.hisp.dhis.android.core.arch.helpers.UidsHelper
import org.hisp.dhis.android.core.common.Unit
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.core.fileresource.FileResourceCollectionRepository
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeCollectionRepository
import org.koin.core.annotation.Singleton
import java.io.File

@Singleton
class TrackedEntityInstanceService(
    private val trackedEntityAttributeRepository: TrackedEntityAttributeCollectionRepository,
    private val trackedEntityAttributeValueRepository: TrackedEntityAttributeValueCollectionRepository,
    private val programTrackedEntityAttributeRepository: ProgramTrackedEntityAttributeCollectionRepository,
    private val fileResourceCollectionRepository: FileResourceCollectionRepository,
) {
    /**
     * Inherit the tracked entity attribute values from one TEI to another. It only inherits attributes that are marked
     * as "inherited=true" and that belong to program passed as parameter. This method is useful when creating new
     * relationships. Inherited values are persisted in database. Important: this is a blocking method and it should
     * not be executed in the main thread. Consider the asynchronous version [inheritAttributes].
     *
     * @param fromTeiUid TrackedEntityInstance to inherit values from.
     * @param toTeiUid   TrackedEntityInstance that receive the inherited values.
     * @param programUid Only attributes associated to this program will be inherited.
     * @return Unit
     */
    @Throws(D2Error::class)
    fun blockingInheritAttributes(fromTeiUid: String, toTeiUid: String, programUid: String): Unit {
        val programAttributes = programTrackedEntityAttributeRepository
            .byProgram().eq(programUid)
            .blockingGet()

        val attributeUids = programAttributes.mapNotNull { UidsHelper.getUidOrNull(it.trackedEntityAttribute()) }

        val inheritableAttributeUids = trackedEntityAttributeRepository
            .byUid().`in`(attributeUids)
            .byInherit().isTrue
            .blockingGetUids()

        if (inheritableAttributeUids.isNotEmpty()) {
            val fromTeiAttributes = trackedEntityAttributeValueRepository
                .byTrackedEntityInstance().eq(fromTeiUid)
                .byTrackedEntityAttribute().`in`(inheritableAttributeUids)
                .blockingGet()

            for (attributeValue in fromTeiAttributes) {
                inheritAttribute(attributeValue, toTeiUid)
            }
        }

        return Unit()
    }

    @Throws(D2Error::class)
    private fun inheritAttribute(attributeValue: TrackedEntityAttributeValue, toTeiUid: String) {
        val attribute = trackedEntityAttributeRepository.uid(attributeValue.trackedEntityAttribute()).blockingGet()

        if (attribute?.valueType() == ValueType.IMAGE || attribute?.valueType() == ValueType.FILE_RESOURCE) {
            val file = File(
                fileResourceCollectionRepository.uid(attributeValue.value()).blockingGet()!!.path()!!,
            )

            val newFileResourceId = fileResourceCollectionRepository.blockingAdd(file)

            trackedEntityAttributeValueRepository
                .value(attributeValue.trackedEntityAttribute(), toTeiUid)
                .blockingSet(newFileResourceId)
        } else {
            trackedEntityAttributeValueRepository
                .value(attributeValue.trackedEntityAttribute(), toTeiUid)
                .blockingSet(attributeValue.value())
        }
    }

    /**
     * Inherit the tracked entity attribute values from one TEI to another. It only inherits attributes that are marked
     * as "inherited=true" and that belong to program passed as parameter. This method is useful when creating new
     * relationships. Inherited values are persisted in database.
     *
     * @param fromTeiUid TrackedEntityInstance to inherit values from.
     * @param toTeiUid   TrackedEntityInstance that receive the inherited values.
     * @param programUid Only attributes associated to this program will be inherited.
     * @return Unit
     */
    fun inheritAttributes(fromTeiUid: String, toTeiUid: String, programUid: String): Single<Unit> {
        return Single.fromCallable { blockingInheritAttributes(fromTeiUid, toTeiUid, programUid) }
    }
}
