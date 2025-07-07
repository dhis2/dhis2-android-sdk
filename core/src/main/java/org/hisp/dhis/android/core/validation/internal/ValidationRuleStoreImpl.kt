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
package org.hisp.dhis.android.core.validation.internal

import android.database.Cursor
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.IntegerArrayColumnAdapter
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.NameableStatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStoreImpl
import org.hisp.dhis.android.core.validation.ValidationRule
import org.hisp.dhis.android.core.validation.ValidationRuleTableInfo

@Suppress("MagicNumber")
internal class ValidationRuleStoreImpl(
    databaseAdapter: DatabaseAdapter,
) : ValidationRuleStore,
    IdentifiableObjectStoreImpl<ValidationRule>(
        databaseAdapter,
        ValidationRuleTableInfo.TABLE_INFO,
        BINDER,
        { cursor: Cursor -> ValidationRule.create(cursor) },
    ) {

    companion object {
        private val BINDER: StatementBinder<ValidationRule> = object : NameableStatementBinder<ValidationRule>() {
            override fun bindToStatement(o: ValidationRule, w: StatementWrapper) {
                super.bindToStatement(o, w)
                w.bind(11, o.instruction())
                w.bind(12, o.importance())
                w.bind(13, o.operator())
                w.bind(14, o.periodType())
                w.bind(15, o.skipFormValidation())
                w.bind(16, o.leftSide().expression())
                w.bind(17, o.leftSide().description())
                w.bind(18, o.leftSide().missingValueStrategy())
                w.bind(19, o.rightSide().expression())
                w.bind(20, o.rightSide().description())
                w.bind(21, o.rightSide().missingValueStrategy())
                w.bind(22, IntegerArrayColumnAdapter.serialize(o.organisationUnitLevels()))
            }
        }
    }
}
