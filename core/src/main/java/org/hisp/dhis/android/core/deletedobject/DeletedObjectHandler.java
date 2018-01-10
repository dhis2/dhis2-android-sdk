package org.hisp.dhis.android.core.deletedobject;


import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.category.CategoryComboStore;
import org.hisp.dhis.android.core.category.CategoryOption;
import org.hisp.dhis.android.core.category.CategoryOptionComboStore;
import org.hisp.dhis.android.core.category.CategoryOptionStore;
import org.hisp.dhis.android.core.category.CategoryStore;
import org.hisp.dhis.android.core.constant.ConstantStore;
import org.hisp.dhis.android.core.dataelement.DataElementStore;
import org.hisp.dhis.android.core.option.OptionSetStore;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.program.ProgramStore;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityStore;
import org.hisp.dhis.android.core.user.UserCredentialsStore;
import org.hisp.dhis.android.core.user.UserStore;

public class DeletedObjectHandler {
    @NonNull
    private final UserStore userStore;
    @NonNull
    private final UserCredentialsStore userCredentialsStore;
    @NonNull
    private final CategoryStore categoryStore;
    @NonNull
    private final CategoryComboStore categoryComboStore;
    @NonNull
    private final CategoryOptionComboStore categoryOptionComboStore;
    @NonNull
    private final ConstantStore constantStore;
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



    public DeletedObjectHandler(
            @NonNull UserStore userStore,
            @NonNull UserCredentialsStore userCredentialsStore,
            @NonNull CategoryStore categoryStore,
            @NonNull CategoryComboStore categoryComboStore,
            @NonNull CategoryOptionComboStore categoryOptionComboStore,
            @NonNull ConstantStore constantStore,
            @NonNull ProgramStore programStore,
            @NonNull OrganisationUnitStore organisationUnitStore,
            @NonNull OptionSetStore optionSetStore,
            @NonNull TrackedEntityStore trackedEntityStore,
            @NonNull CategoryOptionStore categoryOptionStore,
            @NonNull DataElementStore dataElementStore) {
        this.userStore = userStore;
        this.userCredentialsStore = userCredentialsStore;
        this.categoryStore = categoryStore;
        this.categoryComboStore = categoryComboStore;
        this.categoryOptionComboStore = categoryOptionComboStore;
        this.constantStore = constantStore;
        this.programStore = programStore;
        this.organisationUnitStore = organisationUnitStore;
        this.optionSetStore = optionSetStore;
        this.trackedEntityStore = trackedEntityStore;
        this.categoryOptionStore = categoryOptionStore;
        this.dataElementStore = dataElementStore;
    }

    public void handle(String uid, ResourceModel.Type type) {
        removeResource(uid, type);
    }

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
        }
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
