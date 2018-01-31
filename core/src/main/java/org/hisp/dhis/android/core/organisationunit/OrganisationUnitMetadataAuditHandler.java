package org.hisp.dhis.android.core.organisationunit;

import android.util.Log;

import org.hisp.dhis.android.core.audit.AuditType;
import org.hisp.dhis.android.core.audit.MetadataAudit;
import org.hisp.dhis.android.core.audit.MetadataAuditHandler;
import org.hisp.dhis.android.core.user.AuthenticatedUserModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrganisationUnitMetadataAuditHandler implements MetadataAuditHandler {

    private final OrganisationUnitFactory organisationUnitFactory;

    public OrganisationUnitMetadataAuditHandler(OrganisationUnitFactory organisationUnitFactory) {
        this.organisationUnitFactory = organisationUnitFactory;
    }

    public void handle(MetadataAudit metadataAudit) throws Exception {
        OrganisationUnit organisationUnit = (OrganisationUnit) metadataAudit.getValue();

        if (metadataAudit.getType() == AuditType.UPDATE) {
            //metadataAudit of UPDATE type does not return payload
            //It's necessary sync by metadata call

            Set<String> uIds = new HashSet<>();
            uIds.add(metadataAudit.getUid());
            String userUId = organisationUnitFactory.getUserOrganisationUnitLinkStore()
                    .queryUserUIdByOrganisationUnitUId(organisationUnit.uid());
            if (userUId == null) {
                Log.e(this.getClass().getSimpleName(),
                        "MetadataAudit Error: Organisation Unit is created on server but organisation unit user does not exists in "
                                + "local: "
                                + metadataAudit);
            }
            //organisationUnitFactory.getOrganisationUnitHandler().newEndPointCall(uIds, metadataAudit.getCreatedAt()).call();
        } else {
            if (metadataAudit.getType() == AuditType.DELETE) {
                organisationUnit = organisationUnit.toBuilder().deleted(true).build();
            }
            List<AuthenticatedUserModel> authenticatedUserModelList = organisationUnitFactory.getAuthenticatedUserStore().query();
            if(authenticatedUserModelList.size()==0){
                Log.e(this.getClass().getSimpleName(),
                        "MetadataAudit Error: Organisation Unit is created on server but authenticated User does not exists in "
                                + "local: "
                                + metadataAudit);
            }else {
                organisationUnitFactory.getOrganisationUnitHandler().handleOrganisationUnit(
                        organisationUnit, OrganisationUnitModel.Scope.SCOPE_DATA_CAPTURE, authenticatedUserModelList.get(0).user());
            }
        }
    }
}