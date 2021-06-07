/*
 *  Copyright (c) 2004-2021, University of Oslo
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

package org.hisp.dhis.android.core.datavalue.internal.conflicts

import java.util.ArrayList
import java.util.Date
import java.util.regex.Pattern
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.datavalue.DataValueConflict
import org.hisp.dhis.android.core.datavalue.DataValueTableInfo
import org.hisp.dhis.android.core.imports.ImportStatus
import org.hisp.dhis.android.core.imports.internal.ImportConflict

internal object InvalidDataElementTypeConflict : DataValueImportConflictItem {

    private val regex: Regex = Regex("must match data element type: (\\w{11})")

    override fun matches(conflict: ImportConflict): Boolean {
        val patternStr = "(?<=:\\s)[a-zA-Z0-9]{11}"
        val pattern = Pattern.compile(patternStr)
        val matcher = pattern.matcher(conflict.value())
        return matcher.find()
    }

    override fun getDataValues(conflict: ImportConflict, dataValues: List<DataValue>): List<DataValueConflict> {
        val patternStr = "(?<=:\\s)[a-zA-Z0-9]{11}"
        val pattern = Pattern.compile(patternStr)
        val matcher = pattern.matcher(conflict.value())

        val foundDataValuesConflicts: MutableList<DataValueConflict> = ArrayList()
        if (matcher.find()) {
            val value = conflict.`object`()
            val dataElementUid = matcher.group(0)
            for (dataValue in dataValues) {
                if (dataValue.value() == value && dataValue.dataElement() == dataElementUid) {
                    foundDataValuesConflicts.add(getConflictBuilder(dataValue).build())
                }
            }
        }

        return foundDataValuesConflicts
    }

    private fun getConflictBuilder(dataValue: DataValue): DataValueConflict.Builder {
        return DataValueConflict.builder()
            .conflict("")
            .value(dataValue.value())
            .attributeOptionCombo(dataValue.attributeOptionCombo())
            .categoryOptionCombo(dataValue.categoryOptionCombo())
            .dataElement(dataValue.dataElement())
            .orgUnit(dataValue.organisationUnit())
            .period(dataValue.period())
            .tableReference(DataValueTableInfo.TABLE_INFO.name())
            .status(ImportStatus.WARNING)
            .errorCode("")
            .displayDescription("")
            .created(Date())
    }
}
