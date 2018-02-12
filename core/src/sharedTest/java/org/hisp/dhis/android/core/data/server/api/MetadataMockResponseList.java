package org.hisp.dhis.android.core.data.server.api;

import org.hisp.dhis.android.core.data.file.IFileReader;

import java.io.IOException;

public abstract class MetadataMockResponseList extends MockResponseList {
    protected static String DELETED_OBJECT_EMPTY = "deletedobject/deleted_object_empty.json";

    public MetadataMockResponseList(IFileReader fileReader) throws IOException {
        super(fileReader);

        addFileResponse(getSystemInfoMockResponse());

        addFileResponse(getUserMockResponse());

        addFileResponse(getOrganisationUnitMockResponse());

        addFileResponse(getCategoriesMockResponse());

        addFileResponse(getCategoryCombosMockResponse());

        addFileResponse(getProgramsMockResponse());

        addFileResponse(getTrackedEntityMockResponse());

        addFileResponse(getOptionSetMockResponse());

        addFileResponse(getSystemInfoMockResponse());

        addFileResponse(getDeletedObjectUserMockResponse());

        addFileResponse(getDeletedObjectOrganisationUnitMockResponse());

        addFileResponse(getDeletedObjectCategoryMockResponse());
        addFileResponse(getDeletedObjectCategoryOptionMockResponse());
        addFileResponse(getDeletedObjectCategoryComboMockResponse());
        addFileResponse(getDeletedObjectCategoryOptionComboMockResponse());

        addFileResponse(getDeletedObjectProgramRuleMockResponse());
        addFileResponse(getDeletedObjectProgramRuleActionMockResponse());
        addFileResponse(getDeletedObjectProgramRuleVariableMockResponse());
        addFileResponse(getDeletedObjectProgramIndicatorMockResponse());
        addFileResponse(getDeletedObjectDataElementMockResponse());
        addFileResponse(getDeletedObjectProgramStageMockResponse());
        addFileResponse(getDeletedObjectProgramStageDataElementMockResponse());
        addFileResponse(getDeletedObjectProgramStageSectionMockResponse());
        addFileResponse(getDeletedObjectProgramTrackedEntityAttributeMockResponse());
        addFileResponse(getDeletedObjectTrackedEntityAttributeMockResponse());
        addFileResponse(getDeletedObjectRelationshipTypeMockResponse());
        addFileResponse(getDeletedObjectProgramMockResponse());

        addFileResponse(getDeletedObjectTrackedEntityMockResponse());

        addFileResponse(getDeletedObjectOptionSetMockResponse());
        addFileResponse(getDeletedObjectOptionMockResponse());

    }

    protected abstract String getSystemInfoMockResponse();

    protected abstract String getUserMockResponse();

    protected abstract String getOrganisationUnitMockResponse();

    protected abstract String getCategoriesMockResponse();

    protected abstract String getCategoryCombosMockResponse();

    protected abstract String getProgramsMockResponse();

    protected abstract String getTrackedEntityMockResponse();

    protected abstract String getOptionSetMockResponse();


    protected String getDeletedObjectUserMockResponse() {
        return DELETED_OBJECT_EMPTY;
    }

    protected String getDeletedObjectOrganisationUnitMockResponse() {
        return DELETED_OBJECT_EMPTY;
    }

    protected String getDeletedObjectCategoryMockResponse() {
        return DELETED_OBJECT_EMPTY;
    }

    protected String getDeletedObjectCategoryOptionMockResponse() {
        return DELETED_OBJECT_EMPTY;
    }

    protected String getDeletedObjectCategoryComboMockResponse() {
        return DELETED_OBJECT_EMPTY;
    }

    protected String getDeletedObjectCategoryOptionComboMockResponse() {
        return DELETED_OBJECT_EMPTY;
    }

    protected String getDeletedObjectProgramRuleMockResponse() {
        return DELETED_OBJECT_EMPTY;
    }

    protected String getDeletedObjectProgramRuleActionMockResponse() {
        return DELETED_OBJECT_EMPTY;
    }

    protected String getDeletedObjectProgramRuleVariableMockResponse() {
        return DELETED_OBJECT_EMPTY;
    }

    protected String getDeletedObjectProgramIndicatorMockResponse() {
        return DELETED_OBJECT_EMPTY;
    }

    protected String getDeletedObjectDataElementMockResponse() {
        return DELETED_OBJECT_EMPTY;
    }

    protected String getDeletedObjectProgramStageMockResponse() {
        return DELETED_OBJECT_EMPTY;
    }

    protected String getDeletedObjectProgramStageDataElementMockResponse() {
        return DELETED_OBJECT_EMPTY;
    }

    protected String getDeletedObjectProgramStageSectionMockResponse() {
        return DELETED_OBJECT_EMPTY;
    }

    protected String getDeletedObjectProgramTrackedEntityAttributeMockResponse() {
        return DELETED_OBJECT_EMPTY;
    }

    protected String getDeletedObjectTrackedEntityAttributeMockResponse() {
        return DELETED_OBJECT_EMPTY;
    }

    protected String getDeletedObjectRelationshipTypeMockResponse() {
        return DELETED_OBJECT_EMPTY;
    }

    protected String getDeletedObjectProgramMockResponse() {
        return DELETED_OBJECT_EMPTY;
    }

    protected String getDeletedObjectTrackedEntityMockResponse() {
        return DELETED_OBJECT_EMPTY;
    }

    protected String getDeletedObjectOptionSetMockResponse() {
        return DELETED_OBJECT_EMPTY;
    }

    protected String getDeletedObjectOptionMockResponse() {
        return DELETED_OBJECT_EMPTY;
    }

}
