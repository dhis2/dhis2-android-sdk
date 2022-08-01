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

internal object InvalidAttributeValueTypeConflict : TrackerImportConflictItem {

    private val errorRegex: List<Regex> = listOf(
        Regex("Value '[\\w|\\s]*' is not a valid numeric type for attribute (\\w{11})"),
        Regex("Value '[\\w|\\s]*' is not a valid boolean type for attribute (\\w{11})"),
        Regex("Value '[\\w|\\s]*' is not true \\(true-only type\\) for attribute (\\w{11})"),
        Regex("Value '[\\w|\\s]*' is not a valid date for attribute (\\w{11})"),
        Regex("Value '[\\w|\\s]*' is not a valid datetime for attribute (\\w{11})"),
        Regex("Value '[\\w|\\s]*' is not a valid username for attribute (\\w{11})"),
        Regex("Value '[\\w|\\s]*' is not the uid of a file"),
        Regex("Value '[\\w|\\s]*' is not a valid option for attribute (\\w{11}) and option set [\\w|\\s]*")
    )

    private fun description(attribute: String?) = "Invalid value type for attribute: $attribute"

    override val errorCode: String = "E1007"

    override fun matches(conflict: ImportConflict): Boolean {
        return errorRegex.any { it.matches(conflict.value()) }
    }

    override fun getTrackedEntityAttribute(conflict: ImportConflict): String? {
        return errorRegex.find { it.matches(conflict.value()) }?.find(conflict.value())?.groupValues?.getOrNull(1)
    }

    override fun getDisplayDescription(
        conflict: ImportConflict,
        context: TrackerImportConflictItemContext
    ): String {

        return getTrackedEntityAttribute(conflict)?.let { attributeUid ->
            context.attributeStore.selectByUid(attributeUid)?.let { attribute ->
                val name = attribute.displayFormName() ?: attribute.displayName() ?: attributeUid
                description(name)
            }
        } ?: conflict.value()
    }
}
