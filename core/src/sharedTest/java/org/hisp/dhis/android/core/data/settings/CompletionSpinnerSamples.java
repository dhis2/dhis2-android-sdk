package org.hisp.dhis.android.core.data.settings;

import org.hisp.dhis.android.core.settings.CompletionSpinner;

public class CompletionSpinnerSamples {

    public static CompletionSpinner getCompletionSpinner() {
        return CompletionSpinner.builder()
                .id(1L)
                .uid("aBcDeFg")
                .visible(true)
                .build();
    }
}
