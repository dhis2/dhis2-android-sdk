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

package org.hisp.dhis.android.core.program;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.SectionRenderingColumnAdapter;
import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo;
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper;
import org.hisp.dhis.android.core.common.IdentifiableWithStyleColumns;

public final class ProgramSectionTableInfo {

    private ProgramSectionTableInfo() {
    }

    public static final TableInfo TABLE_INFO = new TableInfo() {

        @Override
        public String name() {
            return "ProgramSection";
        }

        @Override
        public Columns columns() {
            return new Columns();
        }
    };

    public static class Columns extends IdentifiableWithStyleColumns {
        public static final String DESCRIPTION = "description";
        public static final String PROGRAM = "program";
        public static final String SORT_ORDER = "sortOrder";
        public static final String FORM_NAME = "formName";
        public static final String DESKTOP_RENDER_TYPE = SectionRenderingColumnAdapter.DESKTOP_RENDER_TYPE;
        public static final String MOBILE_RENDER_TYPE = SectionRenderingColumnAdapter.MOBILE_RENDER_TYPE;

        @Override
        public String[] all() {
            return CollectionsHelper.appendInNewArray(super.all(),
                    DESCRIPTION,
                    PROGRAM,
                    SORT_ORDER,
                    FORM_NAME,
                    DESKTOP_RENDER_TYPE,
                    MOBILE_RENDER_TYPE
            );
        }
    }
}