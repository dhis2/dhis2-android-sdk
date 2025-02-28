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

package org.hisp.dhis.android.core.settings

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyWithUidCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.settings.internal.CustomIntentStore
import org.hisp.dhis.android.core.settings.internal.CustomIntentTriggerChildrenAppender
import org.koin.core.annotation.Singleton

@Singleton
class CustomIntentCollectionRepository internal constructor(
    store: CustomIntentStore,
    databaseAdapter: DatabaseAdapter,
    scope: RepositoryScope,
) : ReadOnlyWithUidCollectionRepositoryImpl<CustomIntent, CustomIntentCollectionRepository>(
    store,
    databaseAdapter,
    childrenAppenders,
    scope.toBuilder().children(
        scope.children()
            .withChild("triggers"),
    ).build(),
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope -> CustomIntentCollectionRepository(store, databaseAdapter, s) },
) {

    fun byUid(): StringFilterConnector<CustomIntentCollectionRepository> {
        return cf.string(CustomIntentTableInfo.Columns.UID)
    }

    fun byName(): StringFilterConnector<CustomIntentCollectionRepository> {
        return cf.string(CustomIntentTableInfo.Columns.NAME)
    }

    fun byDataElement(uid: String): CustomIntentCollectionRepository {
        return cf.subQuery(CustomIntentTableInfo.Columns.UID).inLinkTable(
            CustomIntentDataElementTableInfo.TABLE_INFO.name(),
            CustomIntentDataElementTableInfo.Columns.CUSTOM_INTENT_UID,
            CustomIntentDataElementTableInfo.Columns.UID,
            listOf(uid),
        )
    }

    fun byAttribute(uid: String): CustomIntentCollectionRepository {
        return cf.subQuery(CustomIntentTableInfo.Columns.UID).inLinkTable(
            CustomIntentAttributeTableInfo.TABLE_INFO.name(),
            CustomIntentAttributeTableInfo.Columns.CUSTOM_INTENT_UID,
            CustomIntentAttributeTableInfo.Columns.UID,
            listOf(uid),
        )
    }

    internal companion object {
        val childrenAppenders: ChildrenAppenderGetter<CustomIntent> = mapOf(
            "triggers" to CustomIntentTriggerChildrenAppender::create,
        )
    }
}
