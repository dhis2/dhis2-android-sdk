package org.hisp.dhis.android.core.organisationunit;

import android.util.Log;

import org.hisp.dhis.android.core.audit.AuditType;
import org.hisp.dhis.android.core.audit.MetadataAudit;
import org.hisp.dhis.android.core.audit.MetadataAuditHandler;
import org.hisp.dhis.android.core.user.AuthenticatedUserModel;
import org.hisp.dhis.android.core.user.User;

import java.util.List;

public class OrganisationUnitMetadataAuditHandler implements MetadataAuditHandler {

    private final OrganisationUnitFactory organisationUnitFactory;
    private final boolean isTranslationOn;
    private final String translationLocale;

    public OrganisationUnitMetadataAuditHandler(OrganisationUnitFactory organisationUnitFactory,
            boolean isTranslationOn, String translationLocale) {
        this.organisationUnitFactory = organisationUnitFactory;
        this.isTranslationOn = isTranslationOn;
        this.translationLocale = translationLocale;
    }

    @Override
    public void handle(MetadataAudit metadataAudit) throws Exception {
        OrganisationUnit organisationUnit = (OrganisationUnit) metadataAudit.getValue();

        if (metadataAudit.getType() == AuditType.UPDATE) {
            //metadataAudit of UPDATE type does not return payload
            //It's necessary sync by metadata call

            String userUId = organisationUnitFactory.getUserOrganisationUnitLinkStore()
                    .queryUserUIdByOrganisationUnitUId(metadataAudit.getUid());
            if (userUId == null) {
                Log.e(this.getClass().getSimpleName(),
                        "MetadataAudit Error: Organisation Unit is updated "
                                + "on server but organisation unit user does not exists in "
                                + "local: "
                                + metadataAudit);
                return;
            }
            User user = organisationUnitFactory.getUserStore().queryByUId(userUId);
            if (user == null) {
                Log.e(this.getClass().getSimpleName(),
                        "MetadataAudit Error: Organisation Unit is updated "
                                + "on server but organisation unit user does not exists in "
                                + "local: "
                                + metadataAudit);
                return;
            }
            OrganisationUnitQuery organisationUnitQuery =
                    OrganisationUnitQuery.defaultQuery(user, isTranslationOn, translationLocale,
                            metadataAudit.getUid());
            organisationUnitFactory.newEndPointCall(metadataAudit.getCreatedAt(),
                    organisationUnitQuery).call();
        } else {
            if (metadataAudit.getType() == AuditType.DELETE) {
                organisationUnit = organisationUnit.toBuilder().deleted(true).build();
            }
            List<AuthenticatedUserModel> authenticatedUserModelList = organisationUnitFactory
                    .getAuthenticatedUserStore().query();
            if (authenticatedUserModelList.isEmpty()) {
                Log.e(this.getClass().getSimpleName(),
                        "MetadataAudit Error: Organisation Unit is created "
                                + "on server but authenticated User does not exists in "
                                + "local: "
                                + metadataAudit);
            } else {
                organisationUnitFactory.getOrganisationUnitHandler().handleOrganisationUnit(
                        organisationUnit, OrganisationUnitModel.Scope.SCOPE_DATA_CAPTURE,
                        authenticatedUserModelList.get(0).user(),
                        metadataAudit.getCreatedAt());
            }
        }
    }
}