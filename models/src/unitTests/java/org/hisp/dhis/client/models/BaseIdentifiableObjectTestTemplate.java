package org.hisp.dhis.client.models;

import org.junit.Test;

public class BaseIdentifiableObjectTestTemplate implements BaseIdendifiableObjectTestMethods {

    // TODO: Set up valid builder object to use in tests

    //**************************************************************************************
    //
    // BASE IDENTIFIABLE OBJECT TESTS
    //
    //**************************************************************************************

    @Override
    @Test(expected = IllegalStateException.class)
    public void build_shouldThrowOnNullUidField() {
        throw new UnsupportedOperationException();
    }

    @Override
    @Test
    public void isValid_shouldReturnFalseOnMalformedUid() {
        throw new UnsupportedOperationException();
    }

    @Override
    @Test
    public void isValid_shouldReturnFalseOnNullCreatedField() {
        throw new UnsupportedOperationException();
    }

    @Override
    @Test
    public void isValid_shouldReturnFalseOnNullLastUpdatedField() {
        throw new UnsupportedOperationException();
    }

    @Override
    @Test
    public void isValid_shouldReturnTrueOnValidObject() {
        throw new UnsupportedOperationException();
    }

    //**************************************************************************************
    //
    // EQUALS VERIFIER
    //
    //**************************************************************************************

    @Override
    @Test
    public void equals_shouldConformToContract() {
        throw new UnsupportedOperationException();
    }

    //**************************************************************************************
    //
    // COLLECTION MUTATION TESTS
    //
    //**************************************************************************************

    // TODO: Implement tests for collection mutations if any
}
