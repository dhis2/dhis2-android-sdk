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

package org.hisp.dhis.android.testapp.maintenance;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.maintenance.ForeignKeyViolation;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class ForeignKeyViolationCollectionRepositoryMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void find_all() {
        List<ForeignKeyViolation> foreignKeyViolations = d2.maintenanceModule().foreignKeyViolations().blockingGet();
        assertThat(foreignKeyViolations.size()).isEqualTo(4);
    }

    @Test
    public void filter_by_from_table() {
        List<ForeignKeyViolation> foreignKeyViolations = d2.maintenanceModule().foreignKeyViolations()
                .byFromTable().eq("CategoryOptionComboCategoryOptionLink").blockingGet();
        assertThat(foreignKeyViolations.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_from_column() {
        List<ForeignKeyViolation> foreignKeyViolations = d2.maintenanceModule().foreignKeyViolations()
                .byFromColumn().eq("categoryOption").blockingGet();
        assertThat(foreignKeyViolations.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_to_table() {
        List<ForeignKeyViolation> foreignKeyViolations = d2.maintenanceModule().foreignKeyViolations()
                .byToTable().eq("CategoryOption").blockingGet();
        assertThat(foreignKeyViolations.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_to_column() {
        List<ForeignKeyViolation> foreignKeyViolations = d2.maintenanceModule().foreignKeyViolations()
                .byToColumn().eq("uid").blockingGet();
        assertThat(foreignKeyViolations.size()).isEqualTo(3);
    }

    @Test
    public void filter_by_not_found_value() {
        List<ForeignKeyViolation> foreignKeyViolations = d2.maintenanceModule().foreignKeyViolations()
                .byNotFoundValue().eq("non_existent_category_option_uid").blockingGet();
        assertThat(foreignKeyViolations.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_from_object_uid() {
        List<ForeignKeyViolation> foreignKeyViolations = d2.maintenanceModule().foreignKeyViolations()
                .byFromObjectUid().eq("non_existent_option_uid").blockingGet();
        assertThat(foreignKeyViolations.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_from_object_row() {
        List<ForeignKeyViolation> foreignKeyViolations = d2.maintenanceModule().foreignKeyViolations()
                .byFromObjectRow().like("categoryOption: non_existent_category_option_uid").blockingGet();
        assertThat(foreignKeyViolations.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_created() throws ParseException {
        List<ForeignKeyViolation> foreignKeyViolations = d2.maintenanceModule().foreignKeyViolations()
                .byCreated().after(BaseIdentifiableObject.parseDate("2019-01-15T08:14:06.767")).blockingGet();
        assertThat(foreignKeyViolations.size()).isEqualTo(4);
    }
}