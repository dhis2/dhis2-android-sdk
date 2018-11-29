package org.hisp.dhis.android.core.maintenance;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.category.CategoryOptionComboCategoryOptionLinkTableInfo;
import org.hisp.dhis.android.core.category.CategoryOptionTableInfo;
import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.hisp.dhis.android.core.option.OptionFields;
import org.hisp.dhis.android.core.option.OptionSetTableInfo;
import org.hisp.dhis.android.core.option.OptionTableInfo;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class MaintenanceMockIntegrationShould extends MockIntegrationShould {

    @BeforeClass
    public static void setUpAll() throws Exception {
        downloadMetadata();
    }

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
            violationsToCompare.add(violation.toBuilder().created(null).fromObjectRow(null).build());
        }

        assertThat(violationsToCompare.contains(categoryOptionComboViolation), is(true));
        assertThat(violationsToCompare.contains(optionViolation), is(true));
    }

    @Test
    public void allow_access_to_foreign_key_violations_with_children() {
        List<ForeignKeyViolation> violations = d2.maintenanceModule().foreignKeyViolations.getWithAllChildren();
        assertThat(violations.size(), is(2));
    }
}