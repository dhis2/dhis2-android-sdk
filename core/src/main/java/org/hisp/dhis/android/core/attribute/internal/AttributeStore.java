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

package org.hisp.dhis.android.core.attribute.internal;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.NameableStatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.db.stores.internal.StoreFactory;
import org.hisp.dhis.android.core.attribute.Attribute;
import org.hisp.dhis.android.core.attribute.AttributeTableInfo;

public final class AttributeStore {

    private AttributeStore() {
    }

    private static StatementBinder<Attribute> BINDER =
            new NameableStatementBinder<Attribute>() {

                @Override
                public void bindToStatement(@NonNull Attribute o, @NonNull StatementWrapper w) {
                    super.bindToStatement(o, w);
                    w.bind(11, o.valueType());
                    w.bind(12, o.unique());
                    w.bind(13, o.mandatory());
                    w.bind(14, o.indicatorAttribute());
                    w.bind(15, o.indicatorGroupAttribute());
                    w.bind(16, o.userGroupAttribute());
                    w.bind(17, o.dataElementAttribute());
                    w.bind(18, o.constantAttribute());
                    w.bind(19, o.categoryOptionAttribute());
                    w.bind(20, o.optionSetAttribute());
                    w.bind(21, o.sqlViewAttribute());
                    w.bind(22, o.legendSetAttribute());
                    w.bind(23, o.trackedEntityAttributeAttribute());
                    w.bind(24, o.organisationUnitAttribute());
                    w.bind(25, o.dataSetAttribute());
                    w.bind(26, o.documentAttribute());
                    w.bind(27, o.validationRuleGroupAttribute());
                    w.bind(28, o.dataElementGroupAttribute());
                    w.bind(29, o.sectionAttribute());
                    w.bind(30, o.trackedEntityTypeAttribute());
                    w.bind(31, o.userAttribute());
                    w.bind(32, o.categoryOptionGroupAttribute());
                    w.bind(33, o.programStageAttribute());
                    w.bind(34, o.programAttribute());
                    w.bind(35, o.categoryAttribute());
                    w.bind(36, o.categoryOptionComboAttribute());
                    w.bind(37, o.categoryOptionGroupSetAttribute());
                    w.bind(38, o.validationRuleAttribute());
                    w.bind(39, o.programIndicatorAttribute());
                    w.bind(40, o.organisationUnitGroupAttribute());
                    w.bind(41, o.dataElementGroupSetAttribute());
                    w.bind(42, o.organisationUnitGroupSetAttribute());
                    w.bind(43, o.optionAttribute());
                }
            };

    public static IdentifiableObjectStore<Attribute> create(
            DatabaseAdapter databaseAdapter) {
        return StoreFactory.objectWithUidStore(databaseAdapter,
                AttributeTableInfo.TABLE_INFO, BINDER, Attribute::create);
    }
}