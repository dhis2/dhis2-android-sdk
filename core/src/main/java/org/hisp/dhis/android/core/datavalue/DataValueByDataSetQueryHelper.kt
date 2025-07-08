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
package org.hisp.dhis.android.core.datavalue

import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator
import org.hisp.dhis.android.persistence.category.CategoryOptionComboTableInfo
import org.hisp.dhis.android.persistence.dataelement.DataElementTableInfo
import org.hisp.dhis.android.persistence.dataset.DataSetDataElementLinkTableInfo
import org.hisp.dhis.android.persistence.dataset.DataSetOrganisationUnitLinkTableInfo
import org.hisp.dhis.android.persistence.dataset.DataSetTableInfo
import org.hisp.dhis.android.persistence.datavalue.DataValueConflictTableInfo
import org.hisp.dhis.android.persistence.datavalue.DataValueTableInfo

internal object DataValueByDataSetQueryHelper {

    private const val DSE_ALIAS = "dse"
    private const val DE_ALIAS = "de"
    private const val DS_ALIAS = "ds"
    private const val COC_ALIAS = "coc"
    private const val AOC_ALIAS = "aoc"
    private const val DSOU_ALIAS = "dsou"

    private const val DSE_DATASET = "$DSE_ALIAS.${DataSetDataElementLinkTableInfo.Columns.DATA_SET}"
    private const val DSE_DATAELEMENT = "$DSE_ALIAS.${DataSetDataElementLinkTableInfo.Columns.DATA_ELEMENT}"
    private const val DSE_CATEGORYCOMBO = "$DSE_ALIAS.${DataSetDataElementLinkTableInfo.Columns.CATEGORY_COMBO}"

    private const val DE_UID = "$DE_ALIAS.${DataElementTableInfo.Columns.UID}"
    private const val DE_CATEGORYCOMBO = "$DE_ALIAS.${DataElementTableInfo.Columns.CATEGORY_COMBO}"

    private const val DS_UID = "$DS_ALIAS.${DataSetTableInfo.Columns.UID}"
    private const val DS_CATEGORYCOMBO = "$DS_ALIAS.${DataSetTableInfo.Columns.CATEGORY_COMBO}"

    private const val COC_UID = "$COC_ALIAS.${CategoryOptionComboTableInfo.Columns.UID}"
    private const val COC_CATEGORYCOMBO = "$COC_ALIAS.${CategoryOptionComboTableInfo.Columns.CATEGORY_COMBO}"

    private const val AOC_UID = "$AOC_ALIAS.${CategoryOptionComboTableInfo.Columns.UID}"
    private const val AOC_CATEGORYCOMBO = "$AOC_ALIAS.${CategoryOptionComboTableInfo.Columns.CATEGORY_COMBO}"

    private const val DSOU_DATASET = "$DSOU_ALIAS.${DataSetOrganisationUnitLinkTableInfo.Columns.DATA_SET}"
    private const val DSOU_ORGUNIT = "$DSOU_ALIAS.${DataSetOrganisationUnitLinkTableInfo.Columns.ORGANISATION_UNIT}"

    val dataValueKey = buildKey(
        DataValueTableInfo.Columns.DATA_ELEMENT,
        DataValueTableInfo.Columns.CATEGORY_OPTION_COMBO,
        DataValueTableInfo.Columns.ATTRIBUTE_OPTION_COMBO,
        DataValueTableInfo.Columns.ORGANISATION_UNIT,
    )

    val dataValueConflictKey = buildKey(
        DataValueConflictTableInfo.Columns.DATA_ELEMENT,
        DataValueConflictTableInfo.Columns.CATEGORY_OPTION_COMBO,
        DataValueConflictTableInfo.Columns.ATTRIBUTE_OPTION_COMBO,
        DataValueConflictTableInfo.Columns.ORG_UNIT,
    )

    val operator = FilterItemOperator.IN

    fun whereClause(dataSetUid: String): String =
        """SELECT ${buildKey(DSE_DATAELEMENT, COC_UID, AOC_UID, DSOU_ORGUNIT)} 
            FROM ${DataSetDataElementLinkTableInfo.TABLE_INFO.name()} $DSE_ALIAS
            INNER JOIN ${DataElementTableInfo.TABLE_INFO.name()} $DE_ALIAS
                ON $DE_UID = $DSE_DATAELEMENT
            INNER JOIN ${DataSetTableInfo.TABLE_INFO.name()} $DS_ALIAS
                ON $DS_UID = $DSE_DATASET
            INNER JOIN ${CategoryOptionComboTableInfo.TABLE_INFO.name()} $COC_ALIAS
                ON $COC_CATEGORYCOMBO = 
                    (CASE WHEN $DSE_CATEGORYCOMBO IS NOT NULL THEN $DSE_CATEGORYCOMBO ELSE $DE_CATEGORYCOMBO END)
            INNER JOIN ${CategoryOptionComboTableInfo.TABLE_INFO.name()} $AOC_ALIAS
                ON $AOC_CATEGORYCOMBO = $DS_CATEGORYCOMBO
            INNER JOIN ${DataSetOrganisationUnitLinkTableInfo.TABLE_INFO.name()} $DSOU_ALIAS
                ON $DS_UID = $DSOU_DATASET
            WHERE $DSE_DATASET = '$dataSetUid'
        """.trimIndent().replace("\n", " ")

    /*
     The initial approach was to use row values instead of an auxiliary key. The SQL would be something like:

     WHERE (dataElement, categoryOptionCombo) IN (SELECT dse.dataElement, coc.uid FROM ...)

     But row values were added to SQL in version 3.15.0, which means Android 8.0 (API 26)
     https://www.sqlite.org/rowvalue.html

     This solution using an auxiliary key is a workaround for that.
     */
    private fun buildKey(
        dataElementTag: String,
        categoryOptionComboTag: String,
        attributeOptionComboTag: String,
        organisationUnitTag: String,
    ): String {
        return "$dataElementTag || '.' || $categoryOptionComboTag || '.' || $attributeOptionComboTag || " +
            "'.' || $organisationUnitTag"
    }
}
