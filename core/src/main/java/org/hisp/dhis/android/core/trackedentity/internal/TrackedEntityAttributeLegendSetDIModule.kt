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

package org.hisp.dhis.android.core.trackedentity.internal

import dagger.Module
import dagger.Provides
import dagger.Reusable
import java.util.Collections
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.stores.internal.LinkStore
import org.hisp.dhis.android.core.arch.handlers.internal.OrderedLinkHandler
import org.hisp.dhis.android.core.arch.handlers.internal.OrderedLinkHandlerImpl
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeLegendSetLink
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeLegendSetLinkStore

@Module
internal class TrackedEntityAttributeLegendSetDIModule {
    @Provides
    @Reusable
    fun store(databaseAdapter: DatabaseAdapter?): LinkStore<TrackedEntityAttributeLegendSetLink> {
        return TrackedEntityAttributeLegendSetLinkStore.create(databaseAdapter)
    }

    @Provides
    @Reusable
    fun handler(
        store: LinkStore<TrackedEntityAttributeLegendSetLink>
    ): OrderedLinkHandler<ObjectWithUid?, TrackedEntityAttributeLegendSetLink> {
        return OrderedLinkHandlerImpl(store)
    }

    @Provides
    @Reusable
    fun childrenAppenders(databaseAdapter: DatabaseAdapter): Map<String, ChildrenAppender<TrackedEntityAttribute>> {
        return Collections.singletonMap(
            TrackedEntityAttributeFields.LEGEND_SETS,
            TrackedEntityAttributeLegendSetChildrenAppender.create(databaseAdapter)
        )
    }
}
