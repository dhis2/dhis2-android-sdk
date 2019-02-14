package org.hisp.dhis.android.testapp.category;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.category.CategoryOption;
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
public class CategoryOptionRepositoryMockIntegrationShould extends MockIntegrationShould {

    @BeforeClass
    public static void setUpAll() throws Exception {
        downloadMetadata();
    }

    @Test
    public void find_all() {
        List<CategoryOption> options = d2.categoryModule().categoryOptions.get();
        assertThat(options.size(), is(8));
    }

    @Test
    public void filter_by_start_date() throws ParseException {
        Date date = BaseIdentifiableObject.DATE_FORMAT.parse("2012-12-24T12:24:24.000");
        List<CategoryOption> options = d2.categoryModule().categoryOptions
                .byStartDate().eq(date)
                .get();
        assertThat(options.size(), is(1));
    }

    @Test
    public void filter_by_end_date() throws ParseException {
        Date date = BaseIdentifiableObject.DATE_FORMAT.parse("2013-12-24T12:24:24.777");
        List<CategoryOption> options = d2.categoryModule().categoryOptions
                .byEndDate().eq(date)
                .get();
        assertThat(options.size(), is(1));
    }

    @Test
    public void filter_by_access_data_write() {
        List<CategoryOption> options = d2.categoryModule().categoryOptions
                .byAccessDataWrite().isTrue()
                .get();
        assertThat(options.size(), is(5));
    }
}