package org.hisp.dhis.client.sdk.ui.activities;

/**
 * An interface meant to comunicate the selected reportEntity uid between
 * a Fragment and an Activity that the fragment is attatched to.
 * (The Activity would implement this interface,
 * whilst the Fragment onAttach/onDetach would type check it against this interface
 * & set it as callback using this interface).
 */
public interface ReportEntitySelection {

    void setSelectedUid(String uid);

    String getSelectedUid();
}
