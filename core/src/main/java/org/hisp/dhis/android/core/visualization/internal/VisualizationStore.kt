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
package org.hisp.dhis.android.core.visualization.internal

import android.database.Cursor
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.IntegerListColumnAdapter
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.ObjectWithUidListColumnAdapter
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.RelativePeriodsColumnAdapter
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.StringListColumnAdapter
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.IdentifiableStatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.db.stores.internal.StoreFactory
import org.hisp.dhis.android.core.arch.helpers.UidsHelper
import org.hisp.dhis.android.core.visualization.Visualization
import org.hisp.dhis.android.core.visualization.VisualizationTableInfo

@Suppress("MagicNumber")
internal object VisualizationStore {
    private val BINDER = object : IdentifiableStatementBinder<Visualization>() {
        override fun bindToStatement(o: Visualization, w: StatementWrapper) {
            super.bindToStatement(o, w)
            w.bind(7, o.description())
            w.bind(8, o.displayDescription())
            w.bind(9, o.displayFormName())
            w.bind(10, o.title())
            w.bind(11, o.displayTitle())
            w.bind(12, o.subtitle())
            w.bind(13, o.displaySubtitle())
            w.bind(14, o.type())
            w.bind(15, o.hideTitle())
            w.bind(16, o.hideSubtitle())
            w.bind(17, o.hideEmptyColumns())
            w.bind(18, o.hideEmptyRows())
            w.bind(19, o.hideEmptyRowItems())
            w.bind(20, o.hideLegend())
            w.bind(21, o.showHierarchy())
            w.bind(22, o.rowTotals())
            w.bind(23, o.rowSubTotals())
            w.bind(24, o.colTotals())
            w.bind(25, o.colSubTotals())
            w.bind(26, o.showDimensionLabels())
            w.bind(27, o.percentStackedValues())
            w.bind(28, o.noSpaceBetweenColumns())
            w.bind(29, o.skipRounding())
            w.bind(30, o.displayDensity())
            w.bind(31, o.digitGroupSeparator())
            w.bind(32, RelativePeriodsColumnAdapter.serialize(o.relativePeriods()))
            w.bind(33, StringListColumnAdapter.serialize(o.filterDimensions()))
            w.bind(34, StringListColumnAdapter.serialize(o.rowDimensions()))
            w.bind(35, StringListColumnAdapter.serialize(o.columnDimensions()))
            w.bind(36, IntegerListColumnAdapter.serialize(o.organisationUnitLevels()))
            w.bind(37, o.userOrganisationUnit())
            w.bind(38, o.userOrganisationUnitChildren())
            w.bind(39, o.userOrganisationUnitGrandChildren())
            w.bind(40, ObjectWithUidListColumnAdapter.serialize(o.organisationUnits()))
            w.bind(41, ObjectWithUidListColumnAdapter.serialize(o.periods()))
            w.bind(42, o.legend()?.showKey())
            w.bind(43, o.legend()?.style())
            w.bind(44, UidsHelper.getUidOrNull(o.legend()?.set()))
            w.bind(45, o.legend()?.strategy())
            w.bind(46, o.aggregationType())
        }
    }

    @JvmStatic
    fun create(databaseAdapter: DatabaseAdapter): IdentifiableObjectStore<Visualization> {
        return StoreFactory.objectWithUidStore(
            databaseAdapter, VisualizationTableInfo.TABLE_INFO, BINDER
        ) { cursor: Cursor -> Visualization.create(cursor) }
    }
}
