package org.hisp.dhis.android.core.option;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hisp.dhis.android.core.data.TestConstants.DEFAULT_IS_TRANSLATION_ON;
import static org.hisp.dhis.android.core.data.TestConstants.DEFAULT_TRANSLATION_LOCALE;
import static org.mockito.Mockito.when;

import org.hamcrest.MatcherAssert;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.data.file.ResourcesFileReader;
import org.hisp.dhis.android.core.data.server.RetrofitFactory;
import org.hisp.dhis.android.core.data.server.api.Dhis2MockServer;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Date;


import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Retrofit;

@RunWith(JUnit4.class)
public class OptionSetCallShould {
    @Mock
    private DatabaseAdapter mockDatabase;

    @Mock
    private Transaction mockTransaction;

    @Mock
    private ResourceHandler mockResourceHandler;

    @Mock
    private OptionSetHandler mockOptionSetHandler;

    private Dhis2MockServer dhis2MockServer;

    private OptionSetCall optionSetCall;

    @Before
    public void setUp() throws IOException {

        dhis2MockServer = new Dhis2MockServer(new ResourcesFileReader());
        Retrofit retrofit = RetrofitFactory.build(dhis2MockServer.getBaseEndpoint());

        MockitoAnnotations.initMocks(this);
        OptionSetService mockService = retrofit.create(OptionSetService.class);

        when(mockDatabase.beginNewTransaction()).thenReturn(mockTransaction);

        OptionSetQuery optionSetQuery = OptionSetQuery.defaultQuery();

        optionSetCall = new OptionSetCall(mockService, mockOptionSetHandler, mockDatabase,
                mockResourceHandler, new Date(), optionSetQuery
        );
    }

    @After
    public void tearDown() throws IOException {
        dhis2MockServer.shutdown();
    }

    @Test
    public void append_translation_variables_to_the_query_string()
            throws Exception {

        whenCallOptionSetCall();

        thenAssertTranslationParametersAreIncluded();
    }

    private void whenCallOptionSetCall() throws Exception {

        dhis2MockServer.enqueueMockResponse();
        optionSetCall.call();
    }

    private void thenAssertTranslationParametersAreIncluded() throws InterruptedException {
        RecordedRequest request = dhis2MockServer.takeRequest();

        MatcherAssert.assertThat(request.getPath(), containsString(
                "translation=" + DEFAULT_IS_TRANSLATION_ON + "&locale="
                        + DEFAULT_TRANSLATION_LOCALE));
    }


}
