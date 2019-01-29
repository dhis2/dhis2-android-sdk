package org.hisp.dhis.android.core.category;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyIdentifiableCollectionRepository;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class CategoryCollectionRepositoryMockIntegrationShould extends MockIntegrationShould {

    private final String BEFORE_DATE = "2007-12-24T12:24:25.203";
    private final String IN_BETWEEN_DATE = "2016-04-16T18:04:34.745";
    private final String AFTER_DATE =  "2017-12-24T12:24:25.203";

    private final String BIRTH_UID =  "m2jTvAj5kkm";
    private final String DEFAULT_UID =  "p0KPaWEg3cf";

    @BeforeClass
    public static void setUpAll() throws Exception {
        downloadMetadata();
    }

    @Test
    public void find_objects_with_equal_name() {
        ReadOnlyIdentifiableCollectionRepository<Category, ReadOnlyIdentifiableCollectionRepository<Category, >> repositoryWithUpdatedScope = d2.categoryModule().categories;
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(1));
        assertThat(combos.get(0).uid(), is(BIRTH_UID));
    }

}