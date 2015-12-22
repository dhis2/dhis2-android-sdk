package org.hisp.dhis.client.sdk.ui.views.chainablepickerview;

import android.os.Parcelable;

/**
 * Common interface for items that can be represented in {@link SelectorListAdapter}
 */
public interface IPickable extends Parcelable {
    @Override String toString();
}
