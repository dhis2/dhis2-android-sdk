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

package org.hisp.dhis.android.core.arch.db.access.internal;


import android.database.sqlite.SQLiteStatement;

import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;

import java.util.Date;

class UnencryptedStatementWrapper implements StatementWrapper {

    private final SQLiteStatement s;

    UnencryptedStatementWrapper(SQLiteStatement s) {
        this.s = s;
    }

    @Override
    public void bindNull(int index) {
        s.bindNull(index);
    }

    @Override
    public void bind(int index, String arg) {
        if (arg == null) {
            s.bindNull(index);
        } else {
            s.bindString(index, arg);
        }
    }

    @Override
    public void bind(int index, Boolean arg) {
        if (arg == null) {
            s.bindNull(index);
        } else {
            s.bindLong(index, arg ? 1 : 0);
        }
    }

    @Override
    public void bind(int index, Integer arg) {
        if (arg == null) {
            s.bindNull(index);
        } else {
            s.bindLong(index, arg);
        }
    }

    @Override
    public void bind(int index, Date arg) {
        if (arg == null) {
            s.bindNull(index);
        } else {
            s.bindString(index, BaseIdentifiableObject.DATE_FORMAT.format(arg));
        }
    }

    @Override
    public void bind(int index, Enum arg) {
        if (arg == null) {
            s.bindNull(index);
        } else {
            s.bindString(index, arg.name());
        }
    }

    @Override
    public void bind(int index, Double arg) {
        if (arg == null) {
            s.bindNull(index);
        } else {
            s.bindDouble(index, arg);
        }
    }

    @Override
    public void bind(int index, Long arg) {
        if (arg == null) {
            s.bindNull(index);
        } else {
            s.bindLong(index, arg);
        }
    }

    @Override
    public void clearBindings() {
        s.clearBindings();
    }

    @Override
    public long executeInsert() {
        return s.executeInsert();
    }

    @Override
    public int executeUpdateDelete() {
        return s.executeUpdateDelete();
    }

    @Override
    public void close() {
        s.close();
    }
}