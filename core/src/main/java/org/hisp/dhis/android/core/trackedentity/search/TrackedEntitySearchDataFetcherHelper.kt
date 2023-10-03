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

package org.hisp.dhis.android.core.trackedentity.search

import dagger.Reusable
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeCollectionRepository
import org.hisp.dhis.android.core.program.trackerheaderengine.internal.TrackerHeaderEngine
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeCollectionRepository
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType
import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeAttributeCollectionRepository
import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeCollectionRepository
import javax.inject.Inject

@Reusable
internal class TrackedEntitySearchDataFetcherHelper @Inject constructor(
    private val trackerHeaderEngine: TrackerHeaderEngine,
    private val trackedEntityTypeAttributeCollectionRepository: TrackedEntityTypeAttributeCollectionRepository,
    private val programTrackedEntityAttributeCollectionRepository: ProgramTrackedEntityAttributeCollectionRepository,
    private val trackedEntityAttributeCollectionRepository: TrackedEntityAttributeCollectionRepository,
    private val trackedEntityTypeCollectionRepository: TrackedEntityTypeCollectionRepository,
) {
    fun getHeaderExpression(program: String?): String? {
        return program?.let {
            // https://dhis2.atlassian.net/browse/ANDROSDK-1728
            null
        }
    }

    fun evaluateHeaderExpression(expression: String, trackedEntityInstance: TrackedEntityInstance): String? {
        return trackerHeaderEngine.getTrackedEntityHeader(
            expression = expression,
            attributeValues = trackedEntityInstance.trackedEntityAttributeValues() ?: emptyList(),
        )
    }

    fun getScopeAttributes(program: String?, trackedEntityType: String?): List<SimpleTrackedEntityAttribute> {
        return if (program != null) {
            val programAttributes = programTrackedEntityAttributeCollectionRepository
                .byProgram().eq(program)
                .orderBySortOrder(RepositoryScope.OrderByDirection.ASC)
                .blockingGet()

            val attributes = trackedEntityAttributeCollectionRepository
                .byUid().`in`(programAttributes.mapNotNull { it.trackedEntityAttribute()?.uid() })
                .blockingGet()

            programAttributes.mapNotNull { programAttribute ->
                val attributeUid = programAttribute.trackedEntityAttribute()!!.uid()
                attributes.find { it.uid() == attributeUid }?.let { attribute ->
                    getSimpleTrackedEntityAttribute(
                        attribute = attribute,
                        displayInList = programAttribute.displayInList(),
                    )
                }
            }
        } else if (trackedEntityType != null) {
            val typeAttributes = trackedEntityTypeAttributeCollectionRepository
                .byTrackedEntityTypeUid().eq(trackedEntityType)
                .blockingGet()

            val attributes = trackedEntityAttributeCollectionRepository
                .byUid().`in`(typeAttributes.mapNotNull { it.trackedEntityAttribute()?.uid() })
                .blockingGet()

            typeAttributes.mapNotNull { typeAttribute ->
                val attributeUid = typeAttribute.trackedEntityAttribute()!!.uid()
                attributes.find { it.uid() == attributeUid }?.let { attribute ->
                    getSimpleTrackedEntityAttribute(
                        attribute = attribute,
                        displayInList = typeAttribute.displayInList(),
                    )
                }
            }
        } else {
            val attributes = trackedEntityAttributeCollectionRepository
                .blockingGet()

            attributes.map { attribute ->
                getSimpleTrackedEntityAttribute(
                    attribute = attribute,
                    displayInList = attribute.displayInListNoProgram(),
                )
            }
        }
    }

    private fun getSimpleTrackedEntityAttribute(
        attribute: TrackedEntityAttribute,
        displayInList: Boolean?,
    ): SimpleTrackedEntityAttribute {
        return SimpleTrackedEntityAttribute(
            attribute = attribute.uid(),
            displayName = attribute.displayName() ?: attribute.name() ?: attribute.uid(),
            displayFormName = attribute.displayFormName(),
            displayInList = displayInList ?: false,
            valueType = attribute.valueType()!!,
            optionSet = attribute.optionSet()?.uid(),
        )
    }

    fun getTeType(uid: String): TrackedEntityType? {
        return trackedEntityTypeCollectionRepository.uid(uid).blockingGet()
    }
}

internal data class SimpleTrackedEntityAttribute(
    val attribute: String,
    val displayName: String,
    val displayFormName: String?,
    val displayInList: Boolean,
    val valueType: ValueType,
    val optionSet: String?,
)
