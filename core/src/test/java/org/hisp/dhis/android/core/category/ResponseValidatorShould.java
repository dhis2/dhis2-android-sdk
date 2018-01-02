package org.hisp.dhis.android.core.category;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.hisp.dhis.android.core.common.Payload;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class ResponseValidatorShould {


    private Response<Payload<Category>> response;

    @Mock
    private Payload<Category> mockPayload;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void return_true_on_valid_response() throws Exception {

        stubbingPayloadWith(Collections.<Category>emptyList());

        stubbingResponseToBeSuccessful();

        ResponseValidator<Category> validator = new ResponseValidator<>();

        boolean isValidResponse = validator.isValid(response) ;

        assertTrue(isValidResponse);

    }

    @Test
    public void return_false_on_un_successful_response(){

        mockingResponseForA404Error();

        ResponseValidator<Category> validator = new ResponseValidator<>();

        boolean isValidResponse = validator.isValid(response) ;

        assertFalse(isValidResponse);
    }

    @Test
    public void return_false_on_a_body_with_null_items(){

        stubbingPayloadWith(null);

        stubbingResponseToBeSuccessful();

        ResponseValidator<Category> validator = new ResponseValidator<>();

        boolean isValidResponse = validator.isValid(response) ;

        assertFalse(isValidResponse);
    }

    private void stubbingResponseToBeSuccessful() {
        response = Response.success(mockPayload);
    }

    private  void stubbingPayloadWith(List<Category> items) {
        when(mockPayload.items()).thenReturn(items);
    }

    private void mockingResponseForA404Error() {
        response = Response.error(404, ResponseBody.create(MediaType.parse("application/json"),"{}"));
    }


}