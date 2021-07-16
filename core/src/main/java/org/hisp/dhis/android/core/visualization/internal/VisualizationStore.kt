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
            w.bind(10, o.type())
            w.bind(11, o.hideTitle())
            w.bind(12, o.hideSubtitle())
            w.bind(13, o.hideEmptyColumns())
            w.bind(14, o.hideEmptyRows())
            w.bind(15, o.hideEmptyRowItems())
            w.bind(16, o.hideLegend())
            w.bind(17, o.showHierarchy())
            w.bind(18, o.rowTotals())
            w.bind(19, o.rowSubTotals())
            w.bind(20, o.colTotals())
            w.bind(21, o.colSubTotals())
            w.bind(22, o.showDimensionLabels())
            w.bind(23, o.percentStackedValues())
            w.bind(24, o.noSpaceBetweenColumns())
            w.bind(25, o.skipRounding())
            w.bind(26, o.displayDensity())
            w.bind(27, o.digitGroupSeparator())
            w.bind(28, RelativePeriodsColumnAdapter.serialize(o.relativePeriods()))
            w.bind(29, StringListColumnAdapter.serialize(o.filterDimensions()))
            w.bind(30, StringListColumnAdapter.serialize(o.rowDimensions()))
            w.bind(31, StringListColumnAdapter.serialize(o.columnDimensions()))
            w.bind(32, IntegerListColumnAdapter.serialize(o.organisationUnitLevels()))
            w.bind(33, o.userOrganisationUnit())
            w.bind(34, o.userOrganisationUnitChildren())
            w.bind(35, o.userOrganisationUnitGrandChildren())
            w.bind(36, ObjectWithUidListColumnAdapter.serialize(o.organisationUnits()))
            w.bind(37, ObjectWithUidListColumnAdapter.serialize(o.periods()))
        }
    }

    @JvmStatic
    fun create(databaseAdapter: DatabaseAdapter): IdentifiableObjectStore<Visualization> {
        return StoreFactory.objectWithUidStore(
            databaseAdapter, VisualizationTableInfo.TABLE_INFO, BINDER
        ) { cursor: Cursor -> Visualization.create(cursor) }
    }
}
