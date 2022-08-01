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
package org.hisp.dhis.android.core.imports.internal.conflicts

import org.hisp.dhis.android.core.imports.internal.ImportConflict

internal object InvalidDataValueConflict : TrackerImportConflictItem {

    private val errorList: List<String> = listOf(
        "value_not_numeric",
        "value_not_unit_interval",
        "value_not_percentage",
        "value_not_integer",
        "value_not_positive_integer",
        "value_not_negative_integer",
        "value_not_zero_or_positive_integer",
        "value_not_bool",
        "value_not_true_only",
        "value_not_valid_date",
        "value_not_valid_datetime",
        "value_not_coordinate",
        "value_not_url",
        "value_not_valid_file_resource_uid"
    )

    private fun description(dataElementId: String) = "Invalid value type for dataElement: $dataElementId"

    override val errorCode: String = ""

    override fun matches(conflict: ImportConflict): Boolean {
        return errorList.contains(conflict.value())
    }

    override fun getDataElement(conflict: ImportConflict): String? {
        return conflict.`object`()
    }

    override fun getDisplayDescription(
        conflict: ImportConflict,
        context: TrackerImportConflictItemContext
    ): String {

        return getDataElement(conflict)?.let { dataElementUid ->
            context.dataElementStore.selectByUid(dataElementUid)?.let { dataElement ->
                val name = dataElement.displayFormName() ?: dataElement.displayName() ?: dataElementUid
                description(name)
            }
        }
            ?: conflict.value()
    }
}
