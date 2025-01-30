/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.arch.api.fields.internal

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboFields
import org.hisp.dhis.android.core.common.internal.AccessFields
import org.hisp.dhis.android.core.dataapproval.internal.DataApprovalFields
import org.hisp.dhis.android.core.dataset.internal.DataSetCompleteRegistrationFields
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentFields
import org.hisp.dhis.android.core.enrollment.internal.NewEnrollmentFields
import org.hisp.dhis.android.core.event.internal.DateFilterPeriodFields
import org.hisp.dhis.android.core.event.internal.EventDataFilterFields
import org.hisp.dhis.android.core.event.internal.EventFields
import org.hisp.dhis.android.core.event.internal.EventQueryCriteriaFields
import org.hisp.dhis.android.core.event.internal.NewEventFields
import org.hisp.dhis.android.core.fileresource.internal.FileResourceFields
import org.hisp.dhis.android.core.legendset.internal.LegendFields
import org.hisp.dhis.android.core.map.layer.internal.externalmap.ExternalMapLayerFields
import org.hisp.dhis.android.core.note.internal.NewNoteFields
import org.hisp.dhis.android.core.note.internal.NoteFields
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitFields
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitGroupFields
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitLevelFields
import org.hisp.dhis.android.core.program.internal.ProgramRuleVariableFields
import org.hisp.dhis.android.core.program.internal.ProgramSectionFields
import org.hisp.dhis.android.core.program.internal.ProgramStageDataElementFields
import org.hisp.dhis.android.core.program.internal.ProgramStageSectionFields
import org.hisp.dhis.android.core.program.internal.ProgramTrackedEntityAttributeFields
import org.hisp.dhis.android.core.programstageworkinglist.internal.ProgramStageQueryCriteriaFields
import org.hisp.dhis.android.core.programstageworkinglist.internal.ProgramStageWorkingListAttributeValueFilterFields
import org.hisp.dhis.android.core.programstageworkinglist.internal.ProgramStageWorkingListEventDataFilterFields
import org.hisp.dhis.android.core.relationship.RelationshipFields
import org.hisp.dhis.android.core.relationship.internal.NewRelationshipFields
import org.hisp.dhis.android.core.relationship.internal.NewRelationshipItemEnrollmentFields
import org.hisp.dhis.android.core.relationship.internal.NewRelationshipItemEventFields
import org.hisp.dhis.android.core.relationship.internal.NewRelationshipItemFields
import org.hisp.dhis.android.core.relationship.internal.NewRelationshipItemTrackedEntityInstanceFields
import org.hisp.dhis.android.core.relationship.internal.RelationshipConstraintFields
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemEnrollmentFields
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemEventFields
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemFields
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemTrackedEntityInstanceFields
import org.hisp.dhis.android.core.relationship.internal.RelationshipTypeFields
import org.hisp.dhis.android.core.relationship.internal.TrackerDataViewFields
import org.hisp.dhis.android.core.settings.internal.SystemSettingsFields
import org.hisp.dhis.android.core.settings.internal.UserSettingsFields
import org.hisp.dhis.android.core.systeminfo.internal.SystemInfoFields
import org.hisp.dhis.android.core.trackedentity.internal.AttributeValueFilterFields
import org.hisp.dhis.android.core.trackedentity.internal.EntityQueryCriteriaFields
import org.hisp.dhis.android.core.trackedentity.internal.NewTrackedEntityAttributeValueFields
import org.hisp.dhis.android.core.trackedentity.internal.NewTrackedEntityDataValueFields
import org.hisp.dhis.android.core.trackedentity.internal.NewTrackedEntityInstanceFields
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeValueFields
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueFields
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceEventFilterFields
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceFields
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityTypeAttributeFields
import org.hisp.dhis.android.core.user.internal.UserCredentialsFields
import org.hisp.dhis.android.core.user.internal.UserGroupFields
import org.hisp.dhis.android.core.user.internal.UserRoleFields
import org.hisp.dhis.android.core.validation.internal.ValidationRuleExpressionFields
import org.hisp.dhis.android.network.attribute.AttributeFields
import org.hisp.dhis.android.network.attribute.AttributeValueFields
import org.hisp.dhis.android.network.category.CategoryFields
import org.hisp.dhis.android.network.categorycombo.CategoryComboFields
import org.hisp.dhis.android.network.categoryoption.CategoryOptionFields
import org.hisp.dhis.android.network.common.fields.DataAccessFields
import org.hisp.dhis.android.network.common.fields.Fields
import org.hisp.dhis.android.network.common.fields.ObjectStyleFields
import org.hisp.dhis.android.network.common.fields.Property
import org.hisp.dhis.android.network.constant.ConstantFields
import org.hisp.dhis.android.network.customicon.CustomIconFields
import org.hisp.dhis.android.network.dataelement.DataElementFields
import org.hisp.dhis.android.network.dataset.DataElementOperandFields
import org.hisp.dhis.android.network.dataset.DataInputPeriodFields
import org.hisp.dhis.android.network.dataset.DataSetElementFields
import org.hisp.dhis.android.network.dataset.DataSetFields
import org.hisp.dhis.android.network.dataset.SectionFields
import org.hisp.dhis.android.network.datavalue.DataValueFields
import org.hisp.dhis.android.network.eventfilter.EventFilterFields
import org.hisp.dhis.android.network.expressiondimensionitem.ExpressionDimensionItemFields
import org.hisp.dhis.android.network.indicator.IndicatorFields
import org.hisp.dhis.android.network.indicatortype.IndicatorTypeFields
import org.hisp.dhis.android.network.legendset.LegendSetFields
import org.hisp.dhis.android.network.option.OptionFields
import org.hisp.dhis.android.network.optiongroup.OptionGroupFields
import org.hisp.dhis.android.network.optionset.OptionSetFields
import org.hisp.dhis.android.network.program.ProgramFields
import org.hisp.dhis.android.network.programindicator.AnalyticsPeriodBoundaryFields
import org.hisp.dhis.android.network.programindicator.ProgramIndicatorFields
import org.hisp.dhis.android.network.programrule.ProgramRuleActionFields
import org.hisp.dhis.android.network.programrule.ProgramRuleFields
import org.hisp.dhis.android.network.programstage.ProgramStageFields
import org.hisp.dhis.android.network.programstageworkinglist.ProgramStageWorkingListFields
import org.hisp.dhis.android.network.trackedentityattribute.TrackedEntityAttributeFields
import org.hisp.dhis.android.network.trackedentityinstancefilter.TrackedEntityInstanceFilterFields
import org.hisp.dhis.android.network.trackedentitytype.TrackedEntityTypeFields
import org.hisp.dhis.android.network.trackervisualization.TrackerVisualizationDimensionFields
import org.hisp.dhis.android.network.trackervisualization.TrackerVisualizationDimensionRepetitionFields
import org.hisp.dhis.android.network.trackervisualization.TrackerVisualizationFields
import org.hisp.dhis.android.network.user.UserFields
import org.hisp.dhis.android.network.validationrule.ValidationRuleFields
import org.hisp.dhis.android.network.visualization.VisualizationDimensionFields
import org.hisp.dhis.android.network.visualization.VisualizationDimensionItemFields
import org.hisp.dhis.android.network.visualization.VisualizationFields
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@Ignore("Test for fields string generation. Only to be executed on demand")
@RunWith(JUnit4::class)
class FieldsStringGenerationShould {

    @Test
    fun generate_not_empty_string() {
        val sortedFieldsClasses = fieldsClasses.sortedBy { it::class.simpleName }

        sortedFieldsClasses.forEach { fieldsClass ->
            fieldsClass::class.members
                .filter { it.returnType.toString().contains("Fields") }
                .forEach { member ->
                    val fieldInstance = try {
                        member.call(fieldsClass) as? Fields<*>
                    } catch (e: Exception) {
                        null
                    }

                    fieldInstance?.let { field ->
                        val sortedFields = field.fields.sortedBy { it.name }

                        @Suppress("UNCHECKED_CAST")
                        val sortedField = Fields(sortedFields as List<Property<Any>>)

                        val generatedString = sortedField.generateString()
                        println("${fieldsClass::class.simpleName}.${member.name}: $generatedString")

                        assertThat(generatedString).isNotEmpty()
                    }
                }
        }
    }

    private val fieldsClasses = listOf(
        AccessFields,
        AnalyticsPeriodBoundaryFields,
        AttributeFields,
        AttributeValueFilterFields,
        AttributeValueFields,
        CategoryComboFields,
        CategoryFields,
        CategoryOptionComboFields,
        CategoryOptionFields,
        ConstantFields,
        CustomIconFields,
        DataAccessFields,
        DataApprovalFields,
        DataElementFields,
        DataElementOperandFields,
        DataInputPeriodFields,
        DataSetCompleteRegistrationFields,
        DataSetElementFields,
        DataSetFields,
        DataValueFields,
        DateFilterPeriodFields,
        EnrollmentFields,
        EntityQueryCriteriaFields,
        EventDataFilterFields,
        EventFields,
        EventFilterFields,
        EventQueryCriteriaFields,
        ExpressionDimensionItemFields,
        ExternalMapLayerFields,
        FileResourceFields,
        IndicatorFields,
        IndicatorTypeFields,
        LegendFields,
        LegendSetFields,
        NewEnrollmentFields,
        NewEventFields,
        NewNoteFields,
        NewRelationshipFields,
        NewRelationshipItemEnrollmentFields,
        NewRelationshipItemEventFields,
        NewRelationshipItemFields,
        NewRelationshipItemTrackedEntityInstanceFields,
        NewTrackedEntityAttributeValueFields,
        NewTrackedEntityDataValueFields,
        NewTrackedEntityInstanceFields,
        NoteFields,
        ObjectStyleFields,
        OptionFields,
        OptionGroupFields,
        OptionSetFields,
        OrganisationUnitFields,
        OrganisationUnitGroupFields,
        OrganisationUnitLevelFields,
        ProgramFields,
        ProgramIndicatorFields,
        ProgramRuleActionFields,
        ProgramRuleFields,
        ProgramRuleVariableFields,
        ProgramSectionFields,
        ProgramStageDataElementFields,
        ProgramStageFields,
        ProgramStageQueryCriteriaFields,
        ProgramStageSectionFields,
        ProgramStageWorkingListAttributeValueFilterFields,
        ProgramStageWorkingListEventDataFilterFields,
        ProgramStageWorkingListFields,
        ProgramTrackedEntityAttributeFields,
        RelationshipConstraintFields,
        RelationshipFields,
        RelationshipItemEnrollmentFields,
        RelationshipItemEventFields,
        RelationshipItemFields,
        RelationshipItemTrackedEntityInstanceFields,
        RelationshipTypeFields,
        SectionFields,
        SystemInfoFields,
        SystemSettingsFields,
        TrackedEntityAttributeFields,
        TrackedEntityAttributeValueFields,
        TrackedEntityDataValueFields,
        TrackedEntityInstanceEventFilterFields,
        TrackedEntityInstanceFields,
        TrackedEntityInstanceFilterFields,
        TrackedEntityTypeAttributeFields,
        TrackedEntityTypeFields,
        TrackerDataViewFields,
        TrackerVisualizationDimensionFields,
        TrackerVisualizationDimensionRepetitionFields,
        TrackerVisualizationFields,
        UserCredentialsFields,
        UserFields,
        UserGroupFields,
        UserRoleFields,
        UserSettingsFields,
        ValidationRuleExpressionFields,
        ValidationRuleFields,
        VisualizationDimensionFields,
        VisualizationDimensionItemFields,
        VisualizationFields,
    )
}
