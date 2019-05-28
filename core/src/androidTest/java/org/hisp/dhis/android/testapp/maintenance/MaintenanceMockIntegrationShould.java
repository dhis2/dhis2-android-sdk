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

package org.hisp.dhis.android.testapp.maintenance;

import org.hisp.dhis.android.core.category.CategoryOptionComboCategoryOptionLinkTableInfo;
import org.hisp.dhis.android.core.category.CategoryOptionTableInfo;
import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.utils.integration.BaseIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.ForeignKeyViolation;
import org.hisp.dhis.android.core.option.OptionFields;
import org.hisp.dhis.android.core.option.OptionSetTableInfo;
import org.hisp.dhis.android.core.option.OptionTableInfo;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import androidx.test.runner.AndroidJUnit4;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class MaintenanceMockIntegrationShould extends BaseIntegrationTestFullDispatcher {

    @Test
    public void allow_access_to_foreign_key_violations() {
        List<ForeignKeyViolation> violations = d2.maintenanceModule().foreignKeyViolations.get();
        assertThat(violations.size(), is(2));

        ForeignKeyViolation categoryOptionComboViolation = ForeignKeyViolation.builder()
                .toTable(CategoryOptionTableInfo.TABLE_INFO.name())
                .toColumn(BaseIdentifiableObjectModel.Columns.UID)
                .fromTable(CategoryOptionComboCategoryOptionLinkTableInfo.TABLE_INFO.name())
                .fromColumn(CategoryOptionComboCategoryOptionLinkTableInfo.Columns.CATEGORY_OPTION)
                .notFoundValue("non_existent_category_option_uid")
                .build();

        ForeignKeyViolation optionViolation = ForeignKeyViolation.builder()
                .toTable(OptionSetTableInfo.TABLE_INFO.name())
                .toColumn(BaseIdentifiableObjectModel.Columns.UID)
                .fromTable(OptionTableInfo.TABLE_INFO.name())
                .fromColumn(OptionFields.OPTION_SET)
                .notFoundValue("non_existent_option_set_uid")
                .fromObjectUid("non_existent_option_uid")
                .build();

        List<ForeignKeyViolation> violationsToCompare = new ArrayList<>();
        for (ForeignKeyViolation violation : violations) {
            violationsToCompare.add(violation.toBuilder().id(null).created(null).fromObjectRow(null).build());
        }

        assertThat(violationsToCompare.contains(categoryOptionComboViolation), is(true));
        assertThat(violationsToCompare.contains(optionViolation), is(true));
    }

    @Test
    public void allow_access_to_foreign_key_violations_with_children() {
        List<ForeignKeyViolation> violations = d2.maintenanceModule().foreignKeyViolations.withAllChildren().get();
        assertThat(violations.size(), is(2));
    }

    @Test
    public void get_no_vulnerabilities_for_high_threshold() {
        assertThat(d2.maintenanceModule().getPerformanceHintsService(100,
                100).areThereVulnerabilities(), is(false));
    }

    @Test
    public void get_vulnerabilities_for_low_threshold() {
        assertThat(d2.maintenanceModule().getPerformanceHintsService(1,
                1).areThereVulnerabilities(), is(true));
    }

    @Test
    public void allow_access_to_d2_errors() {
        List<D2Error> d2Errors = d2.maintenanceModule().d2Errors.get();
        assertThat(d2Errors.size(), is(2));
    }
}