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

package org.hisp.dhis.android.persistence.dataelement

import org.hisp.dhis.android.core.arch.db.stores.projections.internal.LinkTableChildProjection
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.dataelement.internal.DataElementStore
import org.hisp.dhis.android.persistence.common.querybuilders.SQLStatementBuilderImpl
import org.hisp.dhis.android.persistence.common.stores.IdentifiableObjectStoreImpl
import org.hisp.dhis.android.persistence.dataset.SectionDataElementLinkTableInfo
import org.hisp.dhis.android.persistence.program.ProgramStageSectionDataElementLinkTableInfo

internal class DataElementStoreImpl(
    val dao: DataElementDao,
) : DataElementStore, IdentifiableObjectStoreImpl<DataElement, DataElementDB>(
    dao,
    DataElement::toDB,
    SQLStatementBuilderImpl(DataElementTableInfo.TABLE_INFO),
) {
    override suspend fun getForSection(sectionUid: String): List<DataElement> {
        val projection = LinkTableChildProjection(
            DataElementTableInfo.TABLE_INFO,
            SectionDataElementLinkTableInfo.Columns.SECTION,
            SectionDataElementLinkTableInfo.Columns.DATA_ELEMENT,
        )
        val sectionSqlBuilder = SQLStatementBuilderImpl(
            SectionDataElementLinkTableInfo.TABLE_INFO,
        )
        val query = sectionSqlBuilder.selectChildrenWithLinkTable(
            projection,
            sectionUid,
            null,
        )
        return selectRawQuery(query)
    }

    override suspend fun getForProgramStageSection(programStageSection: String): List<DataElement> {
        val projection = LinkTableChildProjection(
            DataElementTableInfo.TABLE_INFO,
            ProgramStageSectionDataElementLinkTableInfo.Columns.PROGRAM_STAGE_SECTION,
            ProgramStageSectionDataElementLinkTableInfo.Columns.DATA_ELEMENT,
        )
        val sectionSqlBuilder = SQLStatementBuilderImpl(
            ProgramStageSectionDataElementLinkTableInfo.TABLE_INFO,
        )
        val query = sectionSqlBuilder.selectChildrenWithLinkTable(
            projection,
            programStageSection,
            null,
        )
        return selectRawQuery(query)
    }
}
