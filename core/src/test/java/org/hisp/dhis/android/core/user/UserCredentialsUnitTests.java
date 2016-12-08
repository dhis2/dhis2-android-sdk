package org.hisp.dhis.android.core.user;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

// ToDo: consider testing .isValid() as well.
@RunWith(JUnit4.class)
public class UserCredentialsUnitTests {

    @Test
    public void equals_shouldConformToContract() {
        EqualsVerifier.forClass(UserCredentialsModel.builder().build().getClass())
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }
}
