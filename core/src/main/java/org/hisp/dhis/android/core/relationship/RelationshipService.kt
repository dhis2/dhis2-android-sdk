package org.hisp.dhis.android.core.relationship

interface RelationshipService {
    fun hasAccessPermission(relationshipType: RelationshipType): Boolean
}
