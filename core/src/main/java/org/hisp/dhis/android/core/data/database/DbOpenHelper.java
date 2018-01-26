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

package org.hisp.dhis.android.core.data.database;

import static org.hisp.dhis.android.core.user.UserOrganisationUnitLinkModel.Columns
        .ORGANISATION_UNIT_SCOPE;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import org.hisp.dhis.android.core.category.CategoryCategoryComboLinkModel;
import org.hisp.dhis.android.core.category.CategoryComboModel;
import org.hisp.dhis.android.core.category.CategoryModel;
import org.hisp.dhis.android.core.category.CategoryOptionComboCategoryLinkModel;
import org.hisp.dhis.android.core.category.CategoryOptionComboModel;
import org.hisp.dhis.android.core.category.CategoryCategoryOptionLinkModel;
import org.hisp.dhis.android.core.category.CategoryOptionModel;
import org.hisp.dhis.android.core.configuration.ConfigurationModel;
import org.hisp.dhis.android.core.constant.ConstantModel;
import org.hisp.dhis.android.core.dataelement.DataElementModel;
import org.hisp.dhis.android.core.enrollment.EnrollmentModel;
import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.core.option.OptionModel;
import org.hisp.dhis.android.core.option.OptionSetModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkModel;
import org.hisp.dhis.android.core.program.ProgramIndicatorModel;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.hisp.dhis.android.core.program.ProgramRuleActionModel;
import org.hisp.dhis.android.core.program.ProgramRuleModel;
import org.hisp.dhis.android.core.program.ProgramRuleVariableModel;
import org.hisp.dhis.android.core.program.ProgramStageDataElementModel;
import org.hisp.dhis.android.core.program.ProgramStageModel;
import org.hisp.dhis.android.core.program.ProgramStageSectionModel;
import org.hisp.dhis.android.core.program.ProgramStageSectionProgramIndicatorLinkModel;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeModel;
import org.hisp.dhis.android.core.relationship.RelationshipModel;
import org.hisp.dhis.android.core.relationship.RelationshipTypeModel;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.systeminfo.SystemInfoModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModel;
import org.hisp.dhis.android.core.user.AuthenticatedUserModel;
import org.hisp.dhis.android.core.user.UserCredentialsModel;
import org.hisp.dhis.android.core.user.UserModel;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkModel;
import org.hisp.dhis.android.core.user.UserRoleModel;
import org.hisp.dhis.android.core.user.UserRoleProgramLinkModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

@SuppressWarnings({
        "PMD.AvoidDuplicateLiterals", "PMD.ExcessiveImports"
})
public class DbOpenHelper extends CustomSQLBriteOpenHelper {

    @VisibleForTesting
    static int VERSION = 3;
    public String mockedSqlDatabase = "";
    private static final String CREATE_CONFIGURATION_TABLE =
            "CREATE TABLE " + ConfigurationModel.CONFIGURATION + " (" +
                    ConfigurationModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ConfigurationModel.Columns.SERVER_URL + " TEXT NOT NULL UNIQUE" +
                    ");";

    private static final String CREATE_CATEGORY_TABLE =
            "CREATE TABLE " + CategoryModel.TABLE + " (" +
                    CategoryModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CategoryModel.Columns.UID + " TEXT NOT NULL UNIQUE," +
                    CategoryModel.Columns.CODE + " TEXT," +
                    CategoryModel.Columns.NAME + " TEXT," +
                    CategoryModel.Columns.DISPLAY_NAME + " TEXT," +
                    CategoryModel.Columns.CREATED + " TEXT," +
                    CategoryModel.Columns.LAST_UPDATED + " TEXT," +
                    CategoryModel.Columns.DATA_DIMENSION_TYPE + " TEXT" + ");";

    private static final String CREATE_CATEGORY_OPTION_TABLE =
            "CREATE TABLE " + CategoryOptionModel.TABLE + " (" +
                    CategoryModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CategoryModel.Columns.UID + " TEXT NOT NULL UNIQUE," +
                    CategoryModel.Columns.CODE + " TEXT," +
                    CategoryModel.Columns.NAME + " TEXT," +
                    CategoryModel.Columns.DISPLAY_NAME + " TEXT," +
                    CategoryModel.Columns.CREATED + " TEXT," +
                    CategoryModel.Columns.LAST_UPDATED + " TEXT" + ");";

    private static final String CREATE_CATEGORY_CATEGORY_OPTION_LINK_TABLE = "CREATE TABLE " +
            CategoryCategoryOptionLinkModel.TABLE + " (" +
            CategoryCategoryOptionLinkModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            CategoryCategoryOptionLinkModel.Columns.CATEGORY + " TEXT NOT NULL," +
            CategoryCategoryOptionLinkModel.Columns.CATEGORY_OPTION + " TEXT NOT NULL, " +
            " FOREIGN KEY (" + CategoryCategoryOptionLinkModel.Columns.CATEGORY + ") " +
            " REFERENCES " + CategoryModel.TABLE + " (" + CategoryModel.Columns.UID + ") " +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED," +
            " FOREIGN KEY (" + CategoryCategoryOptionLinkModel.Columns.CATEGORY_OPTION + ") " +
            " REFERENCES " + CategoryOptionModel.TABLE + " (" + CategoryOptionModel.Columns.UID
            + ")" +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED," +
            "UNIQUE (" + CategoryCategoryOptionLinkModel.Columns.CATEGORY + ", " +
            CategoryCategoryOptionLinkModel.Columns.CATEGORY_OPTION + ")" +
            ");";

    private static final String CREATE_CATEGORY_COMBO_TABLE =
            "CREATE TABLE " + CategoryComboModel.TABLE + " (" +
                    CategoryComboModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CategoryComboModel.Columns.UID + " TEXT NOT NULL UNIQUE," +
                    CategoryComboModel.Columns.CODE + " TEXT," +
                    CategoryComboModel.Columns.NAME + " TEXT," +
                    CategoryComboModel.Columns.DISPLAY_NAME + " TEXT," +
                    CategoryComboModel.Columns.CREATED + " TEXT," +
                    CategoryComboModel.Columns.LAST_UPDATED + " TEXT, " +
                    CategoryComboModel.Columns.IS_DEFAULT + " INTEGER" + ");";

    private static final String CREATE_CATEGORY_CATEGORY_COMBO_LINK_TABLE = "CREATE TABLE " +
            CategoryCategoryComboLinkModel.TABLE + " (" +
            CategoryCategoryComboLinkModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            CategoryCategoryComboLinkModel.Columns.CATEGORY + " TEXT NOT NULL," +
            CategoryCategoryComboLinkModel.Columns.CATEGORY_COMBO + " TEXT NOT NULL, " +
            " FOREIGN KEY (" + CategoryCategoryComboLinkModel.Columns.CATEGORY + ") " +
            " REFERENCES " + CategoryModel.TABLE + " (" + CategoryModel.Columns.UID + ") " +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED," +
            " FOREIGN KEY (" + CategoryCategoryComboLinkModel.Columns.CATEGORY_COMBO + ") " +
            " REFERENCES " + CategoryComboModel.TABLE + " (" + CategoryComboModel.Columns.UID + ")"
            +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED," +
            "UNIQUE (" + CategoryCategoryComboLinkModel.Columns.CATEGORY + ", " +
            CategoryCategoryComboLinkModel.Columns.CATEGORY_COMBO + ")" +
            ");";

    private static final String CREATE_CATEGORY_OPTION_COMBO_TABLE =
            "CREATE TABLE " + CategoryOptionComboModel.TABLE + " (" +
                    CategoryOptionComboModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    CategoryOptionComboModel.Columns.UID + " TEXT NOT NULL UNIQUE," +
                    CategoryOptionComboModel.Columns.CODE + " TEXT," +
                    CategoryOptionComboModel.Columns.NAME + " TEXT," +
                    CategoryOptionComboModel.Columns.DISPLAY_NAME + " TEXT," +
                    CategoryOptionComboModel.Columns.CREATED + " TEXT," +
                    CategoryOptionComboModel.Columns.LAST_UPDATED + " TEXT, " +
                    CategoryOptionComboModel.Columns.CATEGORY_COMBO + " TEXT, " +
                    " FOREIGN KEY (" + CategoryOptionComboModel.Columns.CATEGORY_COMBO + ") " +
                    " REFERENCES " + CategoryComboModel.TABLE + " ("
                    + CategoryComboModel.Columns.UID + ")" +
                    " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED );";

    private static final String CREATE_CATEGORY_OPTION_COMBO_CATEGORY_LINK_TABLE = "CREATE TABLE " +
            CategoryOptionComboCategoryLinkModel.TABLE + " (" +
            CategoryOptionComboCategoryLinkModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            +
            CategoryOptionComboCategoryLinkModel.Columns.CATEGORY_OPTION_COMBO + " TEXT NOT NULL," +
            CategoryOptionComboCategoryLinkModel.Columns.CATEGORY + " TEXT NOT NULL, " +
            " FOREIGN KEY (" + CategoryOptionComboCategoryLinkModel.Columns.CATEGORY_OPTION_COMBO + ") " +
            " REFERENCES " + CategoryOptionComboModel.TABLE + " ("
            + CategoryOptionComboModel.Columns.UID + ") " +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED," +
            "UNIQUE (" + CategoryOptionComboCategoryLinkModel.Columns.CATEGORY_OPTION_COMBO + ", " +
            CategoryOptionComboCategoryLinkModel.Columns.CATEGORY + ")" +
            ");";

    private static final String CREATE_USER_TABLE = "CREATE TABLE " + UserModel.TABLE + " (" +
            UserModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            UserModel.Columns.UID + " TEXT NOT NULL UNIQUE," +
            UserModel.Columns.CODE + " TEXT," +
            UserModel.Columns.NAME + " TEXT," +
            UserModel.Columns.DISPLAY_NAME + " TEXT," +
            UserModel.Columns.CREATED + " TEXT," +
            UserModel.Columns.LAST_UPDATED + " TEXT," +
            UserModel.Columns.BIRTHDAY + " TEXT," +
            UserModel.Columns.EDUCATION + " TEXT," +
            UserModel.Columns.GENDER + " TEXT," +
            UserModel.Columns.JOB_TITLE + " TEXT," +
            UserModel.Columns.SURNAME + " TEXT," +
            UserModel.Columns.FIRST_NAME + " TEXT," +
            UserModel.Columns.INTRODUCTION + " TEXT," +
            UserModel.Columns.EMPLOYER + " TEXT," +
            UserModel.Columns.INTERESTS + " TEXT," +
            UserModel.Columns.LANGUAGES + " TEXT," +
            UserModel.Columns.EMAIL + " TEXT," +
            UserModel.Columns.PHONE_NUMBER + " TEXT," +
            UserModel.Columns.NATIONALITY + " TEXT" +
            ");";

    private static final String CREATE_USER_CREDENTIALS_TABLE = "CREATE TABLE " +
            UserCredentialsModel.TABLE + " (" +
            UserCredentialsModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            UserCredentialsModel.Columns.UID + " TEXT NOT NULL UNIQUE," +
            UserCredentialsModel.Columns.CODE + " TEXT," +
            UserCredentialsModel.Columns.NAME + " TEXT," +
            UserCredentialsModel.Columns.DISPLAY_NAME + " TEXT," +
            UserCredentialsModel.Columns.CREATED + " TEXT," +
            UserCredentialsModel.Columns.LAST_UPDATED + " TEXT," +
            UserCredentialsModel.Columns.USERNAME + " TEXT," +
            UserCredentialsModel.Columns.USER + " TEXT NOT NULL UNIQUE," +
            " FOREIGN KEY (" + UserCredentialsModel.Columns.USER + ") " +
            "REFERENCES " + UserModel.TABLE + " (" + UserModel.Columns.UID + ") " +
            "ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED " +
            ");";

    private static final String CREATE_ORGANISATION_UNIT_TABLE = "CREATE TABLE " +
            OrganisationUnitModel.TABLE + " (" +
            OrganisationUnitModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            OrganisationUnitModel.Columns.UID + " TEXT NOT NULL UNIQUE," +
            OrganisationUnitModel.Columns.CODE + " TEXT," +
            OrganisationUnitModel.Columns.NAME + " TEXT," +
            OrganisationUnitModel.Columns.DISPLAY_NAME + " TEXT," +
            OrganisationUnitModel.Columns.CREATED + " TEXT," +
            OrganisationUnitModel.Columns.LAST_UPDATED + " TEXT," +
            OrganisationUnitModel.Columns.SHORT_NAME + " TEXT," +
            OrganisationUnitModel.Columns.DISPLAY_SHORT_NAME + " TEXT," +
            OrganisationUnitModel.Columns.DESCRIPTION + " TEXT," +
            OrganisationUnitModel.Columns.DISPLAY_DESCRIPTION + " TEXT," +
            OrganisationUnitModel.Columns.PATH + " TEXT," +
            OrganisationUnitModel.Columns.OPENING_DATE + " TEXT," +
            OrganisationUnitModel.Columns.CLOSED_DATE + " TEXT," +
            OrganisationUnitModel.Columns.LEVEL + " INTEGER," +
            OrganisationUnitModel.Columns.PARENT + " TEXT" +
            ");";

    private static final String CREATE_USER_ORGANISATION_UNIT_TABLE = "CREATE TABLE " +
            UserOrganisationUnitLinkModel.TABLE + " (" +
            UserOrganisationUnitLinkModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            UserOrganisationUnitLinkModel.Columns.USER + " TEXT NOT NULL," +
            UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT + " TEXT NOT NULL," +
            ORGANISATION_UNIT_SCOPE + " TEXT NOT NULL," +
            " FOREIGN KEY (" + UserOrganisationUnitLinkModel.Columns.USER + ") " +
            " REFERENCES " + UserModel.TABLE + " (" + UserModel.Columns.UID + ") " +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED," +
            " FOREIGN KEY (" + UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT + ") " +
            " REFERENCES " + OrganisationUnitModel.TABLE + " (" + OrganisationUnitModel.Columns.UID
            + ")" +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED," +
            "UNIQUE (" + UserOrganisationUnitLinkModel.Columns.USER + ", " +
            UserOrganisationUnitLinkModel.Columns.ORGANISATION_UNIT + ", " +
            ORGANISATION_UNIT_SCOPE + ")" +
            ");";

    private static final String CREATE_AUTHENTICATED_USER_TABLE = "CREATE TABLE " +
            AuthenticatedUserModel.TABLE + " (" +
            AuthenticatedUserModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            AuthenticatedUserModel.Columns.USER + " TEXT NOT NULL UNIQUE," +
            AuthenticatedUserModel.Columns.CREDENTIALS + " TEXT NOT NULL," +
            " FOREIGN KEY (" + AuthenticatedUserModel.Columns.USER + ") " +
            " REFERENCES " + UserModel.TABLE + " (" + UserModel.Columns.UID + ")" +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED" +
            ");";

    private static final String CREATE_OPTION_SET_TABLE =
            "CREATE TABLE " + OptionSetModel.TABLE + " (" +
                    OptionSetModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    OptionSetModel.Columns.UID + " TEXT NOT NULL UNIQUE," +
                    OptionSetModel.Columns.CODE + " TEXT," +
                    OptionSetModel.Columns.NAME + " TEXT," +
                    OptionSetModel.Columns.DISPLAY_NAME + " TEXT," +
                    OptionSetModel.Columns.CREATED + " TEXT," +
                    OptionSetModel.Columns.LAST_UPDATED + " TEXT," +
                    OptionSetModel.Columns.VERSION + " INTEGER," +
                    OptionSetModel.Columns.VALUE_TYPE + " TEXT" +
                    ");";

    private static final String CREATE_OPTION_TABLE = "CREATE TABLE " + OptionModel.TABLE + " (" +
            OptionModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            OptionModel.Columns.UID + " TEXT NOT NULL UNIQUE," +
            OptionModel.Columns.CODE + " TEXT," +
            OptionModel.Columns.NAME + " TEXT," +
            OptionModel.Columns.DISPLAY_NAME + " TEXT," +
            OptionModel.Columns.CREATED + " TEXT," +
            OptionModel.Columns.LAST_UPDATED + " TEXT," +
            OptionModel.Columns.OPTION_SET + " TEXT NOT NULL," +
            " FOREIGN KEY (" + OptionModel.Columns.OPTION_SET + ") " +
            " REFERENCES " + OptionSetModel.TABLE +
            " (" + OptionSetModel.Columns.UID + ") ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED "
            +
            ");";

    private static final String CREATE_PROGRAM_TABLE = "CREATE TABLE " + ProgramModel.TABLE + " (" +
            ProgramModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ProgramModel.Columns.UID + " TEXT NOT NULL UNIQUE," +
            ProgramModel.Columns.CODE + " TEXT," +
            ProgramModel.Columns.NAME + " TEXT," +
            ProgramModel.Columns.DISPLAY_NAME + " TEXT," +
            ProgramModel.Columns.CREATED + " TEXT," +
            ProgramModel.Columns.LAST_UPDATED + " TEXT," +
            ProgramModel.Columns.SHORT_NAME + " TEXT," +
            ProgramModel.Columns.DISPLAY_SHORT_NAME + " TEXT," +
            ProgramModel.Columns.DESCRIPTION + " TEXT," +
            ProgramModel.Columns.DISPLAY_DESCRIPTION + " TEXT," +
            ProgramModel.Columns.VERSION + " INTEGER," +
            ProgramModel.Columns.ONLY_ENROLL_ONCE + " INTEGER," +
            ProgramModel.Columns.ENROLLMENT_DATE_LABEL + " TEXT," +
            ProgramModel.Columns.DISPLAY_INCIDENT_DATE + " INTEGER," +
            ProgramModel.Columns.INCIDENT_DATE_LABEL + " TEXT," +
            ProgramModel.Columns.REGISTRATION + " INTEGER," +
            ProgramModel.Columns.SELECT_ENROLLMENT_DATES_IN_FUTURE + " INTEGER," +
            ProgramModel.Columns.DATA_ENTRY_METHOD + " INTEGER," +
            ProgramModel.Columns.IGNORE_OVERDUE_EVENTS + " INTEGER," +
            ProgramModel.Columns.RELATIONSHIP_FROM_A + " INTEGER," +
            ProgramModel.Columns.SELECT_INCIDENT_DATES_IN_FUTURE + " INTEGER," +
            ProgramModel.Columns.CAPTURE_COORDINATES + " INTEGER," +
            ProgramModel.Columns.USE_FIRST_STAGE_DURING_REGISTRATION + " INTEGER," +
            ProgramModel.Columns.DISPLAY_FRONT_PAGE_LIST + " INTEGER," +
            ProgramModel.Columns.PROGRAM_TYPE + " TEXT," +
            ProgramModel.Columns.RELATIONSHIP_TYPE + " TEXT," +
            ProgramModel.Columns.RELATIONSHIP_TEXT + " TEXT," +
            ProgramModel.Columns.RELATED_PROGRAM + " TEXT," +
            ProgramModel.Columns.TRACKED_ENTITY + " TEXT," +
            ProgramModel.Columns.CATEGORY_COMBO + " TEXT," +
            " FOREIGN KEY (" + ProgramModel.Columns.RELATIONSHIP_TYPE + ")" +
            " REFERENCES " + RelationshipTypeModel.TABLE + " (" + RelationshipTypeModel.Columns.UID
            + ")" +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, " +
            " FOREIGN KEY (" + ProgramModel.Columns.TRACKED_ENTITY + ")" +
            " REFERENCES " + TrackedEntityModel.TABLE + " (" + TrackedEntityModel.Columns.UID + ")"
            +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED" +
            " FOREIGN KEY (" + ProgramModel.Columns.CATEGORY_COMBO + ")" +
            " REFERENCES " + CategoryComboModel.TABLE + " (" + CategoryComboModel.Columns.UID + ")"
            +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED" +
            ");";

    private static final String CREATE_TRACKED_ENTITY_TABLE =
            "CREATE TABLE " + TrackedEntityModel.TABLE +
                    " (" +
                    TrackedEntityModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TrackedEntityModel.Columns.UID + " TEXT NOT NULL UNIQUE," +
                    TrackedEntityModel.Columns.CODE + " TEXT," +
                    TrackedEntityModel.Columns.NAME + " TEXT," +
                    TrackedEntityModel.Columns.DISPLAY_NAME + " TEXT," +
                    TrackedEntityModel.Columns.CREATED + " TEXT," +
                    TrackedEntityModel.Columns.LAST_UPDATED + " TEXT," +
                    TrackedEntityModel.Columns.SHORT_NAME + " TEXT," +
                    TrackedEntityModel.Columns.DISPLAY_SHORT_NAME + " TEXT," +
                    TrackedEntityModel.Columns.DESCRIPTION + " TEXT," +
                    TrackedEntityModel.Columns.DISPLAY_DESCRIPTION + " TEXT" +
                    ");";

    private static final String CREATE_DATA_ELEMENT_TABLE =
            "CREATE TABLE " + DataElementModel.TABLE + " (" +
                    DataElementModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DataElementModel.Columns.UID + " TEXT NOT NULL UNIQUE," +
                    DataElementModel.Columns.CODE + " TEXT," +
                    DataElementModel.Columns.NAME + " TEXT," +
                    DataElementModel.Columns.DISPLAY_NAME + " TEXT," +
                    DataElementModel.Columns.CREATED + " TEXT," +
                    DataElementModel.Columns.LAST_UPDATED + " TEXT," +
                    DataElementModel.Columns.SHORT_NAME + " TEXT," +
                    DataElementModel.Columns.DISPLAY_SHORT_NAME + " TEXT," +
                    DataElementModel.Columns.DESCRIPTION + " TEXT," +
                    DataElementModel.Columns.DISPLAY_DESCRIPTION + " TEXT," +
                    DataElementModel.Columns.VALUE_TYPE + " TEXT," +
                    DataElementModel.Columns.ZERO_IS_SIGNIFICANT + " INTEGER," +
                    DataElementModel.Columns.AGGREGATION_TYPE + " TEXT," +
                    DataElementModel.Columns.FORM_NAME + " TEXT," +
                    DataElementModel.Columns.NUMBER_TYPE + " TEXT," +
                    DataElementModel.Columns.DOMAIN_TYPE + " TEXT," +
                    DataElementModel.Columns.DIMENSION + " TEXT," +
                    DataElementModel.Columns.DISPLAY_FORM_NAME + " TEXT," +
                    DataElementModel.Columns.OPTION_SET + " TEXT," +
                    DataElementModel.Columns.CATEGORY_COMBO + " TEXT," +
                    " FOREIGN KEY ( " + DataElementModel.Columns.OPTION_SET + ")" +
                    " REFERENCES " + OptionSetModel.TABLE + " (" + OptionSetModel.Columns.UID + ")"
                    +
                    " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED" +
                    ");";

    private static final String CREATE_PROGRAM_STAGE_DATA_ELEMENT_TABLE = "CREATE TABLE " +
            ProgramStageDataElementModel.TABLE + " (" +
            ProgramStageDataElementModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ProgramStageDataElementModel.Columns.UID + " TEXT NOT NULL UNIQUE," +
            ProgramStageDataElementModel.Columns.CODE + " TEXT," +
            ProgramStageDataElementModel.Columns.NAME + " TEXT," +
            ProgramStageDataElementModel.Columns.DISPLAY_NAME + " TEXT," +
            ProgramStageDataElementModel.Columns.CREATED + " TEXT," +
            ProgramStageDataElementModel.Columns.LAST_UPDATED + " TEXT," +
            ProgramStageDataElementModel.Columns.DISPLAY_IN_REPORTS + " INTEGER," +
            ProgramStageDataElementModel.Columns.COMPULSORY + " INTEGER," +
            ProgramStageDataElementModel.Columns.ALLOW_PROVIDED_ELSEWHERE + " INTEGER," +
            ProgramStageDataElementModel.Columns.SORT_ORDER + " INTEGER," +
            ProgramStageDataElementModel.Columns.ALLOW_FUTURE_DATE + " INTEGER," +
            ProgramStageDataElementModel.Columns.DATA_ELEMENT + " TEXT NOT NULL," +
            ProgramStageDataElementModel.Columns.PROGRAM_STAGE + " TEXT NOT NULL," +
            ProgramStageDataElementModel.Columns.PROGRAM_STAGE_SECTION + " TEXT," +
            " FOREIGN KEY (" + ProgramStageDataElementModel.Columns.PROGRAM_STAGE + ")" +
            " REFERENCES " + ProgramStageModel.TABLE + " (" + ProgramStageModel.Columns.UID + ")" +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED," +
            " FOREIGN KEY (" + ProgramStageDataElementModel.Columns.DATA_ELEMENT + ")" +
            " REFERENCES " + DataElementModel.TABLE + " (" + DataElementModel.Columns.UID + ")" +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED," +
            " FOREIGN KEY (" + ProgramStageDataElementModel.Columns.PROGRAM_STAGE_SECTION + ")" +
            " REFERENCES " + ProgramStageSectionModel.TABLE + " ("
            + ProgramStageSectionModel.Columns.UID + ")" +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED" +
            ");";

    private static final String CREATE_RELATIONSHIP_TABLE =
            "CREATE TABLE " + RelationshipModel.TABLE + " (" +
                    RelationshipModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    RelationshipModel.Columns.TRACKED_ENTITY_INSTANCE_A + " TEXT NOT NULL," +
                    RelationshipModel.Columns.TRACKED_ENTITY_INSTANCE_B + " TEXT NOT NULL," +
                    RelationshipModel.Columns.RELATIONSHIP_TYPE + " TEXT NOT NULL," +
                    " FOREIGN KEY (" + RelationshipModel.Columns.RELATIONSHIP_TYPE + ") " +
                    " REFERENCES " + RelationshipTypeModel.TABLE + " ("
                    + RelationshipTypeModel.Columns.UID + ")" +
                    " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED" +
                    " FOREIGN KEY (" + RelationshipModel.Columns.TRACKED_ENTITY_INSTANCE_A + ") " +
                    " REFERENCES " + TrackedEntityInstanceModel.TABLE + " ("
                    + TrackedEntityInstanceModel.Columns.UID + ")" +
                    " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED" +
                    " FOREIGN KEY (" + RelationshipModel.Columns.TRACKED_ENTITY_INSTANCE_B + ") " +
                    " REFERENCES " + TrackedEntityInstanceModel.TABLE + " ("
                    + TrackedEntityInstanceModel.Columns.UID + ")" +
                    " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED" +
                    ");";

    private static final String CREATE_RELATIONSHIP_TYPE_TABLE = "CREATE TABLE " +
            RelationshipTypeModel.TABLE + " (" +
            RelationshipTypeModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            RelationshipTypeModel.Columns.UID + " TEXT NOT NULL UNIQUE, " +
            RelationshipTypeModel.Columns.CODE + " TEXT, " +
            RelationshipTypeModel.Columns.NAME + " TEXT, " +
            RelationshipTypeModel.Columns.DISPLAY_NAME + " TEXT, " +
            RelationshipTypeModel.Columns.CREATED + " TEXT, " +
            RelationshipTypeModel.Columns.LAST_UPDATED + " TEXT, " +
            RelationshipTypeModel.Columns.B_IS_TO_A + " TEXT, " +
            RelationshipTypeModel.Columns.A_IS_TO_B + " TEXT " +
            ");";

    private static final String CREATE_PROGRAM_STAGE_SECTION_TABLE = "CREATE TABLE " +
            ProgramStageSectionModel.TABLE + " (" +
            ProgramStageSectionModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ProgramStageSectionModel.Columns.UID + " TEXT NOT NULL UNIQUE," +
            ProgramStageSectionModel.Columns.CODE + " TEXT," +
            ProgramStageSectionModel.Columns.NAME + " TEXT," +
            ProgramStageSectionModel.Columns.DISPLAY_NAME + " TEXT," +
            ProgramStageSectionModel.Columns.CREATED + " TEXT," +
            ProgramStageSectionModel.Columns.LAST_UPDATED + " TEXT," +
            ProgramStageSectionModel.Columns.SORT_ORDER + " INTEGER," +
            ProgramStageSectionModel.Columns.PROGRAM_STAGE + " TEXT NOT NULL," +
            " FOREIGN KEY ( " + ProgramStageSectionModel.Columns.PROGRAM_STAGE + ")" +
            " REFERENCES " + ProgramStageModel.TABLE + " (" + ProgramStageModel.Columns.UID + ")" +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED" +
            ");";

    private static final String CREATE_PROGRAM_STAGE_TABLE = "CREATE TABLE " +
            ProgramStageModel.TABLE + " (" +
            ProgramStageModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ProgramStageModel.Columns.UID + " TEXT NOT NULL UNIQUE," +
            ProgramStageModel.Columns.CODE + " TEXT," +
            ProgramStageModel.Columns.NAME + " TEXT," +
            ProgramStageModel.Columns.DISPLAY_NAME + " TEXT," +
            ProgramStageModel.Columns.CREATED + " TEXT," +
            ProgramStageModel.Columns.LAST_UPDATED + " TEXT," +
            ProgramStageModel.Columns.EXECUTION_DATE_LABEL + " TEXT," +
            ProgramStageModel.Columns.ALLOW_GENERATE_NEXT_VISIT + " INTEGER," +
            ProgramStageModel.Columns.VALID_COMPLETE_ONLY + " INTEGER," +
            ProgramStageModel.Columns.REPORT_DATE_TO_USE + " TEXT," +
            ProgramStageModel.Columns.OPEN_AFTER_ENROLLMENT + " INTEGER," +
            ProgramStageModel.Columns.REPEATABLE + " INTEGER," +
            ProgramStageModel.Columns.CAPTURE_COORDINATES + " INTEGER," +
            ProgramStageModel.Columns.FORM_TYPE + " TEXT," +
            ProgramStageModel.Columns.DISPLAY_GENERATE_EVENT_BOX + " INTEGER," +
            ProgramStageModel.Columns.GENERATED_BY_ENROLMENT_DATE + " INTEGER," +
            ProgramStageModel.Columns.AUTO_GENERATE_EVENT + " INTEGER," +
            ProgramStageModel.Columns.SORT_ORDER + " INTEGER," +
            ProgramStageModel.Columns.HIDE_DUE_DATE + " INTEGER," +
            ProgramStageModel.Columns.BLOCK_ENTRY_FORM + " INTEGER," +
            ProgramStageModel.Columns.MIN_DAYS_FROM_START + " INTEGER," +
            ProgramStageModel.Columns.STANDARD_INTERVAL + " INTEGER," +
            ProgramStageModel.Columns.PROGRAM + " TEXT NOT NULL," +
            " FOREIGN KEY ( " + ProgramStageModel.Columns.PROGRAM + ")" +
            " REFERENCES " + ProgramModel.TABLE + " (" + ProgramModel.Columns.UID + ")" +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED" +
            ");";

    private static final String CREATE_PROGRAM_RULE_VARIABLE_TABLE = "CREATE TABLE " +
            ProgramRuleVariableModel.TABLE + " (" +
            ProgramRuleVariableModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ProgramRuleVariableModel.Columns.UID + " TEXT NOT NULL UNIQUE," +
            ProgramRuleVariableModel.Columns.CODE + " TEXT," +
            ProgramRuleVariableModel.Columns.NAME + " TEXT," +
            ProgramRuleVariableModel.Columns.DISPLAY_NAME + " TEXT," +
            ProgramRuleVariableModel.Columns.CREATED + " TEXT," +
            ProgramRuleVariableModel.Columns.LAST_UPDATED + " TEXT," +
            ProgramRuleVariableModel.Columns.USE_CODE_FOR_OPTION_SET + " INTEGER," +
            ProgramRuleVariableModel.Columns.PROGRAM + " TEXT NOT NULL," +
            ProgramRuleVariableModel.Columns.PROGRAM_STAGE + " TEXT," +
            ProgramRuleVariableModel.Columns.DATA_ELEMENT + " TEXT," +
            ProgramRuleVariableModel.Columns.TRACKED_ENTITY_ATTRIBUTE + " TEXT," +
            ProgramRuleVariableModel.Columns.PROGRAM_RULE_VARIABLE_SOURCE_TYPE + " TEXT," +
            " FOREIGN KEY (" + ProgramRuleVariableModel.Columns.PROGRAM + ")" +
            " REFERENCES " + ProgramModel.TABLE + " (" + ProgramModel.Columns.UID + ")" +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED," +
            " FOREIGN KEY (" + ProgramRuleVariableModel.Columns.PROGRAM_STAGE + ")" +
            " REFERENCES " + ProgramStageModel.TABLE + " (" + ProgramStageModel.Columns.UID + ")" +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED," +
            " FOREIGN KEY (" + ProgramRuleVariableModel.Columns.TRACKED_ENTITY_ATTRIBUTE + ")" +
            " REFERENCES " + TrackedEntityAttributeModel.TABLE + "("
            + TrackedEntityAttributeModel.Columns.UID + ")" +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED," +
            " FOREIGN KEY (" + ProgramRuleVariableModel.Columns.DATA_ELEMENT + ")" +
            " REFERENCES " + DataElementModel.TABLE + " (" + DataElementModel.Columns.UID + ")" +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED" +
            ");";

    private static final String CREATE_TRACKED_ENTITY_ATTRIBUTE_TABLE = "CREATE TABLE " +
            TrackedEntityAttributeModel.TABLE + " (" +
            TrackedEntityAttributeModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TrackedEntityAttributeModel.Columns.UID + " TEXT NOT NULL UNIQUE," +
            TrackedEntityAttributeModel.Columns.CODE + " TEXT," +
            TrackedEntityAttributeModel.Columns.NAME + " TEXT," +
            TrackedEntityAttributeModel.Columns.DISPLAY_NAME + " TEXT," +
            TrackedEntityAttributeModel.Columns.CREATED + " TEXT," +
            TrackedEntityAttributeModel.Columns.LAST_UPDATED + " TEXT," +
            TrackedEntityAttributeModel.Columns.SHORT_NAME + " TEXT," +
            TrackedEntityAttributeModel.Columns.DISPLAY_SHORT_NAME + " TEXT," +
            TrackedEntityAttributeModel.Columns.DESCRIPTION + " TEXT," +
            TrackedEntityAttributeModel.Columns.DISPLAY_DESCRIPTION + " TEXT," +
            TrackedEntityAttributeModel.Columns.PATTERN + " TEXT," +
            TrackedEntityAttributeModel.Columns.SORT_ORDER_IN_LIST_NO_PROGRAM + " INTEGER," +
            TrackedEntityAttributeModel.Columns.OPTION_SET + " TEXT," +
            TrackedEntityAttributeModel.Columns.VALUE_TYPE + " TEXT," +
            TrackedEntityAttributeModel.Columns.EXPRESSION + " TEXT," +
            TrackedEntityAttributeModel.Columns.SEARCH_SCOPE + " TEXT," +
            TrackedEntityAttributeModel.Columns.PROGRAM_SCOPE + " INTEGER," +
            TrackedEntityAttributeModel.Columns.DISPLAY_IN_LIST_NO_PROGRAM + " INTEGER," +
            TrackedEntityAttributeModel.Columns.GENERATED + " INTEGER," +
            TrackedEntityAttributeModel.Columns.DISPLAY_ON_VISIT_SCHEDULE + " INTEGER," +
            TrackedEntityAttributeModel.Columns.ORG_UNIT_SCOPE + " INTEGER," +
            TrackedEntityAttributeModel.Columns.UNIQUE + " INTEGER," +
            TrackedEntityAttributeModel.Columns.INHERIT + " INTEGER," +
            " FOREIGN KEY (" + TrackedEntityAttributeModel.Columns.OPTION_SET + ")" +
            " REFERENCES " + OptionSetModel.TABLE + " (" + OptionSetModel.Columns.UID + ")" +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED" +
            ");";

    private static final String CREATE_PROGRAM_TRACKED_ENTITY_ATTRIBUTE_TABLE = "CREATE TABLE " +
            ProgramTrackedEntityAttributeModel.TABLE + " (" +
            ProgramTrackedEntityAttributeModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ProgramTrackedEntityAttributeModel.Columns.UID + " TEXT NOT NULL UNIQUE," +
            ProgramTrackedEntityAttributeModel.Columns.CODE + " TEXT," +
            ProgramTrackedEntityAttributeModel.Columns.NAME + " TEXT," +
            ProgramTrackedEntityAttributeModel.Columns.DISPLAY_NAME + " TEXT," +
            ProgramTrackedEntityAttributeModel.Columns.CREATED + " TEXT," +
            ProgramTrackedEntityAttributeModel.Columns.LAST_UPDATED + " TEXT," +
            ProgramTrackedEntityAttributeModel.Columns.SHORT_NAME + " TEXT," +
            ProgramTrackedEntityAttributeModel.Columns.DISPLAY_SHORT_NAME + " TEXT," +
            ProgramTrackedEntityAttributeModel.Columns.DESCRIPTION + " TEXT," +
            ProgramTrackedEntityAttributeModel.Columns.DISPLAY_DESCRIPTION + " TEXT," +
            ProgramTrackedEntityAttributeModel.Columns.MANDATORY + " INTEGER," +
            ProgramTrackedEntityAttributeModel.Columns.TRACKED_ENTITY_ATTRIBUTE + " TEXT NOT NULL,"
            +
            ProgramTrackedEntityAttributeModel.Columns.ALLOW_FUTURE_DATES + " INTEGER," +
            ProgramTrackedEntityAttributeModel.Columns.DISPLAY_IN_LIST + " INTEGER," +
            ProgramTrackedEntityAttributeModel.Columns.SORT_ORDER + " INTEGER," +
            ProgramTrackedEntityAttributeModel.Columns.PROGRAM + " TEXT NOT NULL," +
            " FOREIGN KEY (" + ProgramTrackedEntityAttributeModel.Columns.TRACKED_ENTITY_ATTRIBUTE
            + ")" +
            " REFERENCES " + TrackedEntityAttributeModel.TABLE +
            " (" + TrackedEntityAttributeModel.Columns.UID + ")" +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED," +
            " FOREIGN KEY (" + ProgramTrackedEntityAttributeModel.Columns.PROGRAM + ")" +
            " REFERENCES " + ProgramModel.TABLE + " (" + ProgramModel.Columns.UID + ")" +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED" +
            ");";

    private static final String CREATE_PROGRAM_RULE_TABLE = "CREATE TABLE " +
            ProgramRuleModel.TABLE + " (" +
            ProgramRuleModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ProgramRuleModel.Columns.UID + " TEXT NOT NULL UNIQUE," +
            ProgramRuleModel.Columns.CODE + " TEXT," +
            ProgramRuleModel.Columns.NAME + " TEXT," +
            ProgramRuleModel.Columns.DISPLAY_NAME + " TEXT," +
            ProgramRuleModel.Columns.CREATED + " TEXT," +
            ProgramRuleModel.Columns.LAST_UPDATED + " TEXT," +
            ProgramRuleModel.Columns.PRIORITY + " INTEGER," +
            ProgramRuleModel.Columns.CONDITION + " TEXT," +
            ProgramRuleModel.Columns.PROGRAM + " TEXT NOT NULL," +
            ProgramRuleModel.Columns.PROGRAM_STAGE + " TEXT," +
            " FOREIGN KEY (" + ProgramRuleModel.Columns.PROGRAM + ")" +
            " REFERENCES " + ProgramModel.TABLE + " (" + ProgramModel.Columns.UID + ")" +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED," +
            " FOREIGN KEY (" + ProgramRuleModel.Columns.PROGRAM_STAGE + ")" +
            " REFERENCES " + ProgramStageModel.TABLE + " (" + ProgramStageModel.Columns.UID + ")" +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED" +
            ");";

    private static final String CREATE_CONSTANT_TABLE =
            "CREATE TABLE " + ConstantModel.TABLE + " (" +
                    ConstantModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ConstantModel.Columns.UID + " TEXT NOT NULL UNIQUE," +
                    ConstantModel.Columns.CODE + " TEXT," +
                    ConstantModel.Columns.NAME + " TEXT," +
                    ConstantModel.Columns.DISPLAY_NAME + " TEXT," +
                    ConstantModel.Columns.CREATED + " TEXT," +
                    ConstantModel.Columns.LAST_UPDATED + " TEXT," +
                    ConstantModel.Columns.VALUE + " TEXT" +
                    ");";

    private static final String CREATE_SYSTEM_INFO_TABLE =
            "CREATE TABLE " + SystemInfoModel.TABLE + " (" +
                    SystemInfoModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SystemInfoModel.Columns.SERVER_DATE + " TEXT," +
                    SystemInfoModel.Columns.DATE_FORMAT + " TEXT," +
                    SystemInfoModel.Columns.VERSION + " TEXT," +
                    SystemInfoModel.Columns.CONTEXT_PATH + " TEXT" +
                    ");";

    private static final String CREATE_PROGRAM_INDICATOR_TABLE = "CREATE TABLE " +
            ProgramIndicatorModel.TABLE + " (" +
            ProgramIndicatorModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ProgramIndicatorModel.Columns.UID + " TEXT NOT NULL UNIQUE," +
            ProgramIndicatorModel.Columns.CODE + " TEXT," +
            ProgramIndicatorModel.Columns.NAME + " TEXT," +
            ProgramIndicatorModel.Columns.DISPLAY_NAME + " TEXT," +
            ProgramIndicatorModel.Columns.CREATED + " TEXT," +
            ProgramIndicatorModel.Columns.LAST_UPDATED + " TEXT," +
            ProgramIndicatorModel.Columns.SHORT_NAME + " TEXT," +
            ProgramIndicatorModel.Columns.DISPLAY_SHORT_NAME + " TEXT," +
            ProgramIndicatorModel.Columns.DESCRIPTION + " TEXT," +
            ProgramIndicatorModel.Columns.DISPLAY_DESCRIPTION + " TEXT," +
            ProgramIndicatorModel.Columns.DISPLAY_IN_FORM + " INTEGER," +
            ProgramIndicatorModel.Columns.EXPRESSION + " TEXT," +
            ProgramIndicatorModel.Columns.DIMENSION_ITEM + " TEXT," +
            ProgramIndicatorModel.Columns.FILTER + " TEXT," +
            ProgramIndicatorModel.Columns.DECIMALS + " INTEGER," +
            ProgramIndicatorModel.Columns.PROGRAM + " TEXT NOT NULL," +
            " FOREIGN KEY (" + ProgramIndicatorModel.Columns.PROGRAM + ")" +
            " REFERENCES " + ProgramModel.TABLE + " (" + ProgramModel.Columns.UID + ")" +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED " +
            ");";

    private static final String CREATE_PROGRAM_RULE_ACTION_TABLE = "CREATE TABLE " +
            ProgramRuleActionModel.TABLE + " (" +
            ProgramRuleActionModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ProgramRuleActionModel.Columns.UID + " TEXT NOT NULL UNIQUE," +
            ProgramRuleActionModel.Columns.CODE + " TEXT," +
            ProgramRuleActionModel.Columns.NAME + " TEXT," +
            ProgramRuleActionModel.Columns.DISPLAY_NAME + " TEXT," +
            ProgramRuleActionModel.Columns.CREATED + " TEXT," +
            ProgramRuleActionModel.Columns.LAST_UPDATED + " TEXT," +
            ProgramRuleActionModel.Columns.DATA + " TEXT," +
            ProgramRuleActionModel.Columns.CONTENT + " TEXT," +
            ProgramRuleActionModel.Columns.LOCATION + " TEXT," +
            ProgramRuleActionModel.Columns.TRACKED_ENTITY_ATTRIBUTE + " TEXT," +
            ProgramRuleActionModel.Columns.PROGRAM_INDICATOR + " TEXT," +
            ProgramRuleActionModel.Columns.PROGRAM_STAGE_SECTION + " TEXT," +
            ProgramRuleActionModel.Columns.PROGRAM_RULE_ACTION_TYPE + " TEXT," +
            ProgramRuleActionModel.Columns.PROGRAM_STAGE + " TEXT," +
            ProgramRuleActionModel.Columns.DATA_ELEMENT + " TEXT," +
            ProgramRuleActionModel.Columns.PROGRAM_RULE + " TEXT NOT NULL," +
            " FOREIGN KEY (" + ProgramRuleActionModel.Columns.PROGRAM_RULE + ")" +
            " REFERENCES " + ProgramRuleModel.TABLE +
            " (" + ProgramRuleModel.Columns.UID + ")" +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED," +
            " FOREIGN KEY (" + ProgramRuleActionModel.Columns.TRACKED_ENTITY_ATTRIBUTE + ")" +
            " REFERENCES " + TrackedEntityAttributeModel.TABLE +
            " (" + TrackedEntityAttributeModel.Columns.UID + ") " +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED," +
            " FOREIGN KEY (" + ProgramRuleActionModel.Columns.PROGRAM_INDICATOR + ")" +
            " REFERENCES " + ProgramIndicatorModel.TABLE +
            " (" + ProgramIndicatorModel.Columns.UID + ") " +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED," +
            " FOREIGN KEY (" + ProgramRuleActionModel.Columns.PROGRAM_STAGE_SECTION + ")" +
            " REFERENCES " + ProgramStageSectionModel.TABLE +
            " (" + ProgramStageSectionModel.Columns.UID + ") " +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED," +
            " FOREIGN KEY (" + ProgramRuleActionModel.Columns.PROGRAM_STAGE + ")" +
            " REFERENCES " + ProgramStageModel.TABLE +
            " (" + ProgramStageModel.Columns.UID + ")" +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED," +
            " FOREIGN KEY (" + ProgramRuleActionModel.Columns.DATA_ELEMENT + ")" +
            " REFERENCES " + DataElementModel.TABLE +
            " (" + DataElementModel.Columns.UID + ")" +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED" +
            ");";

    private static final String CREATE_TRACKED_ENTITY_DATA_VALUE_TABLE = "CREATE TABLE " +
            TrackedEntityDataValueModel.TABLE + " (" +
            TrackedEntityDataValueModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TrackedEntityDataValueModel.Columns.EVENT + " TEXT NOT NULL," +
            TrackedEntityDataValueModel.Columns.DATA_ELEMENT + " TEXT NOT NULL," +
            TrackedEntityDataValueModel.Columns.STORED_BY + " TEXT," +
            TrackedEntityDataValueModel.Columns.VALUE + " TEXT," +
            TrackedEntityDataValueModel.Columns.CREATED + " TEXT," +
            TrackedEntityDataValueModel.Columns.LAST_UPDATED + " TEXT," +
            TrackedEntityDataValueModel.Columns.PROVIDED_ELSEWHERE + " INTEGER," +
            " FOREIGN KEY (" + TrackedEntityDataValueModel.Columns.DATA_ELEMENT + ")" +
            " REFERENCES " + DataElementModel.TABLE +
            " (" + DataElementModel.Columns.UID + ")" +
            " ON DELETE CASCADE, " +
            " FOREIGN KEY (" + TrackedEntityDataValueModel.Columns.EVENT + ")" +
            " REFERENCES " + EventModel.TABLE + " (" + EventModel.Columns.UID + ")" +
            " ON DELETE CASCADE" +
            ");";

    private static final String CREATE_TRACKED_ENTITY_ATTRIBUTE_VALUE_TABLE = "CREATE TABLE " +
            TrackedEntityAttributeValueModel.TABLE + " (" +
            TrackedEntityAttributeValueModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TrackedEntityAttributeValueModel.Columns.CREATED + " TEXT," +
            TrackedEntityAttributeValueModel.Columns.LAST_UPDATED + " TEXT," +
            TrackedEntityAttributeValueModel.Columns.VALUE + " TEXT," +
            TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_ATTRIBUTE + " TEXT NOT NULL," +
            TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_INSTANCE + " TEXT NOT NULL," +
            " FOREIGN KEY (" + TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_ATTRIBUTE
            + ")" +
            " REFERENCES " + TrackedEntityAttributeModel.TABLE +
            " (" + TrackedEntityAttributeModel.Columns.UID + ")" +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, " +
            " FOREIGN KEY (" + TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_INSTANCE
            + ") " +
            " REFERENCES " + TrackedEntityInstanceModel.TABLE +
            " (" + TrackedEntityInstanceModel.Columns.UID + ")" +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED" +
            ");";

    private static final String CREATE_EVENT_TABLE = "CREATE TABLE " + EventModel.TABLE + " (" +
            EventModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            EventModel.Columns.UID + " TEXT NOT NULL UNIQUE," +
            EventModel.Columns.ENROLLMENT_UID + " TEXT," +
            EventModel.Columns.CREATED + " TEXT," +
            EventModel.Columns.LAST_UPDATED + " TEXT," +
            EventModel.Columns.CREATED_AT_CLIENT + " TEXT," +
            EventModel.Columns.LAST_UPDATED_AT_CLIENT + " TEXT," +
            EventModel.Columns.STATUS + " TEXT," +
            EventModel.Columns.LATITUDE + " TEXT," +
            EventModel.Columns.LONGITUDE + " TEXT," +
            EventModel.Columns.PROGRAM + " TEXT NOT NULL," +
            EventModel.Columns.PROGRAM_STAGE + " TEXT NOT NULL," +
            EventModel.Columns.ORGANISATION_UNIT + " TEXT NOT NULL," +
            EventModel.Columns.EVENT_DATE + " TEXT," +
            EventModel.Columns.COMPLETE_DATE + " TEXT," +
            EventModel.Columns.DUE_DATE + " TEXT," +
            EventModel.Columns.STATE + " TEXT," +
            EventModel.Columns.ATTRIBUTE_CATEGORY_OPTIONS + " TEXT," +
            EventModel.Columns.ATTRIBUTE_OPTION_COMBO + " TEXT," +
            EventModel.Columns.TRACKED_ENTITY_INSTANCE + " TEXT," +
            " FOREIGN KEY (" + EventModel.Columns.PROGRAM + ")" +
            " REFERENCES " + ProgramModel.TABLE +
            " (" + ProgramModel.Columns.UID + ")" +
            " ON DELETE CASCADE," +
            " FOREIGN KEY (" + EventModel.Columns.PROGRAM_STAGE + ")" +
            " REFERENCES " + ProgramStageModel.TABLE +
            " (" + ProgramStageModel.Columns.UID + ")" +
            " ON DELETE CASCADE," +
            "FOREIGN KEY (" + EventModel.Columns.ENROLLMENT_UID + ")" +
            " REFERENCES " + EnrollmentModel.TABLE +
            " (" + EnrollmentModel.Columns.UID + ")" +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED," +
            " FOREIGN KEY (" + EventModel.Columns.ORGANISATION_UNIT + ")" +
            " REFERENCES " + OrganisationUnitModel.TABLE +
            " (" + OrganisationUnitModel.Columns.UID + ")" +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED," +
            " FOREIGN KEY (" + EventModel.Columns.TRACKED_ENTITY_INSTANCE + ")" +
            " REFERENCES " + TrackedEntityInstanceModel.TABLE + " (" + TrackedEntityInstanceModel.Columns.UID + ")" +
            " ON DELETE CASCADE," +
            " FOREIGN KEY (" + EventModel.Columns.ATTRIBUTE_CATEGORY_OPTIONS + ")" +
            " REFERENCES " + CategoryOptionModel.TABLE + " (" + CategoryOptionModel.Columns.UID + ")"+
            " ON DELETE CASCADE," +
            " FOREIGN KEY (" + EventModel.Columns.ATTRIBUTE_OPTION_COMBO + ")" +
            " REFERENCES " + CategoryOptionComboModel.TABLE + " (" + CategoryOptionComboModel.Columns.UID + ")"
            +
            " ON DELETE CASCADE" +
            ");";

    private static final String CREATE_TRACKED_ENTITY_INSTANCE_TABLE = "CREATE TABLE " +
            TrackedEntityInstanceModel.TABLE + " (" +
            TrackedEntityInstanceModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TrackedEntityInstanceModel.Columns.UID + " TEXT NOT NULL UNIQUE," +
            TrackedEntityInstanceModel.Columns.CREATED + " TEXT," +
            TrackedEntityInstanceModel.Columns.LAST_UPDATED + " TEXT," +
            TrackedEntityInstanceModel.Columns.CREATED_AT_CLIENT + " TEXT," +
            TrackedEntityInstanceModel.Columns.LAST_UPDATED_AT_CLIENT + " TEXT," +
            TrackedEntityInstanceModel.Columns.ORGANISATION_UNIT + " TEXT NOT NULL," +
            TrackedEntityInstanceModel.Columns.TRACKED_ENTITY + " TEXT NOT NULL," +
            TrackedEntityInstanceModel.Columns.STATE + " TEXT," +
            " FOREIGN KEY (" + TrackedEntityInstanceModel.Columns.ORGANISATION_UNIT + ")" +
            " REFERENCES " + OrganisationUnitModel.TABLE + " (" + OrganisationUnitModel.Columns.UID
            + ")" +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED" +
            " FOREIGN KEY (" + TrackedEntityInstanceModel.Columns.TRACKED_ENTITY + ")" +
            " REFERENCES " + TrackedEntityModel.TABLE + " (" + TrackedEntityModel.Columns.UID + ")"
            +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED" +
            ");";

    private static final String CREATE_ENROLLMENT_TABLE =
            "CREATE TABLE " + EnrollmentModel.TABLE + " (" +
                    EnrollmentModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    EnrollmentModel.Columns.UID + " TEXT NOT NULL UNIQUE," +
                    EnrollmentModel.Columns.CREATED + " TEXT," +
                    EnrollmentModel.Columns.LAST_UPDATED + " TEXT," +
                    EnrollmentModel.Columns.CREATED_AT_CLIENT + " TEXT," +
                    EnrollmentModel.Columns.LAST_UPDATED_AT_CLIENT + " TEXT," +
                    EnrollmentModel.Columns.ORGANISATION_UNIT + " TEXT NOT NULL," +
                    EnrollmentModel.Columns.PROGRAM + " TEXT NOT NULL," +
                    EnrollmentModel.Columns.DATE_OF_ENROLLMENT + " TEXT," +
                    EnrollmentModel.Columns.DATE_OF_INCIDENT + " TEXT," +
                    EnrollmentModel.Columns.FOLLOW_UP + " INTEGER," +
                    EnrollmentModel.Columns.ENROLLMENT_STATUS + " TEXT," +
                    EnrollmentModel.Columns.TRACKED_ENTITY_INSTANCE + " TEXT NOT NULL," +
                    EnrollmentModel.Columns.LATITUDE + " TEXT," +
                    EnrollmentModel.Columns.LONGITUDE + " TEXT," +
                    EnrollmentModel.Columns.STATE + " TEXT," +
                    " FOREIGN KEY (" + EnrollmentModel.Columns.ORGANISATION_UNIT + ")" +
                    " REFERENCES " + OrganisationUnitModel.TABLE +
                    " (" + OrganisationUnitModel.Columns.UID + ")" +
                    " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED," +
                    " FOREIGN KEY (" + EnrollmentModel.Columns.PROGRAM + ")" +
                    " REFERENCES " + ProgramModel.TABLE +
                    " (" + ProgramModel.Columns.UID + ")" +
                    " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED," +
                    " FOREIGN KEY (" + EnrollmentModel.Columns.TRACKED_ENTITY_INSTANCE + ")" +
                    " REFERENCES " + TrackedEntityInstanceModel.TABLE +
                    " (" + TrackedEntityInstanceModel.Columns.UID + ")" +
                    " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED" +
                    ");";

    private static final String CREATE_RESOURCE_TABLE =
            "CREATE TABLE " + ResourceModel.TABLE + " (" +
                    ResourceModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ResourceModel.Columns.RESOURCE_TYPE + " TEXT NOT NULL," +
                    ResourceModel.Columns.LAST_SYNCED + " TEXT" + ");";

    private static final String CREATE_ORGANISATION_UNIT_PROGRAM_LINK_TABLE = "CREATE TABLE " +
            OrganisationUnitProgramLinkModel.TABLE + " (" +
            OrganisationUnitProgramLinkModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            OrganisationUnitProgramLinkModel.Columns.ORGANISATION_UNIT + " TEXT NOT NULL," +
            OrganisationUnitProgramLinkModel.Columns.PROGRAM + " TEXT NOT NULL," +
            " FOREIGN KEY (" + OrganisationUnitProgramLinkModel.Columns.ORGANISATION_UNIT + ")" +
            " REFERENCES " + OrganisationUnitModel.TABLE + " (" + OrganisationUnitModel.Columns.UID
            + ")" +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED," +
            " FOREIGN KEY (" + OrganisationUnitProgramLinkModel.Columns.PROGRAM + ")" +
            " REFERENCES " + ProgramModel.TABLE + " (" + ProgramModel.Columns.UID + ")" +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED," +
            " UNIQUE (" + OrganisationUnitProgramLinkModel.Columns.ORGANISATION_UNIT + ", " +
            OrganisationUnitProgramLinkModel.Columns.PROGRAM + ")" +
            ");";

    private static final String CREATE_USER_ROLE_TABLE =
            "CREATE TABLE " + UserRoleModel.TABLE + " (" +
                    UserRoleModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    UserRoleModel.Columns.UID + " TEXT NOT NULL UNIQUE," +
                    UserRoleModel.Columns.CODE + " TEXT," +
                    UserRoleModel.Columns.NAME + " TEXT," +
                    UserRoleModel.Columns.DISPLAY_NAME + " TEXT," +
                    UserRoleModel.Columns.CREATED + " TEXT," +
                    UserRoleModel.Columns.LAST_UPDATED + " TEXT" +
                    ");";

    private static final String CREATE_USER_ROLE_PROGRAM_TABLE = "CREATE TABLE " +
            UserRoleProgramLinkModel.TABLE + " (" +
            UserRoleProgramLinkModel.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            UserRoleProgramLinkModel.Columns.USER_ROLE + " TEXT NOT NULL," +
            UserRoleProgramLinkModel.Columns.PROGRAM + " TEXT NOT NULL," +
            " FOREIGN KEY (" + UserRoleProgramLinkModel.Columns.USER_ROLE + ") " +
            " REFERENCES " + UserRoleModel.TABLE + " (" + UserRoleModel.Columns.UID + ")" +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED," +
            " FOREIGN KEY (" + UserRoleProgramLinkModel.Columns.PROGRAM + ") " +
            " REFERENCES " + ProgramModel.TABLE + " (" + ProgramModel.Columns.UID + ")" +
            " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED," +
            " UNIQUE (" + UserRoleProgramLinkModel.Columns.USER_ROLE + ", " +
            UserRoleProgramLinkModel.Columns.PROGRAM + ")" +
            ");";

    private static final String CREATE_PROGRAM_STAGE_SECTION_PROGRAM_INDICATOR_LINK_TABLE =
            "CREATE TABLE " +
                    ProgramStageSectionProgramIndicatorLinkModel.TABLE + " (" +
                    ProgramStageSectionProgramIndicatorLinkModel.Columns.ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ProgramStageSectionProgramIndicatorLinkModel.Columns.PROGRAM_STAGE_SECTION
                    + " TEXT NOT NULL," +
                    ProgramStageSectionProgramIndicatorLinkModel.Columns.PROGRAM_INDICATOR
                    + " TEXT NOT NULL," +
                    " FOREIGN KEY ("
                    + ProgramStageSectionProgramIndicatorLinkModel.Columns.PROGRAM_STAGE_SECTION
                    + ") " +
                    " REFERENCES " + ProgramStageSectionModel.TABLE + " ("
                    + ProgramStageModel.Columns.UID + ")" +
                    " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED," +
                    " FOREIGN KEY ("
                    + ProgramStageSectionProgramIndicatorLinkModel.Columns.PROGRAM_INDICATOR + ") "
                    +
                    " REFERENCES " + ProgramIndicatorModel.TABLE + " ("
                    + ProgramIndicatorModel.Columns.UID + ")" +
                    " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, " +
                    " UNIQUE ("
                    + ProgramStageSectionProgramIndicatorLinkModel.Columns.PROGRAM_STAGE_SECTION
                    + ", " +
                    ProgramStageSectionProgramIndicatorLinkModel.Columns.PROGRAM_INDICATOR + ")" +
                    ");";

    /**
     * This method should be used only for testing purposes
     */
    // ToDo: Revise usage of this method
    @VisibleForTesting
    static SQLiteDatabase create() {
        return create(SQLiteDatabase.create(null));
    }


    private static SQLiteDatabase create(SQLiteDatabase database) {
        database.execSQL(CREATE_CONFIGURATION_TABLE);
        database.execSQL(CREATE_USER_TABLE);
        database.execSQL(CREATE_USER_CREDENTIALS_TABLE);
        database.execSQL(CREATE_ORGANISATION_UNIT_TABLE);
        database.execSQL(CREATE_USER_ORGANISATION_UNIT_TABLE);
        database.execSQL(CREATE_AUTHENTICATED_USER_TABLE);
        database.execSQL(CREATE_OPTION_SET_TABLE);
        database.execSQL(CREATE_OPTION_TABLE);
        database.execSQL(CREATE_PROGRAM_TABLE);
        database.execSQL(CREATE_TRACKED_ENTITY_TABLE);
        database.execSQL(CREATE_DATA_ELEMENT_TABLE);
        database.execSQL(CREATE_PROGRAM_STAGE_DATA_ELEMENT_TABLE);
        database.execSQL(CREATE_RELATIONSHIP_TABLE);
        database.execSQL(CREATE_RELATIONSHIP_TYPE_TABLE);
        database.execSQL(CREATE_PROGRAM_STAGE_SECTION_TABLE);
        database.execSQL(CREATE_PROGRAM_STAGE_TABLE);
        database.execSQL(CREATE_PROGRAM_RULE_VARIABLE_TABLE);
        database.execSQL(CREATE_TRACKED_ENTITY_ATTRIBUTE_TABLE);
        database.execSQL(CREATE_PROGRAM_TRACKED_ENTITY_ATTRIBUTE_TABLE);
        database.execSQL(CREATE_CONSTANT_TABLE);
        database.execSQL(CREATE_SYSTEM_INFO_TABLE);
        database.execSQL(CREATE_PROGRAM_RULE_TABLE);
        database.execSQL(CREATE_PROGRAM_INDICATOR_TABLE);
        database.execSQL(CREATE_PROGRAM_RULE_ACTION_TABLE);
        database.execSQL(CREATE_TRACKED_ENTITY_DATA_VALUE_TABLE);
        database.execSQL(CREATE_TRACKED_ENTITY_ATTRIBUTE_VALUE_TABLE);
        database.execSQL(CREATE_EVENT_TABLE);
        database.execSQL(CREATE_TRACKED_ENTITY_INSTANCE_TABLE);
        database.execSQL(CREATE_ENROLLMENT_TABLE);
        database.execSQL(CREATE_RESOURCE_TABLE);
        database.execSQL(CREATE_ORGANISATION_UNIT_PROGRAM_LINK_TABLE);
        database.execSQL(CREATE_USER_ROLE_TABLE);
        database.execSQL(CREATE_USER_ROLE_PROGRAM_TABLE);
        database.execSQL(CREATE_PROGRAM_STAGE_SECTION_PROGRAM_INDICATOR_LINK_TABLE);
        database.execSQL(CREATE_CATEGORY_TABLE);
        database.execSQL(CREATE_CATEGORY_OPTION_TABLE);
        database.execSQL(CREATE_CATEGORY_CATEGORY_OPTION_LINK_TABLE);
        database.execSQL(CREATE_CATEGORY_COMBO_TABLE);
        database.execSQL(CREATE_CATEGORY_CATEGORY_COMBO_LINK_TABLE);
        database.execSQL(CREATE_CATEGORY_OPTION_COMBO_TABLE);
        database.execSQL(CREATE_CATEGORY_OPTION_COMBO_CATEGORY_LINK_TABLE);
        return database;
    }

    public DbOpenHelper(Context context, String databaseName, int version, String migrationTestDir,
            String sqlDatabase) {
        super(context, databaseName, version, true, migrationTestDir);
        mockedSqlDatabase = sqlDatabase;
    }

    public DbOpenHelper(@NonNull Context context, @Nullable String databaseName) {
        super(context, databaseName, VERSION);
    }

    public DbOpenHelper(Context context, String databaseName, int testVersion) {
        super(context, databaseName, testVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String emptySqlFile = "";
        if (emptySqlFile.equals(mockedSqlDatabase)) {
            create(db);
        } else {
            populateDBfromResource(db, mockedSqlDatabase);
        }
        super.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        // enable foreign key support in database
        db.execSQL("PRAGMA foreign_keys = ON;");
    }

    private SQLiteDatabase populateDBfromResource(SQLiteDatabase database, String databaseSqlFile) {
        String emptySqlFile = "";
        if (emptySqlFile.equals(databaseSqlFile)) {
            return database;
        }
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(databaseSqlFile);
        InputStreamReader inputStreamReader = new InputStreamReader(in, Charset.forName("UTF-8"));
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String line = null;
        try {
            line = reader.readLine();
            while (line != null) {
                database.execSQL(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            Log.e(getClass().getName(), e.getMessage());
        } finally {
            try {
                reader.close();
                inputStreamReader.close();
                in.close();
            } catch (IOException e) {
                Log.e(getClass().getName(), e.getMessage());
            }
        }
        return database;
    }
}
