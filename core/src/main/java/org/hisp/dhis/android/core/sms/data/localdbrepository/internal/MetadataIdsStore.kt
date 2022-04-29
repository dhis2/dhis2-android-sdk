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

import dagger.Reusable
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.smscompression.SMSConsts
import org.hisp.dhis.smscompression.models.SMSMetadata

@Reusable
internal class MetadataIdsStore @Inject constructor(
    private val smsMetadataIdsStore: ObjectWithoutUidStore<SMSMetadataId>,
    private val smsConfigStore: SMSConfigStore
) {
    fun getMetadataIds(): Single<SMSMetadata> {
        return Single.fromCallable {
            val lastSync = smsConfigStore.get(SMSConfigKey.METADATA_SYNC_DATE)
            val metadataIdList = smsMetadataIdsStore.selectAll()

            val byType = metadataIdList
                .groupBy { it.type() }
                .mapValues { (_, list) -> list.map { SMSMetadata.ID(it.uid()) } }

            SMSMetadata().apply {
                lastSyncDate = lastSync?.let { DateUtils.DATE_FORMAT.parse(it) }

                users = getIds(byType, SMSConsts.MetadataType.USER)
                trackedEntityTypes = getIds(byType, SMSConsts.MetadataType.TRACKED_ENTITY_TYPE)
                trackedEntityAttributes = getIds(byType, SMSConsts.MetadataType.TRACKED_ENTITY_ATTRIBUTE)
                programs = getIds(byType, SMSConsts.MetadataType.PROGRAM)
                organisationUnits = getIds(byType, SMSConsts.MetadataType.ORGANISATION_UNIT)
                dataElements = getIds(byType, SMSConsts.MetadataType.DATA_ELEMENT)
                categoryOptionCombos = getIds(byType, SMSConsts.MetadataType.CATEGORY_OPTION_COMBO)
                dataSets = getIds(byType, SMSConsts.MetadataType.DATASET)
                programStages = getIds(byType, SMSConsts.MetadataType.PROGRAM_STAGE)
                events = getIds(byType, SMSConsts.MetadataType.EVENT)
                enrollments = getIds(byType, SMSConsts.MetadataType.ENROLLMENT)
                trackedEntityInstances = getIds(byType, SMSConsts.MetadataType.TRACKED_ENTITY_INSTANCE)
                relationships = getIds(byType, SMSConsts.MetadataType.RELATIONSHIP)
                relationshipTypes = getIds(byType, SMSConsts.MetadataType.RELATIONSHIP_TYPE)
            }
        }
    }

    private fun getIds(
        map: Map<SMSConsts.MetadataType, List<SMSMetadata.ID>>,
        type: SMSConsts.MetadataType
    ): List<SMSMetadata.ID> {
        return map[type] ?: emptyList()
    }

    fun setMetadataIds(metadata: SMSMetadata?): Completable {
        return Completable.fromAction {
            metadata?.let {
                it.lastSyncDate?.let { date ->
                    smsConfigStore.set(SMSConfigKey.METADATA_SYNC_DATE, DateUtils.DATE_FORMAT.format(date))
                }
                val metadataIds =
                    buildFor(it, SMSConsts.MetadataType.USER) +
                        buildFor(it, SMSConsts.MetadataType.TRACKED_ENTITY_TYPE) +
                        buildFor(it, SMSConsts.MetadataType.TRACKED_ENTITY_ATTRIBUTE) +
                        buildFor(it, SMSConsts.MetadataType.PROGRAM) +
                        buildFor(it, SMSConsts.MetadataType.ORGANISATION_UNIT) +
                        buildFor(it, SMSConsts.MetadataType.DATA_ELEMENT) +
                        buildFor(it, SMSConsts.MetadataType.CATEGORY_OPTION_COMBO) +
                        buildFor(it, SMSConsts.MetadataType.DATASET) +
                        buildFor(it, SMSConsts.MetadataType.PROGRAM_STAGE) +
                        buildFor(it, SMSConsts.MetadataType.EVENT) +
                        buildFor(it, SMSConsts.MetadataType.ENROLLMENT) +
                        buildFor(it, SMSConsts.MetadataType.TRACKED_ENTITY_INSTANCE) +
                        buildFor(it, SMSConsts.MetadataType.RELATIONSHIP) +
                        buildFor(it, SMSConsts.MetadataType.RELATIONSHIP_TYPE)

                smsMetadataIdsStore.delete()
                metadataIds.forEach { id -> smsMetadataIdsStore.insert(id) }
            }
        }
    }

    private fun buildFor(metadata: SMSMetadata, type: SMSConsts.MetadataType): List<SMSMetadataId> {
        return metadata.getType(type).map { SMSMetadataId.builder().type(type).uid(it).build() }
    }

    fun clear(): Completable {
        return Completable.fromAction {
            smsMetadataIdsStore.delete()
            smsConfigStore.delete(SMSConfigKey.METADATA_SYNC_DATE)
        }
    }
}
