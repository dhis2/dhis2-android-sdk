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
package org.hisp.dhis.android.core.relationship.internal

import org.hisp.dhis.android.core.wipe.internal.ModuleWiper
import org.hisp.dhis.android.core.wipe.internal.TableWiper
import org.hisp.dhis.android.persistence.relationship.RelationshipConstraintTableInfo
import org.hisp.dhis.android.persistence.relationship.RelationshipItemTableInfo
import org.hisp.dhis.android.persistence.relationship.RelationshipTableInfo
import org.hisp.dhis.android.persistence.relationship.RelationshipTypeTableInfo
import org.koin.core.annotation.Singleton

@Singleton
internal class RelationshipModuleWiper(
    private val tableWiper: TableWiper,
) : ModuleWiper {
    override suspend fun wipeMetadata() {
        tableWiper.wipeTables(
            RelationshipTypeTableInfo.TABLE_INFO,
            RelationshipConstraintTableInfo.TABLE_INFO,
        )
    }

    override suspend fun wipeData() {
        tableWiper.wipeTables(
            RelationshipTableInfo.TABLE_INFO,
            RelationshipItemTableInfo.TABLE_INFO,
        )
    }
}
