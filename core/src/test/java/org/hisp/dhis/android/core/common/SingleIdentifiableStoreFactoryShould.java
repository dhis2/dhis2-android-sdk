package org.hisp.dhis.android.core.common;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.deletedobject.DeletedObject;
import org.hisp.dhis.android.core.deletedobject.DeletedObjectHandler;
import org.hisp.dhis.android.core.deletedobject.IdentifiableStoreFactory;
import org.hisp.dhis.android.core.user.UserCredentials;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SingleIdentifiableStoreFactoryShould {
    private DeletedObjectHandler deletedObjectHandler;

    @Mock
    private DatabaseAdapter mDatabaseAdapter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_null_exception_when_handle_deleted_unsupported_type() {
        DeletedObject deletedObject = givenADeletedObjectByClass(
                UserCredentials.class.getSimpleName());

        IdentifiableStoreFactory identifiableStoreFactory =
                new IdentifiableStoreFactory(mDatabaseAdapter);

        identifiableStoreFactory.getByKlass(deletedObject.klass());
    }

    private DeletedObject givenADeletedObjectByClass(String klass) {
        return DeletedObject.create("xxx", klass, null, "");
    }
}
