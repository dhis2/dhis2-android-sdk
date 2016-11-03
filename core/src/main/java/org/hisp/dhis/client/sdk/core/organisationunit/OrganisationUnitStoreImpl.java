package org.hisp.dhis.client.sdk.core.organisationunit;

import android.content.ContentResolver;
import android.database.Cursor;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.client.sdk.core.commons.database.AbsIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.core.organisationunit.OrganisationUnitTable.OrganisationUnitColumns;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;

import java.util.List;

public class OrganisationUnitStoreImpl extends AbsIdentifiableObjectStore<OrganisationUnit> implements OrganisationUnitStore {

    public OrganisationUnitStoreImpl(ContentResolver contentResolver, ObjectMapper objectMapper) {
        super(contentResolver, new OrganisationUnitMapper(objectMapper));
    }

    @Override
    public List<OrganisationUnit> query(String parentOrganisationUnitId) {
        if (parentOrganisationUnitId == null) {
            throw new IllegalArgumentException("parent orgUnit uid must not be null");
        }

        final String[] selectionArgs = new String[]{parentOrganisationUnitId};
        final String selection = OrganisationUnitColumns.COLUMN_PARENT + " = ?";

        Cursor cursor = contentResolver.query(mapper.getContentUri(),
                mapper.getProjection(), selection, selectionArgs, null);
        return toModels(cursor);
    }
}
