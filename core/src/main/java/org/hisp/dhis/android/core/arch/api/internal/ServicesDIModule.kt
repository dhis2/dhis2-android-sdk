package org.hisp.dhis.android.core.arch.api.internal

import io.ktor.client.HttpClient
import org.hisp.dhis.android.core.attribute.internal.AttributeService
import org.hisp.dhis.android.core.category.internal.CategoryComboService
import org.hisp.dhis.android.core.category.internal.CategoryOptionService
import org.hisp.dhis.android.core.category.internal.CategoryService
import org.hisp.dhis.android.core.constant.internal.ConstantService
import org.hisp.dhis.android.core.constant.internal.KtorConstantService
import org.hisp.dhis.android.core.dataapproval.internal.DataApprovalService
import org.hisp.dhis.android.core.dataelement.internal.DataElementService
import org.hisp.dhis.android.core.dataset.internal.DataSetCompleteRegistrationService
import org.hisp.dhis.android.core.dataset.internal.DataSetService
import org.hisp.dhis.android.core.datavalue.internal.DataValueService
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentService
import org.hisp.dhis.android.core.event.internal.EventFilterService
import org.hisp.dhis.android.core.event.internal.EventService
import org.hisp.dhis.android.core.expressiondimensionitem.internal.ExpressionDimensionItemService
import org.hisp.dhis.android.core.fileresource.internal.FileResourceService
import org.hisp.dhis.android.core.icon.internal.IconService
import org.hisp.dhis.android.core.indicator.internal.IndicatorService
import org.hisp.dhis.android.core.indicator.internal.IndicatorTypeService
import org.hisp.dhis.android.core.legendset.internal.LegendSetService
import org.hisp.dhis.android.core.map.layer.internal.bing.BingService
import org.hisp.dhis.android.core.map.layer.internal.externalmap.ExternalMapLayerService
import org.hisp.dhis.android.core.option.internal.OptionGroupService
import org.hisp.dhis.android.core.option.internal.OptionService
import org.hisp.dhis.android.core.option.internal.OptionSetService
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitLevelService
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitService
import org.hisp.dhis.android.core.program.internal.ProgramIndicatorService
import org.hisp.dhis.android.core.program.internal.ProgramRuleService
import org.hisp.dhis.android.core.program.internal.ProgramService
import org.hisp.dhis.android.core.program.internal.ProgramStageService
import org.hisp.dhis.android.core.programstageworkinglist.internal.ProgramStageWorkingListService
import org.hisp.dhis.android.core.relationship.internal.RelationshipService
import org.hisp.dhis.android.core.relationship.internal.RelationshipTypeService
import org.hisp.dhis.android.core.settings.internal.SettingService
import org.hisp.dhis.android.core.sms.data.webapirepository.internal.ApiService
import org.hisp.dhis.android.core.systeminfo.internal.PingService
import org.hisp.dhis.android.core.systeminfo.internal.SystemInfoService
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeReservedValueService
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeService
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceFilterService
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceService
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityTypeService
import org.hisp.dhis.android.core.trackedentity.ownership.OwnershipService
import org.hisp.dhis.android.core.tracker.exporter.TrackerExporterService
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerImporterService
import org.hisp.dhis.android.core.usecase.stock.internal.StockUseCaseService
import org.hisp.dhis.android.core.user.internal.AuthorityService
import org.hisp.dhis.android.core.user.internal.UserService
import org.hisp.dhis.android.core.validation.internal.ValidationRuleService
import org.hisp.dhis.android.core.visualization.internal.TrackerVisualizationService
import org.hisp.dhis.android.core.visualization.internal.VisualizationService
import org.koin.dsl.module
import retrofit2.Retrofit

internal val servicesDIModule = module {
    single { get<Retrofit>().create(ApiService::class.java) }
    single { get<Retrofit>().create(AuthorityService::class.java) }
    single { get<Retrofit>().create(BingService::class.java) }
    single { get<Retrofit>().create(CategoryComboService::class.java) }
    single { get<Retrofit>().create(CategoryOptionService::class.java) }
    single { get<Retrofit>().create(CategoryService::class.java) }
    single { get<Retrofit>().create(ConstantService::class.java) }
    single { KtorConstantService(get<HttpClient>()) }
    single { get<Retrofit>().create(DataApprovalService::class.java) }
    single { get<Retrofit>().create(DataElementService::class.java) }
    single { get<Retrofit>().create(DataSetCompleteRegistrationService::class.java) }
    single { get<Retrofit>().create(DataSetService::class.java) }
    single { get<Retrofit>().create(DataValueService::class.java) }
    single { get<Retrofit>().create(EnrollmentService::class.java) }
    single { get<Retrofit>().create(EventFilterService::class.java) }
    single { get<Retrofit>().create(EventService::class.java) }
    single { get<Retrofit>().create(ExpressionDimensionItemService::class.java) }
    single { get<Retrofit>().create(ExternalMapLayerService::class.java) }
    single { get<Retrofit>().create(FileResourceService::class.java) }
    single { get<Retrofit>().create(IconService::class.java) }
    single { get<Retrofit>().create(IndicatorService::class.java) }
    single { get<Retrofit>().create(IndicatorTypeService::class.java) }
    single { get<Retrofit>().create(LegendSetService::class.java) }
    single { get<Retrofit>().create(AttributeService::class.java) }
    single { get<Retrofit>().create(OptionGroupService::class.java) }
    single { get<Retrofit>().create(OptionService::class.java) }
    single { get<Retrofit>().create(OptionSetService::class.java) }
    single { get<Retrofit>().create(OrganisationUnitLevelService::class.java) }
    single { get<Retrofit>().create(OrganisationUnitService::class.java) }
    single { get<Retrofit>().create(OwnershipService::class.java) }
    single { get<Retrofit>().create(PingService::class.java) }
    single { get<Retrofit>().create(ProgramIndicatorService::class.java) }
    single { get<Retrofit>().create(ProgramRuleService::class.java) }
    single { get<Retrofit>().create(ProgramService::class.java) }
    single { get<Retrofit>().create(ProgramStageService::class.java) }
    single { get<Retrofit>().create(ProgramStageWorkingListService::class.java) }
    single { get<Retrofit>().create(RelationshipService::class.java) }
    single { get<Retrofit>().create(RelationshipTypeService::class.java) }
    single { get<Retrofit>().create(SettingService::class.java) }
    single { get<Retrofit>().create(StockUseCaseService::class.java) }
    single { get<Retrofit>().create(SystemInfoService::class.java) }
    single { get<Retrofit>().create(TrackedEntityAttributeReservedValueService::class.java) }
    single { get<Retrofit>().create(TrackedEntityAttributeService::class.java) }
    single { get<Retrofit>().create(TrackedEntityInstanceFilterService::class.java) }
    single { get<Retrofit>().create(TrackedEntityInstanceService::class.java) }
    single { get<Retrofit>().create(TrackedEntityTypeService::class.java) }
    single { get<Retrofit>().create(TrackerExporterService::class.java) }
    single { get<Retrofit>().create(TrackerImporterService::class.java) }
    single { get<Retrofit>().create(TrackerVisualizationService::class.java) }
    single { get<Retrofit>().create(UserService::class.java) }
    single { get<Retrofit>().create(ValidationRuleService::class.java) }
    single { get<Retrofit>().create(VisualizationService::class.java) }
}
