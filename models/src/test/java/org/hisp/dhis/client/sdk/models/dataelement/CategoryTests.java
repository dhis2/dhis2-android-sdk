package org.hisp.dhis.client.sdk.models.dataelement;

import org.hisp.dhis.client.sdk.models.BaseIdentifiableObjectVerifier;
import org.hisp.dhis.client.sdk.models.common.BaseIdentifiableObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class CategoryTests {

    private static BaseIdentifiableObject TEST_OBJECT;
    private static BaseIdentifiableObjectVerifier VERIFIER;

    @Before
    public void setup() {
        Date date = new java.util.Date();

        // TODO: Setup valid model object using Builder
        BaseIdentifiableObject category = new Category();
        category.setUid("genericUid1");
        category.setCreated(date);
        category.setLastUpdated(date);
        TEST_OBJECT = category;

        VERIFIER = new BaseIdentifiableObjectVerifier();
    }

    //************************************************************
    //
    // Validate exceptions
    //
    //************************************************************

    // Exceptions of BaseIdentifiableObject

    // TODO: How should the validate with Category null be tested?
    @Test(expected = IllegalArgumentException.class)
    public void testValidate_exceptionOnNullObject() {
        VERIFIER.validate(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidate_exceptionOnEmptyObject() {
        // TODO: Setup empty model object using builder
        VERIFIER.validate(new Category());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidate_exceptionOnUidNull() {
        VERIFIER.validateWithUid(TEST_OBJECT, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidate_exceptionOnUidLengthNot11() {
        VERIFIER.validateWithUid(TEST_OBJECT, "1234567890");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidate_exceptionOnCreatedNull() {
        VERIFIER.validateWithCreated(TEST_OBJECT, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidate_exceptionOnLastUpdatedNull() {
        VERIFIER.validateWithLastUpdated(TEST_OBJECT, null);
    }

    // TODO: Add exception tests for the implemented subclass

    /**
     * Validate valid object
     */
    @Test
    public void testValidate() {
        VERIFIER.validate(TEST_OBJECT);
    }

    // TODO: Add EqualsVerifier
}
