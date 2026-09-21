/*
 *  Copyright (c) 2004-2026, University of Oslo
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
package org.hisp.dhis.android.core.datavalue.internal

import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper.commaAndSpaceSeparatedCollectionValues
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper.withSingleQuotationMarksArray
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.persistence.category.CategoryOptionComboTableInfo
import org.hisp.dhis.android.persistence.dataelement.DataElementTableInfo
import org.hisp.dhis.android.persistence.dataset.DataSetDataElementLinkTableInfo
import org.hisp.dhis.android.persistence.dataset.DataSetOrganisationUnitLinkTableInfo
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitTableInfo
import org.hisp.dhis.android.persistence.user.UserOrganisationUnitTableInfo

/**
 * Queries used to estimate the volume of a dataValueSets download before issuing it.
 *
 * The joins mirror the ones in [DataValueByDataSetQueryHelper], but each dimension is counted
 * separately: the estimation needs the factors apart from each other in order to size every
 * partition, which a single count over the whole cartesian product cannot provide.
 */
internal object DataValueDownloadEstimationQueryHelper {

    const val DATA_SET_COLUMN = "dataSet"
    const val COUNT_COLUMN = "count"
    const val UID_COLUMN = "uid"
    const val PARENT_COLUMN = "parent"
    const val ORGANISATION_UNIT_COLUMN = "organisationUnit"

    private const val DSE_ALIAS = "dse"
    private const val DE_ALIAS = "de"
    private const val COC_ALIAS = "coc"
    private const val DSOU_ALIAS = "dsou"
    private const val OU_ALIAS = "ou"
    private const val UOU_ALIAS = "uou"

    private const val DSE_DATASET = "$DSE_ALIAS.${DataSetDataElementLinkTableInfo.Columns.DATA_SET}"
    private const val DSE_DATAELEMENT = "$DSE_ALIAS.${DataSetDataElementLinkTableInfo.Columns.DATA_ELEMENT}"
    private const val DSE_CATEGORYCOMBO = "$DSE_ALIAS.${DataSetDataElementLinkTableInfo.Columns.CATEGORY_COMBO}"

    private const val DE_UID = "$DE_ALIAS.${DataElementTableInfo.Columns.UID}"
    private const val DE_CATEGORYCOMBO = "$DE_ALIAS.${DataElementTableInfo.Columns.CATEGORY_COMBO}"

    private const val COC_CATEGORYCOMBO = "$COC_ALIAS.${CategoryOptionComboTableInfo.Columns.CATEGORY_COMBO}"

    private const val DSOU_DATASET = "$DSOU_ALIAS.${DataSetOrganisationUnitLinkTableInfo.Columns.DATA_SET}"
    private const val DSOU_ORGUNIT = "$DSOU_ALIAS.${DataSetOrganisationUnitLinkTableInfo.Columns.ORGANISATION_UNIT}"

    private const val OU_UID = "$OU_ALIAS.${OrganisationUnitTableInfo.Columns.UID}"
    private const val OU_PARENT = "$OU_ALIAS.${OrganisationUnitTableInfo.Columns.PARENT}"

    private const val UOU_ORGUNIT = "$UOU_ALIAS.${UserOrganisationUnitTableInfo.Columns.ORGANISATION_UNIT}"
    private const val UOU_SCOPE = "$UOU_ALIAS.${UserOrganisationUnitTableInfo.Columns.ORGANISATION_UNIT_SCOPE}"

    private val captureScope = "'${OrganisationUnit.Scope.SCOPE_DATA_CAPTURE.name}'"

    /**
     * Number of category option combos a single (period, orgunit) cell can hold in each data set,
     * that is, the sum over its data set elements of the option combos of its category combo,
     * falling back to the data element's one when the data set element does not override it.
     */
    fun categoryOptionComboCountByDataSetQuery(dataSetUids: Collection<String>): String =
        """SELECT $DSE_DATASET AS $DATA_SET_COLUMN, COUNT(*) AS $COUNT_COLUMN
            FROM ${DataSetDataElementLinkTableInfo.TABLE_INFO.name()} $DSE_ALIAS
            INNER JOIN ${DataElementTableInfo.TABLE_INFO.name()} $DE_ALIAS
                ON $DE_UID = $DSE_DATAELEMENT
            INNER JOIN ${CategoryOptionComboTableInfo.TABLE_INFO.name()} $COC_ALIAS
                ON $COC_CATEGORYCOMBO = COALESCE($DSE_CATEGORYCOMBO, $DE_CATEGORYCOMBO)
            WHERE $DSE_DATASET IN (${inValues(dataSetUids)})
            GROUP BY $DSE_DATASET
        """.singleLine()

    /**
     * Number of organisation units assigned to each data set.
     *
     * The capture scope is deliberately not applied here. The estimate only has to bound the
     * request, and an assignment the capture scope does not reach simply contributes nothing to
     * the volume, so counting it can only overestimate, which is the safe direction. Narrowing the
     * count instead risks reading an incomplete assignment table as "nothing to download".
     */
    fun assignedOrgUnitCountByDataSetQuery(dataSetUids: Collection<String>): String =
        """SELECT $DSOU_DATASET AS $DATA_SET_COLUMN, COUNT(DISTINCT $DSOU_ORGUNIT) AS $COUNT_COLUMN
            FROM ${DataSetOrganisationUnitLinkTableInfo.TABLE_INFO.name()} $DSOU_ALIAS
            WHERE $DSOU_DATASET IN (${inValues(dataSetUids)})
            GROUP BY $DSOU_DATASET
        """.singleLine()

    /**
     * Uids of the organisation units assigned to each data set. Only needed when the download has
     * to be split by organisation unit.
     */
    fun assignedOrgUnitsByDataSetQuery(dataSetUids: Collection<String>): String =
        """SELECT DISTINCT $DSOU_DATASET AS $DATA_SET_COLUMN, $DSOU_ORGUNIT AS $ORGANISATION_UNIT_COLUMN
            FROM ${DataSetOrganisationUnitLinkTableInfo.TABLE_INFO.name()} $DSOU_ALIAS
            WHERE $DSOU_DATASET IN (${inValues(dataSetUids)})
        """.singleLine()

    /**
     * Uids of every capture scope organisation unit, used as the assignment of a data set whose
     * own assignment is unknown.
     */
    fun captureScopeOrgUnitsQuery(): String =
        """SELECT DISTINCT $UOU_ORGUNIT AS $ORGANISATION_UNIT_COLUMN
            FROM ${UserOrganisationUnitTableInfo.TABLE_INFO.name()} $UOU_ALIAS
            WHERE $UOU_SCOPE = $captureScope
        """.singleLine()

    /**
     * Uid and parent of every capture scope organisation unit, to walk down the hierarchy. The
     * capture scope is closed downwards from its roots, so the resulting tree has no gaps and
     * descending it never leaves the units the user has access to. Only needed when the download
     * has to be split by organisation unit.
     */
    fun captureScopeOrganisationUnitTreeQuery(): String =
        """SELECT DISTINCT $OU_UID AS $UID_COLUMN, $OU_PARENT AS $PARENT_COLUMN
            FROM ${OrganisationUnitTableInfo.TABLE_INFO.name()} $OU_ALIAS
            INNER JOIN ${UserOrganisationUnitTableInfo.TABLE_INFO.name()} $UOU_ALIAS
                ON $UOU_ORGUNIT = $OU_UID AND $UOU_SCOPE = $captureScope
        """.singleLine()

    private fun inValues(uids: Collection<String>): String =
        commaAndSpaceSeparatedCollectionValues(withSingleQuotationMarksArray(uids).toList())

    private fun String.singleLine(): String = trimIndent().replace("\n", " ")
}
