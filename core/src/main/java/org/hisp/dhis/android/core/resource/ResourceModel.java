/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.resource;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryOption;
import org.hisp.dhis.android.core.category.CategoryOptionCombo;
import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.data.database.DbDateColumnAdapter;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.option.Option;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramIndicator;
import org.hisp.dhis.android.core.program.ProgramRule;
import org.hisp.dhis.android.core.program.ProgramRuleAction;
import org.hisp.dhis.android.core.program.ProgramRuleVariable;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.program.ProgramStageDataElement;
import org.hisp.dhis.android.core.program.ProgramStageSection;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntity;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.user.User;

import java.util.Date;

@AutoValue
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ModifiedCyclomaticComplexity",
        "PMD.StdCyclomaticComplexity", "PMD.CouplingBetweenObjects", "PMD.ExcessiveImports"})
public abstract class ResourceModel extends BaseModel {
    public static final String TABLE = "Resource";

    public static class Columns extends BaseModel.Columns {
        public static final String RESOURCE_TYPE = "resourceType";
        public static final String LAST_SYNCED = "lastSynced";
    }

    public enum Type {
        EVENT,
        SYSTEM_INFO,
        USER,
        ORGANISATION_UNIT,
        PROGRAM,
        OPTION_SET,
        TRACKED_ENTITY,
        TRACKED_ENTITY_INSTANCE,
        CATEGORY,
        CATEGORY_COMBO,
        RELATIONSHIP_TYPE,
        TRACKED_ENTITY_ATTRIBUTE,
        DATA_ELEMENT,
        DELETED_USER,
        DELETED_ORGANISATION_UNIT,
        DELETED_PROGRAM,
        DELETED_OPTION_SET,
        DELETED_TRACKED_ENTITY,
        DELETED_CATEGORY,
        DELETED_CATEGORY_OPTION,
        DELETED_CATEGORY_COMBO,
        DELETED_CATEGORY_OPTION_COMBO,
        DELETED_DATA_ELEMENT,
        DELETED_OPTION,
        DELETED_PROGRAM_INDICATOR,
        DELETED_PROGRAM_RULE,
        DELETED_PROGRAM_RULE_ACTION,
        DELETED_PROGRAM_RULE_VARIABLE,
        DELETED_PROGRAM_STAGE,
        DELETED_PROGRAM_STAGE_DATA_ELEMENT,
        DELETED_PROGRAM_STAGE_SECTION,
        DELETED_PROGRAM_TRACKED_ENTITY_ATTRIBUTE,
        DELETED_TRACKED_ENTITY_ATTRIBUTE,
        DELETED_RELATIONSHIP_TYPE
    }

    @Nullable
    @ColumnName(Columns.RESOURCE_TYPE)
    public abstract String resourceType();

    @Nullable
    @ColumnName(Columns.LAST_SYNCED)
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date lastSynced();

    @NonNull
    public abstract ContentValues toContentValues();

    @NonNull
    public static ResourceModel create(Cursor cursor) {
        return AutoValue_ResourceModel.createFromCursor(cursor);
    }

    @NonNull
    public static Builder builder() {
        return new $$AutoValue_ResourceModel.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseModel.Builder<Builder> {
        public abstract Builder resourceType(@Nullable String resourceType);

        public abstract Builder lastSynced(@Nullable Date lastSynced);

        public abstract ResourceModel build();
    }

    public static ResourceModel.Type getResourceModelFromKlass(String klass) {
        if (klass.equals(User.class.getSimpleName())) {
            return ResourceModel.Type.DELETED_USER;
        } else if (klass.equals(OrganisationUnit.class.getSimpleName())) {
            return ResourceModel.Type.DELETED_ORGANISATION_UNIT;
        } else if (klass.equals(Program.class.getSimpleName())) {
            return ResourceModel.Type.DELETED_PROGRAM;
        } else if (klass.equals(OptionSet.class.getSimpleName())) {
            return ResourceModel.Type.DELETED_OPTION_SET;
        } else if (klass.equals(TrackedEntity.class.getSimpleName())) {
            return ResourceModel.Type.DELETED_TRACKED_ENTITY;
        } else if (klass.equals(Category.class.getSimpleName())) {
            return ResourceModel.Type.DELETED_CATEGORY;
        } else if (klass.equals(CategoryCombo.class.getSimpleName())) {
            return ResourceModel.Type.DELETED_CATEGORY_COMBO;
        } else if (klass.equals(CategoryOption.class.getSimpleName())) {
            return ResourceModel.Type.DELETED_CATEGORY_OPTION;
        } else if (klass.equals(CategoryOptionCombo.class.getSimpleName())) {
            return ResourceModel.Type.DELETED_CATEGORY_OPTION_COMBO;
        } else if (klass.equals(DataElement.class.getSimpleName())) {
            return ResourceModel.Type.DELETED_DATA_ELEMENT;
        } else if (klass.equals(Option.class.getSimpleName())) {
            return ResourceModel.Type.DELETED_OPTION;
        } else if (klass.equals(ProgramIndicator.class.getSimpleName())) {
            return ResourceModel.Type.DELETED_PROGRAM_INDICATOR;
        } else if (klass.equals(ProgramRule.class.getSimpleName())) {
            return ResourceModel.Type.DELETED_PROGRAM_RULE;
        } else if (klass.equals(ProgramRuleAction.class.getSimpleName())) {
            return ResourceModel.Type.DELETED_PROGRAM_RULE_ACTION;
        } else if (klass.equals(ProgramRuleVariable.class.getSimpleName())) {
            return ResourceModel.Type.DELETED_PROGRAM_RULE_VARIABLE;
        } else if (klass.equals(ProgramStage.class.getSimpleName())) {
            return ResourceModel.Type.DELETED_PROGRAM_STAGE;
        } else if (klass.equals(ProgramStageDataElement.class.getSimpleName())) {
            return ResourceModel.Type.DELETED_PROGRAM_STAGE_DATA_ELEMENT;
        } else if (klass.equals(ProgramStageSection.class.getSimpleName())) {
            return ResourceModel.Type.DELETED_PROGRAM_STAGE_SECTION;
        } else if (klass.equals(ProgramTrackedEntityAttribute.class.getSimpleName())) {
            return ResourceModel.Type.DELETED_PROGRAM_TRACKED_ENTITY_ATTRIBUTE;
        } else if (klass.equals(TrackedEntityAttribute.class.getSimpleName())) {
            return ResourceModel.Type.DELETED_TRACKED_ENTITY_ATTRIBUTE;
        } else if (klass.equals(RelationshipType.class.getSimpleName())) {
            return ResourceModel.Type.DELETED_RELATIONSHIP_TYPE;
        } else {
            throw new IllegalArgumentException("unsupported class");
        }
    }

}
