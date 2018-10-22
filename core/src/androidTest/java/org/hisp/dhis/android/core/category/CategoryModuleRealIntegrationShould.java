package org.hisp.dhis.android.core.category;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;

public class CategoryModuleRealIntegrationShould extends AbsStoreTestCase {

    private D2 d2;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());
    }

    @Test
    public void let_client_access_category_combos() throws Exception {
        d2.logIn("android", "Android123").call();
        d2.syncMetaData().call();
        Set<CategoryCombo> combos = d2.categoryModule().categoryCombos.getSetWithAllChildren();
        CategoryCombo combo = d2.categoryModule().categoryCombos.uid("m2jTvAj5kkm").getWithAllChildren();
    }

    @Test
    public void let_client_access_categories() throws Exception {
        d2.logIn("android", "Android123").call();
        d2.syncMetaData().call();
        Set<Category> categories = d2.categoryModule().categories.getSetWithAllChildren();
        Category category = d2.categoryModule().categories.uid("YNZyaJHiHYq").getWithAllChildren();
    }
}
