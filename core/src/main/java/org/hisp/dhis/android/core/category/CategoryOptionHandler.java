package org.hisp.dhis.android.core.category;


import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.common.IdentifiableHandlerImpl;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.LinkModelHandler;
import org.hisp.dhis.android.core.common.LinkModelHandlerImpl;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.dataset.SectionDataElementLinkModel;

public final class CategoryOptionHandler extends IdentifiableHandlerImpl<CategoryOption, CategoryOptionModel> {

    private final LinkModelHandler<ObjectWithUid, CategoryOptionOrganisationUnitLinkModel>
            categoryOptionOrganisationUnitLinkHandler;

    private CategoryOptionHandler(IdentifiableObjectStore<CategoryOptionModel> categoryOptionStore,
                                  LinkModelHandler<ObjectWithUid, CategoryOptionOrganisationUnitLinkModel>
                                          categoryOptionOrganisationUnitLinkHandler) {
        super(categoryOptionStore);

        this.categoryOptionOrganisationUnitLinkHandler = categoryOptionOrganisationUnitLinkHandler;
    }

    public static CategoryOptionHandler create(DatabaseAdapter databaseAdapter) {
        return new CategoryOptionHandler(CategoryOptionStore.create(databaseAdapter),
                new LinkModelHandlerImpl<ObjectWithUid, CategoryOptionOrganisationUnitLinkModel>(
                        CategoryOptionOrganisationUnitLinkStore.create(databaseAdapter)));
    }

    @Override
    protected void afterObjectHandled(CategoryOption categoryOption, HandleAction action) {

        categoryOptionOrganisationUnitLinkHandler.handleMany(categoryOption.uid(), categoryOption.organizationUnits(),
                new CategoryOptionOrganisationUnitLinkModelBuilder(categoryOption));

    }
}