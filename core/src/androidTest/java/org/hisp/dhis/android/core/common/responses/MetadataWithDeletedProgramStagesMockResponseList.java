package org.hisp.dhis.android.core.common.responses;

import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.server.api.MetadataMockResponseList;

import java.io.IOException;

public class MetadataWithDeletedProgramStagesMockResponseList extends MetadataMockResponseList {

    public MetadataWithDeletedProgramStagesMockResponseList() throws IOException {
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
    protected String getDeletedObjectCategoryOptionComboMockResponse() {
        return "deletedobject/deleted_object_category_option_combo.json";
    }

    @Override
    protected String getDeletedObjectProgramMockResponse() {
        return "deletedobject/deleted_object_programs.json";
    }

    @Override
    protected String getDeletedObjectOptionMockResponse() {
        return "deletedobject/deleted_object_option_sets.json";
    }

    @Override
    protected String getDeletedObjectProgramStageMockResponse() {
        return "deletedobject/deleted_object_program_stages.json";
    }

    @Override
    protected String getDeletedObjectProgramStageDataElementMockResponse() {
        return "deletedobject/deleted_object_program_stage_data_elements.json";
    }

    @Override
    protected String getDeletedObjectProgramStageSectionMockResponse() {
        return "deletedobject/deleted_object_program_stage_sections.json";
    }

    @Override
    protected String getDeletedObjectProgramTrackedEntityAttributeMockResponse() {
        return "deletedobject/deleted_object_program_tracked_entity_attributes.json";
    }

    @Override
    protected String getDeletedObjectTrackedEntityAttributeMockResponse() {
        return "deletedobject/deleted_object_tracked_entity_attributes.json";
    }

    @Override
    protected String getDeletedObjectTrackedEntityMockResponse() {
        return "deletedobject/deleted_object_tracked_entity.json";
    }

    @Override
    protected String getDeletedObjectOptionSetMockResponse() {
        return "deletedobject/deleted_object_option_sets.json";
    }


}
