/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.datavalue

import org.hisp.dhis.android.core.category.CategoryOptionComboTableInfo
import org.hisp.dhis.android.core.dataelement.DataElementTableInfo
import org.hisp.dhis.android.core.dataset.DataSetDataElementLinkTableInfo
import org.hisp.dhis.android.core.dataset.DataSetElementLinkTableInfo

internal object DataValueByDataSetQueryHelper {

    private const val DSE_ALIAS = "dse"
    private const val DE_ALIAS = "de"
    private const val COC_ALIAS = "coc"

    private const val DSE_CATEGORYCOMBO = "$DSE_ALIAS.${DataSetDataElementLinkTableInfo.Columns.CATEGORY_COMBO}"
    private const val DSE_DATASET = "$DSE_ALIAS.${DataSetDataElementLinkTableInfo.Columns.DATA_SET}"
    private const val DE_CATEGORYCOMBO = "$DE_ALIAS.${DataElementTableInfo.Columns.CATEGORY_COMBO}"

    private const val DE_UID = "$DSE_ALIAS.${DataSetElementLinkTableInfo.Columns.DATA_ELEMENT}"
    private const val COC_UID = "$COC_ALIAS.${CategoryOptionComboTableInfo.Columns.UID}"

    fun whereClause(dataSetUid: String): String = """
        (${DataValueTableInfo.Columns.DATA_ELEMENT}, ${DataValueTableInfo.Columns.CATEGORY_OPTION_COMBO}) 
        IN
        (SELECT $DE_UID, $COC_UID 
            FROM ${DataSetDataElementLinkTableInfo.TABLE_INFO.name()} $DSE_ALIAS
            INNER JOIN ${DataElementTableInfo.TABLE_INFO.name()} $DE_ALIAS
                ON ${DataElementTableInfo.Columns.UID} = ${DataSetDataElementLinkTableInfo.Columns.DATA_ELEMENT}
            INNER JOIN ${CategoryOptionComboTableInfo.TABLE_INFO.name()} $COC_ALIAS
                ON ${CategoryOptionComboTableInfo.Columns.CATEGORY_COMBO} = 
                    (CASE WHEN $DSE_CATEGORYCOMBO IS NOT NULL THEN $DSE_CATEGORYCOMBO ELSE $DE_CATEGORYCOMBO END)
            WHERE $DSE_DATASET = '${dataSetUid}'
        )
        """.trimIndent().replace("\n", " ")
}