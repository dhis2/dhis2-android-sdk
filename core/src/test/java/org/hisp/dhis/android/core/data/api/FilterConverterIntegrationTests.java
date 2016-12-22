package org.hisp.dhis.android.core.data.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

import okhttp3.ResponseBody;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class FilterConverterIntegrationTests {

    @Test
    public void retrofit_shouldRespectConverter() throws IOException, InterruptedException {
        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.start();

        mockWebServer.enqueue(new MockResponse());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(FilterConverterFactory.create())
                .build();

        TestService testService = retrofit.create(TestService.class);
        testService.test(Filter.<String>builder()
                .fields(
                        Field.<String, String>create("property_one"),
                        Field.<String, String>create("property_two"),
                        NestedField.<String, String>create("nested_property").with(
                                Field.<String, String>create("nested_property_one")))
                .build())
                .execute();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertThat(recordedRequest.getPath()).isEqualTo(
                "/api/?fields=property_one,property_two,nested_property[nested_property_one]");

        mockWebServer.shutdown();
    }

    interface TestService {
        @GET("api/")
        retrofit2.Call<ResponseBody> test(@Query("fields") @Fields Filter<String> filter);
    }
}
