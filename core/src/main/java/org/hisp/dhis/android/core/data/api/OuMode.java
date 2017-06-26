package org.hisp.dhis.android.core.data.api;

/**
 * Defines the selection of organisation units.
 *
 * <ul>
 * <li>SELECTED: specified units only.</li>
 * <li>CHILDREN: immediate children of specified units, including specified units.</li>
 * <li>DESCENDANTS: all units in sub-hierarchy of specified units, including specified units.</li>
 * <li>ALL: all units in system.</li>
 * </ul>
 *
 * @author Lars Helge Overland
 */

public enum OuMode {
    SELECTED,
    CHILDREN,
    DESCENDANTS,
    ACCESSIBLE,
    ALL
}
