package org.hisp.dhis.android.core.deletedobject;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.category.CategoryComboStore;
import org.hisp.dhis.android.core.category.CategoryOptionComboStore;
import org.hisp.dhis.android.core.category.CategoryOptionStore;
import org.hisp.dhis.android.core.category.CategoryStore;
import org.hisp.dhis.android.core.dataelement.DataElementStore;
import org.hisp.dhis.android.core.option.OptionSetStore;
import org.hisp.dhis.android.core.option.OptionStore;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.program.ProgramIndicatorStore;
import org.hisp.dhis.android.core.program.ProgramRuleActionStore;
import org.hisp.dhis.android.core.program.ProgramRuleStore;
import org.hisp.dhis.android.core.program.ProgramRuleVariableStore;
import org.hisp.dhis.android.core.program.ProgramStageDataElementStore;
import org.hisp.dhis.android.core.program.ProgramStageSectionStore;
import org.hisp.dhis.android.core.program.ProgramStageStore;
import org.hisp.dhis.android.core.program.ProgramStore;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeStore;
import org.hisp.dhis.android.core.relationship.RelationshipTypeStore;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityStore;
import org.hisp.dhis.android.core.user.UserStore;

@SuppressWarnings({"PMD.TooManyFields"})
public class DeletedObjectHandler {
    @NonNull
    private final UserStore userStore;
    @NonNull
    private final CategoryStore categoryStore;
    @NonNull
    private final CategoryComboStore categoryComboStore;
    @NonNull
    private final CategoryOptionComboStore categoryOptionComboStore;
    @NonNull
    private final ProgramStore programStore;
    @NonNull
    private final OrganisationUnitStore organisationUnitStore;
    @NonNull
    private final OptionSetStore optionSetStore;
    @NonNull
    private final TrackedEntityStore trackedEntityStore;
    @NonNull
    private final CategoryOptionStore categoryOptionStore;
    @NonNull
    private final DataElementStore dataElementStore;
    @NonNull
    private final OptionStore optionStore;
    @NonNull
    private final ProgramIndicatorStore programIndicatorStore;
    @NonNull
    private final ProgramRuleStore programRuleStore;
    @NonNull
    private final ProgramRuleActionStore programRuleActionStore;
    @NonNull
    private final ProgramRuleVariableStore programRuleVariableStore;
    @NonNull
    private final ProgramStageStore programStageStore;
    @NonNull
    private final ProgramStageDataElementStore programStageDataElementStore;
    @NonNull
    private final ProgramStageSectionStore programStageSectionStore;
    @NonNull
    private final ProgramTrackedEntityAttributeStore programTrackedEntityAttributeStore;
    @NonNull
    private final TrackedEntityAttributeStore trackedEntityAttributeStore;
    @NonNull
    private final RelationshipTypeStore relationshipTypeStore;


    public DeletedObjectHandler(
            @NonNull UserStore userStore,
            @NonNull CategoryStore categoryStore,
            @NonNull CategoryComboStore categoryComboStore,
            @NonNull CategoryOptionComboStore categoryOptionComboStore,
            @NonNull ProgramStore programStore,
            @NonNull OrganisationUnitStore organisationUnitStore,
            @NonNull OptionSetStore optionSetStore,
            @NonNull TrackedEntityStore trackedEntityStore,
            @NonNull CategoryOptionStore categoryOptionStore,
            @NonNull DataElementStore dataElementStore,
            @NonNull OptionStore optionStore,
            @NonNull ProgramIndicatorStore programIndicatorStore,
            @NonNull ProgramRuleStore programRuleStore,
            @NonNull ProgramRuleActionStore programRuleActionStore,
            @NonNull ProgramRuleVariableStore programRuleVariableStore,
            @NonNull ProgramStageStore programStageStore,
            @NonNull ProgramStageDataElementStore programStageDataElementStore,
            @NonNull ProgramStageSectionStore programStageSectionStore,
            @NonNull ProgramTrackedEntityAttributeStore programTrackedEntityAttributeStore,
            @NonNull TrackedEntityAttributeStore trackedEntityAttributeStore,
            @NonNull RelationshipTypeStore relationshipTypeStore) {
        this.userStore = userStore;
        this.categoryStore = categoryStore;
        this.categoryComboStore = categoryComboStore;
        this.categoryOptionComboStore = categoryOptionComboStore;
        this.programStore = programStore;
        this.organisationUnitStore = organisationUnitStore;
        this.optionSetStore = optionSetStore;
        this.trackedEntityStore = trackedEntityStore;
        this.categoryOptionStore = categoryOptionStore;
        this.dataElementStore = dataElementStore;
        this.optionStore = optionStore;
        this.programIndicatorStore = programIndicatorStore;
        this.programRuleStore = programRuleStore;
        this.programRuleActionStore = programRuleActionStore;
        this.programRuleVariableStore = programRuleVariableStore;
        this.programStageStore = programStageStore;
        this.programStageDataElementStore = programStageDataElementStore;
        this.programStageSectionStore = programStageSectionStore;
        this.programTrackedEntityAttributeStore = programTrackedEntityAttributeStore;
        this.trackedEntityAttributeStore = trackedEntityAttributeStore;
        this.relationshipTypeStore = relationshipTypeStore;
    }

    public void handle(String uid, ResourceModel.Type type) {
        removeResource(uid, type);
    }

    @SuppressWarnings("PMD")
    private void removeResource(String uid, ResourceModel.Type type) {
        if (type.equals(ResourceModel.Type.DELETED_USER)) {
            deleteUser(uid);
        } else if (type.equals(ResourceModel.Type.DELETED_ORGANISATION_UNIT)) {
            deleteOrganisationUnit(uid);
        } else if (type.equals(ResourceModel.Type.DELETED_PROGRAM)) {
            deleteProgram(uid);
        } else if (type.equals(ResourceModel.Type.DELETED_OPTION_SET)) {
            deleteOptionSet(uid);
        } else if (type.equals(ResourceModel.Type.DELETED_TRACKED_ENTITY)) {
            deleteTrackedEntity(uid);
        } else if (type.equals(ResourceModel.Type.DELETED_CATEGORY_COMBO)) {
            deleteCategoryCombo(uid);
        } else if (type.equals(ResourceModel.Type.DELETED_CATEGORY)) {
            deleteCategory(uid);
        } else if (type.equals(ResourceModel.Type.DELETED_CATEGORY_OPTION)) {
            deleteCategoryOption(uid);
        } else if (type.equals(ResourceModel.Type.DELETED_CATEGORY_OPTION_COMBO)) {
            deleteCategoryOptionCombo(uid);
        } else if (type.equals(ResourceModel.Type.DELETED_DATA_ELEMENT)) {
            deleteDataElement(uid);
        } else if (type.equals(ResourceModel.Type.DELETED_OPTION)) {
            deleteOption(uid);
        } else if (type.equals(ResourceModel.Type.DELETED_PROGRAM_INDICATOR)) {
            deleteProgramIndicator(uid);
        } else if (type.equals(ResourceModel.Type.DELETED_PROGRAM_RULE)) {
            deleteProgramRule(uid);
        } else if (type.equals(ResourceModel.Type.DELETED_PROGRAM_RULE_ACTION)) {
            deleteProgramRuleAction(uid);
        } else if (type.equals(ResourceModel.Type.DELETED_PROGRAM_RULE_VARIABLE)) {
            deleteProgramRuleVariable(uid);
        } else if (type.equals(ResourceModel.Type.DELETED_PROGRAM_STAGE)) {
            deleteProgramStage(uid);
        } else if (type.equals(ResourceModel.Type.DELETED_PROGRAM_STAGE_DATA_ELEMENT)) {
            deleteProgramStageDataElement(uid);
        } else if (type.equals(ResourceModel.Type.DELETED_PROGRAM_STAGE_SECTION)) {
            deleteProgramStageSection(uid);
        } else if (type.equals(ResourceModel.Type.DELETED_PROGRAM_TRACKED_ENTITY_ATTRIBUTE)) {
            deleteProgramTrackedEntityAttribute(uid);
        } else if (type.equals(ResourceModel.Type.DELETED_TRACKED_ENTITY_ATTRIBUTE)) {
            deleteTrackedEntityAttribute(uid);
        } else if (type.equals(ResourceModel.Type.DELETED_RELATIONSHIP_TYPE)) {
            deleteRelationshipType(uid);
        }
    }

    private void deleteRelationshipType(String uid) {
        relationshipTypeStore.delete(uid);
    }

    private void deleteTrackedEntityAttribute(String uid) {
        trackedEntityAttributeStore.delete(uid);
    }

    private void deleteProgramTrackedEntityAttribute(String uid) {
        programTrackedEntityAttributeStore.delete(uid);
    }

    private void deleteProgramStageSection(String uid) {
        programStageSectionStore.delete(uid);
    }

    private void deleteProgramStageDataElement(String uid) {
        programStageDataElementStore.delete(uid);
    }

    private void deleteProgramStage(String uid) {
        programStageStore.delete(uid);
    }

    private void deleteProgramRuleVariable(String uid) {
        programRuleVariableStore.delete(uid);
    }

    private void deleteProgramRuleAction(String uid) {
        programRuleActionStore.delete(uid);
    }

    private void deleteProgramRule(String uid) {
        programRuleStore.delete(uid);
    }

    private void deleteProgramIndicator(String uid) {
        programIndicatorStore.delete(uid);
    }

    private void deleteOption(String uid) {
        optionStore.delete(uid);
    }

    private void deleteDataElement(String uid) {
        dataElementStore.delete(uid);
    }

    private void deleteOrganisationUnit(String uid) {
        organisationUnitStore.delete(uid);
    }

    private void deleteProgram(String uid) {
        programStore.delete(uid);
    }

    private void deleteOptionSet(String uid) {
        optionSetStore.delete(uid);
    }

    private void deleteTrackedEntity(String uid) {
        trackedEntityStore.delete(uid);
    }


    private void deleteCategory(String uid) {
        categoryStore.delete(uid);
    }

    private void deleteCategoryCombo(String uid) {
        categoryComboStore.delete(uid);
    }

    private void deleteCategoryOptionCombo(String uid) {
        categoryOptionComboStore.delete(uid);
    }

    private void deleteUser(String userUid) {
        userStore.delete(userUid);
    }

    private void deleteCategoryOption(String uid) {
        categoryOptionStore.delete(uid);
    }

}
