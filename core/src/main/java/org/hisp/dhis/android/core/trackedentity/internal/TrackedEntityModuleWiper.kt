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
package org.hisp.dhis.android.core.trackedentity.internal

import org.hisp.dhis.android.core.wipe.internal.ModuleWiper
import org.hisp.dhis.android.core.wipe.internal.TableWiper
import org.hisp.dhis.android.persistence.trackedentity.ProgramTempOwnerTableInfo
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeReservedValueTableInfo
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeTableInfo
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeValueTableInfo
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityDataValueTableInfo
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityInstanceEventFilterTableInfo
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityInstanceFilterTableInfo
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityInstanceSyncTableInfo
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityInstanceTableInfo
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityTypeTableInfo
import org.koin.core.annotation.Singleton

@Singleton
internal class TrackedEntityModuleWiper(private val tableWiper: TableWiper) : ModuleWiper {
    override suspend fun wipeMetadata() {
        tableWiper.wipeTables(
            ProgramTempOwnerTableInfo.TABLE_INFO,
            TrackedEntityAttributeTableInfo.TABLE_INFO,
            TrackedEntityTypeTableInfo.TABLE_INFO,
            TrackedEntityInstanceFilterTableInfo.TABLE_INFO,
            TrackedEntityInstanceEventFilterTableInfo.TABLE_INFO,
        )
    }

    override suspend fun wipeData() {
        tableWiper.wipeTables(
            TrackedEntityInstanceTableInfo.TABLE_INFO,
            TrackedEntityInstanceSyncTableInfo.TABLE_INFO,
            TrackedEntityDataValueTableInfo.TABLE_INFO,
            TrackedEntityAttributeValueTableInfo.TABLE_INFO,
            TrackedEntityAttributeReservedValueTableInfo.TABLE_INFO,
        )
    }
}
