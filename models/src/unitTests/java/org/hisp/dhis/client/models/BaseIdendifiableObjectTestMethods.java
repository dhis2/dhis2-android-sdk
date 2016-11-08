package org.hisp.dhis.client.models;

public interface BaseIdendifiableObjectTestMethods {


    void build_shouldThrowOnNullUidField();

    void isValid_shouldReturnFalseOnMalformedUid();

    void isValid_shouldReturnFalseOnNullCreatedField();

    void isValid_shouldReturnFalseOnNullLastUpdatedField();

    void isValid_shouldReturnTrueOnValidObject();

    void equals_shouldConformToContract();
}
