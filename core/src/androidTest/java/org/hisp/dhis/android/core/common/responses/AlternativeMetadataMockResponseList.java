package org.hisp.dhis.android.core.common.responses;

import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.server.api.MetadataMockResponseList;

import java.io.IOException;

public class AlternativeMetadataMockResponseList extends MetadataMockResponseList {

    public AlternativeMetadataMockResponseList() throws IOException {
        super(new AssetsFileReader());
    }

    @Override
    protected String getSystemInfoMockResponse() {
        return "system_info.json";
    }

    @Override
    protected String getUserMockResponse() {
        return "deletedobject/alternative_user.json";
    }

    @Override
    protected String getOrganisationUnitMockResponse() {
        return "deletedobject/empty_organisation_units.json";
    }

    @Override
    protected String getCategoriesMockResponse() {
        return "deletedobject/empty_categories.json";
    }

    @Override
    protected String getCategoryCombosMockResponse() {
        return "deletedobject/empty_category_combos.json";
    }

    @Override
    protected String getProgramsMockResponse() {
        return "deletedobject/empty_programs.json";
    }

    @Override
    protected String getTrackedEntityMockResponse() {
        return "deletedobject/empty_tracked_entity.json";
    }

    @Override
    protected String getOptionSetMockResponse() {
        return "deletedobject/empty_option_sets.json";
    }

    @Override
    protected String getDeletedObjectUserMockResponse() {
        return "deletedobject/deleted_object_user.json";
    }

    @Override
    protected String getDeletedObjectOrganisationUnitMockResponse() {
        return "deletedobject/deleted_object_organisation_unit.json";
    }

    @Override
    protected String getDeletedObjectCategoryMockResponse() {
        return "deletedobject/deleted_object_categories.json";
    }

    @Override
    protected String getDeletedObjectCategoryOptionMockResponse() {
        return "deletedobject/deleted_object_category_options.json";
    }

    @Override
    protected String getDeletedObjectCategoryComboMockResponse() {
        return "deletedobject/deleted_object_category_combo.json";
    }

    @Override
    protected String getDeletedObjectProgramRuleMockResponse() {
        return "deletedobject/deleted_object_program_rules.json";
    }

    @Override
    protected String getDeletedObjectProgramRuleActionMockResponse() {
        return "deletedobject/deleted_object_program_rule_actions.json";
    }

    @Override
    protected String getDeletedObjectProgramRuleVariableMockResponse() {
        return "deletedobject/deleted_object_program_rule_variables.json";
    }

    @Override
    protected String getDeletedObjectProgramIndicatorMockResponse() {
        return "deletedobject/deleted_object_program_indicators.json";
    }

    @Override
    protected String getDeletedObjectDataElementMockResponse() {
        return "deletedobject/deleted_object_data_elements.json";
    }

    @Override
    protected String getDeletedObjectRelationshipTypeMockResponse() {
        return "deletedobject/deleted_object_relationship_types.json";
    }

    @Override
    protected String getDeletedObjectProgramMockResponse() {
        return "deletedobject/deleted_object_programs.json";
    }

    @Override
    protected String getDeletedObjectTrackedEntityMockResponse() {
        return "deletedobject/deleted_object_tracked_entity.json";
    }

    @Override
    protected String getDeletedObjectOptionSetMockResponse() {
        return "deletedobject/deleted_object_options.json";
    }

    @Override
    protected String getDeletedObjectOptionMockResponse() {
        return "deletedobject/deleted_object_option_sets.json";
    }
}
