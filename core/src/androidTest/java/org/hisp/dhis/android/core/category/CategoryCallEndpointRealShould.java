package org.hisp.dhis.android.core.category;


import android.support.test.InstrumentationRegistry;
import android.util.Log;

import com.google.common.truth.Truth;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.hisp.dhis.android.core.data.api.BasicAuthenticatorFactory;
import org.hisp.dhis.android.core.data.api.ResponseValidator;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.junit.Before;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class CategoryCallEndpointRealShould extends AbsStoreTestCase {

    private D2 d2;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        DbOpenHelper dbOpenHelper = new DbOpenHelper(
                InstrumentationRegistry.getTargetContext().getApplicationContext()
                , null);

        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());
    }

//    @Test
    public void parse_categories() throws Exception {
        retrofit2.Response response = null;
        response = d2.logIn(RealServerMother.user, RealServerMother.password).call();
        Truth.assertThat(response.isSuccessful()).isTrue();


        CategoryService service = d2.retrofit().create(CategoryService.class);
        ResponseValidator<Category> validator = new ResponseValidator<>();

        CategoryQuery query = CategoryQuery.builder().page(1).pageSize(
                CategoryQuery.DEFAULT_PAGE_SIZE).paging(true).build();

        CategoryCallEndpoint categoryCallEndpoint = new CategoryCallEndpoint(query, service,
                validator, databaseAdapter());

        Payload<Category> payload = categoryCallEndpoint.call().body();
//
        Log.d("",payload.toString());
    }


    public static class D2Factory {
        public static D2 create(String url, DatabaseAdapter databaseAdapter) {
            ConfigurationModel config = ConfigurationModel.builder()
                    .serverUrl(HttpUrl.parse(url))
                    .build();

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            return new D2.Builder()
                    .configuration(config)
                    .databaseAdapter(databaseAdapter)
                    .okHttpClient(
                            new OkHttpClient.Builder()
                                    .addInterceptor(
                                            BasicAuthenticatorFactory.create(databaseAdapter))
                                    .addInterceptor(loggingInterceptor)
                                    .build()
                    ).build();

        }
    }

}
