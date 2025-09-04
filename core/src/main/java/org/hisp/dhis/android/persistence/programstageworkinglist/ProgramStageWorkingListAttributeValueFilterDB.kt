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

package org.hisp.dhis.android.persistence.programstageworkinglist

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.programstageworkinglist.ProgramStageWorkingListAttributeValueFilter
import org.hisp.dhis.android.persistence.attribute.AttributeDB
import org.hisp.dhis.android.persistence.common.DateFilterPeriodDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.FilterOperatorsDB
import org.hisp.dhis.android.persistence.common.StringSetDB
import org.hisp.dhis.android.persistence.common.applyFilterOperatorsFields
import org.hisp.dhis.android.persistence.common.toDB

@Entity(
    tableName = "ProgramStageWorkingListAttributeValueFilter",
    foreignKeys = [
        ForeignKey(
            entity = ProgramStageWorkingListDB::class,
            parentColumns = ["uid"],
            childColumns = ["programStageWorkingList"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = AttributeDB::class, //TODO CHECK IF THIS IS CORRECT OR SHOULD BE TrackedEntityAttributeDB
            parentColumns = ["uid"],
            childColumns = ["attribute"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    primaryKeys = ["programStageWorkingList", "attribute"],
)
internal data class ProgramStageWorkingListAttributeValueFilterDB(
    val programStageWorkingList: String,
    val attribute: String,
    val sw: String?,
    val ew: String?,
    override val le: String?,
    override val ge: String?,
    override val gt: String?,
    override val lt: String?,
    override val eq: String?,
    override val inProperty: StringSetDB?,
    override val like: String?,
    override val dateFilter: DateFilterPeriodDB?,
) : EntityDB<ProgramStageWorkingListAttributeValueFilter>, FilterOperatorsDB {
    override fun toDomain(): ProgramStageWorkingListAttributeValueFilter {
        return ProgramStageWorkingListAttributeValueFilter.builder()
            .applyFilterOperatorsFields(this@ProgramStageWorkingListAttributeValueFilterDB)
            .programStageWorkingList(programStageWorkingList)
            .attribute(attribute)
            .sw(sw)
            .ew(ew)
            .build()
    }
}

internal fun ProgramStageWorkingListAttributeValueFilter.toDB(): ProgramStageWorkingListAttributeValueFilterDB {
    return ProgramStageWorkingListAttributeValueFilterDB(
        programStageWorkingList = programStageWorkingList(),
        attribute = attribute(),
        sw = sw(),
        ew = ew(),
        le = le(),
        ge = ge(),
        gt = gt(),
        lt = lt(),
        eq = eq(),
        inProperty = `in`()?.toDB(),
        like = like(),
        dateFilter = dateFilter()?.toDB(),
    )
}
