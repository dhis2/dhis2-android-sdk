package org.hisp.dhis.android.testapp.arch;

import org.hisp.dhis.android.core.common.Model;
import org.junit.Test;

public abstract class BasePublicAccessShould<M extends Model> {

    public abstract M buildObject();

    @Test(expected = NullPointerException.class)
    public abstract void has_public_create_method();

    @Test
    public abstract void has_public_builder_method();

    @Test
    public abstract void has_public_to_builder_method();

    @Test
    public void has_public_to_content_values_method() {
        buildObject().toContentValues();
    }
}