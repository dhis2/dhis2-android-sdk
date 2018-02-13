package org.hisp.dhis.android.core.deletedobject;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.server.api.Dhis2MockServer;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.util.Date;

public class DeletedObjectEndPointShould extends AbsStoreTestCase {

    private Dhis2MockServer dhis2MockServer;
    private D2 d2;

    @Mock
    private DeletedObjectService deletedObjectService;

    @Mock
    private ResourceHandler resourceHandler;

    @Mock
    private DeletedObjectHandler deleteObjectHandler;


    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

        d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());
    }


    @After
    public void tearDown() throws IOException {
        dhis2MockServer.shutdown();
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_if_type_is_not_supported() throws Exception {
        new DeletedObjectEndPointCall(deletedObjectService, resourceHandler,
                deleteObjectHandler, new Date(), "wrong_klass").call();
    }
}
